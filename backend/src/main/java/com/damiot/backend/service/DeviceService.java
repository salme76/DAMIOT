package com.damiot.backend.service;

import com.damiot.backend.model.Device;
import com.damiot.backend.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar dispositivos ESP32
 * 
 * Proporciona métodos para:
 * - Consultar dispositivos
 * - Actualizar estado de conexión (heartbeat)
 * - Habilitar/deshabilitar dispositivos
 * - Detectar dispositivos inactivos
 * 
 * Resiliencia: Maneja errores de BD sin crashear la aplicación
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private volatile boolean databaseAvailable = true;

    /**
     * Obtiene todos los dispositivos habilitados
     */
    public List<Device> getAllEnabledDevices() {
        try {
            List<Device> devices = deviceRepository.findByIsEnabledTrue();
            markDatabaseAvailable();
            return devices;
        } catch (Exception e) {
            handleDatabaseError("obtener dispositivos habilitados", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene todos los dispositivos (habilitados y deshabilitados)
     */
    public List<Device> getAllDevices() {
        try {
            List<Device> devices = deviceRepository.findAll();
            markDatabaseAvailable();
            return devices;
        } catch (Exception e) {
            handleDatabaseError("obtener todos los dispositivos", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene un dispositivo por su ID
     */
    public Optional<Device> getDeviceById(Long id) {
        try {
            Optional<Device> device = deviceRepository.findById(id);
            markDatabaseAvailable();
            return device;
        } catch (Exception e) {
            handleDatabaseError("obtener dispositivo por ID", e);
            return Optional.empty();
        }
    }

    /**
     * Obtiene un dispositivo por su MAC address
     */
    public Optional<Device> getDeviceByMacAddress(String macAddress) {
        try {
            Optional<Device> device = deviceRepository.findByMacAddress(macAddress);
            markDatabaseAvailable();
            return device;
        } catch (Exception e) {
            handleDatabaseError("obtener dispositivo por MAC", e);
            return Optional.empty();
        }
    }

    /**
     * Actualiza el estado de conexión de un dispositivo (heartbeat)
     * 
     * @param macAddress Dirección MAC del dispositivo
     * @param ipAddress Dirección IP actual del dispositivo
     */
    @Transactional
    public void updateDeviceHeartbeat(String macAddress, String ipAddress) {
        try {
            Optional<Device> deviceOpt = deviceRepository.findByMacAddress(macAddress);
            
            if (deviceOpt.isPresent()) {
                Device device = deviceOpt.get();
                device.markAsOnline(ipAddress);
                deviceRepository.save(device);
                log.debug("💓 Heartbeat recibido de: {} ({})", device.getName(), macAddress);
            } else {
                // Auto-registro de dispositivos nuevos (opcional)
                log.warn("💓 Heartbeat de dispositivo desconocido: {} (IP: {})", macAddress, ipAddress);
            }
            markDatabaseAvailable();
        } catch (Exception e) {
            handleDatabaseError("actualizar heartbeat", e);
        }
    }

    /**
     * Marca un dispositivo como offline por su MAC address
     * Usado cuando se recibe LWT del broker MQTT
     */
    @Transactional
    public void markDeviceOfflineByMac(String macAddress) {
        try {
            Optional<Device> deviceOpt = deviceRepository.findByMacAddress(macAddress);
            
            if (deviceOpt.isPresent()) {
                Device device = deviceOpt.get();
                device.markAsOffline();
                deviceRepository.save(device);
                log.info("📴 Dispositivo marcado como offline por LWT: {}", device.getName());
            }
            markDatabaseAvailable();
        } catch (Exception e) {
            handleDatabaseError("marcar dispositivo offline por MAC", e);
        }
    }

    /**
     * Verifica si un dispositivo está offline por su MAC address
     * @return true si el dispositivo está offline o no existe
     */
    public boolean isDeviceOfflineByMac(String macAddress) {
        try {
            Optional<Device> deviceOpt = deviceRepository.findByMacAddress(macAddress);
            if (deviceOpt.isPresent()) {
                return !deviceOpt.get().isOnline();
            }
            return true; // Si no existe, considerarlo offline
        } catch (Exception e) {
            handleDatabaseError("verificar estado offline por MAC", e);
            return true;
        }
    }

    /**
     * Marca un dispositivo como offline
     */
    @Transactional
    public void markDeviceOffline(Long deviceId) {
        try {
            Optional<Device> deviceOpt = deviceRepository.findById(deviceId);
            
            if (deviceOpt.isPresent()) {
                Device device = deviceOpt.get();
                device.markAsOffline();
                deviceRepository.save(device);
                log.info("📴 Dispositivo marcado como offline: {}", device.getName());
            }
            markDatabaseAvailable();
        } catch (Exception e) {
            handleDatabaseError("marcar dispositivo offline", e);
        }
    }

    /**
     * Habilita o deshabilita un dispositivo
     * 
     * @param deviceId ID del dispositivo
     * @param enabled true para habilitar, false para deshabilitar
     * @return Device actualizado o null si hay error
     */
    @Transactional
    public Device toggleDevice(Long deviceId, boolean enabled) {
        try {
            Optional<Device> deviceOpt = deviceRepository.findById(deviceId);
            
            if (deviceOpt.isEmpty()) {
                log.warn("Dispositivo no encontrado con ID: {}", deviceId);
                return null;
            }
            
            Device device = deviceOpt.get();
            device.setIsEnabled(enabled);
            Device saved = deviceRepository.save(device);
            
            log.info("📱 Dispositivo {} {}", device.getName(), enabled ? "habilitado" : "deshabilitado");
            markDatabaseAvailable();
            return saved;
            
        } catch (Exception e) {
            handleDatabaseError("cambiar estado del dispositivo", e);
            return null;
        }
    }

    /**
     * Verifica dispositivos inactivos y los marca como offline
     * Se ejecuta periódicamente para detectar dispositivos que dejaron de enviar heartbeat
     * 
     * Umbral: 30 segundos sin heartbeat = offline (optimizado para demo)
     * 
     * NOTA: NO tiene @Transactional para poder manejar errores de BD internamente
     */
    public void checkInactiveDevices() {
        // Si ya sabemos que la BD no está disponible, no intentar
        if (!databaseAvailable) {
            return;
        }
        
        try {
            List<Device> allDevices = deviceRepository.findAll();
            LocalDateTime threshold = LocalDateTime.now().minusSeconds(30);
            
            int markedOffline = 0;
            for (Device device : allDevices) {
                if (device.isOnline() && device.getLastConnection() != null 
                    && device.getLastConnection().isBefore(threshold)) {
                    try {
                        device.markAsOffline();
                        deviceRepository.save(device);
                        log.warn("📴 Dispositivo {} marcado como offline por inactividad", device.getName());
                        markedOffline++;
                    } catch (Exception e) {
                        log.debug("No se pudo marcar dispositivo offline: {}", e.getMessage());
                    }
                }
            }
            
            if (markedOffline > 0) {
                log.info("🔍 Verificación: {} dispositivos marcados offline", markedOffline);
            }
            markDatabaseAvailable();
            
        } catch (Exception e) {
            handleDatabaseError("verificar dispositivos inactivos", e);
        }
    }

    /**
     * Verifica si la base de datos está disponible
     */
    public boolean isDatabaseAvailable() {
        return databaseAvailable;
    }

    /**
     * Marca la base de datos como disponible
     */
    private void markDatabaseAvailable() {
        if (!databaseAvailable) {
            databaseAvailable = true;
            log.info("✅ Conexión a base de datos restaurada");
        }
    }

    /**
     * Maneja errores de acceso a base de datos
     */
    private void handleDatabaseError(String operation, Exception e) {
        if (databaseAvailable) {
            databaseAvailable = false;
            log.error("❌ Error de base de datos al {}: {}", operation, e.getMessage());
            log.warn("⚠️ La base de datos no está disponible. El sistema continuará con capacidad limitada.");
        }
        // En modo silencioso después del primer error
    }
}
