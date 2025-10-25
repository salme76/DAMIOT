package com.damiot.backend.service;

import com.damiot.backend.model.DeviceStatus;
import com.damiot.backend.repository.DeviceStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceStatusRepository deviceStatusRepository;

    /**
     * Registrar o actualizar el estado de un dispositivo
     */
    @Transactional
    public DeviceStatus updateDeviceStatus(String deviceId, Boolean online, String ipAddress) {
        Optional<DeviceStatus> existingDevice = deviceStatusRepository.findByDeviceId(deviceId);

        DeviceStatus device;
        if (existingDevice.isPresent()) {
            device = existingDevice.get();
            device.setOnline(online);
            device.setLastSeen(LocalDateTime.now());
            if (ipAddress != null) {
                device.setIpAddress(ipAddress);
            }
            log.info("Actualizando estado del dispositivo: {} - Online: {}", deviceId, online);
        } else {
            device = new DeviceStatus();
            device.setDeviceId(deviceId);
            device.setOnline(online);
            device.setLastSeen(LocalDateTime.now());
            device.setIpAddress(ipAddress);
            device.setConnectedSince(LocalDateTime.now());
            log.info("Registrando nuevo dispositivo: {}", deviceId);
        }

        return deviceStatusRepository.save(device);
    }

    /**
     * Marcar dispositivo como online
     */
    @Transactional
    public DeviceStatus markDeviceOnline(String deviceId, String ipAddress) {
        log.info("Dispositivo {} conectado desde {}", deviceId, ipAddress);
        return updateDeviceStatus(deviceId, true, ipAddress);
    }

    /**
     * Marcar dispositivo como offline
     */
    @Transactional
    public DeviceStatus markDeviceOffline(String deviceId) {
        log.warn("Dispositivo {} desconectado", deviceId);
        return updateDeviceStatus(deviceId, false, null);
    }

    /**
     * Actualizar heartbeat del dispositivo
     */
    @Transactional
    public void updateHeartbeat(String deviceId) {
        Optional<DeviceStatus> deviceOpt = deviceStatusRepository.findByDeviceId(deviceId);

        if (deviceOpt.isPresent()) {
            DeviceStatus device = deviceOpt.get();
            device.setLastSeen(LocalDateTime.now());
            if (!device.getOnline()) {
                device.setOnline(true);
                log.info("Dispositivo {} vuelto a conectar", deviceId);
            }
            deviceStatusRepository.save(device);
        } else {
            log.warn("Heartbeat recibido de dispositivo desconocido: {}", deviceId);
            markDeviceOnline(deviceId, null);
        }
    }

    /**
     * Obtener estado de un dispositivo
     */
    public Optional<DeviceStatus> getDeviceStatus(String deviceId) {
        log.debug("Consultando estado del dispositivo: {}", deviceId);
        return deviceStatusRepository.findByDeviceId(deviceId);
    }

    /**
     * Obtener todos los dispositivos online
     */
    public List<DeviceStatus> getOnlineDevices() {
        log.debug("Obteniendo dispositivos online");
        return deviceStatusRepository.findByOnline(true);
    }

    /**
     * Obtener todos los dispositivos offline
     */
    public List<DeviceStatus> getOfflineDevices() {
        log.debug("Obteniendo dispositivos offline");
        return deviceStatusRepository.findByOnline(false);
    }

    /**
     * Obtener todos los dispositivos
     */
    public List<DeviceStatus> getAllDevices() {
        log.debug("Obteniendo todos los dispositivos");
        return deviceStatusRepository.findAll();
    }

    /**
     * Verificar si un dispositivo existe
     */
    public boolean deviceExists(String deviceId) {
        return deviceStatusRepository.existsByDeviceId(deviceId);
    }

    /**
     * Verificar dispositivos inactivos (sin señal hace tiempo)
     */
    @Transactional
    public void checkInactiveDevices(int minutesThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutesThreshold);
        List<DeviceStatus> inactiveDevices = deviceStatusRepository.findByLastSeenBefore(threshold);

        for (DeviceStatus device : inactiveDevices) {
            if (device.getOnline()) {
                log.warn("Dispositivo {} sin señal desde {}, marcando como offline",
                        device.getDeviceId(), device.getLastSeen());
                device.setOnline(false);
                deviceStatusRepository.save(device);
            }
        }
    }

    /**
     * Actualizar versión de firmware
     */
    @Transactional
    public Optional<DeviceStatus> updateFirmwareVersion(String deviceId, String firmwareVersion) {
        Optional<DeviceStatus> deviceOpt = deviceStatusRepository.findByDeviceId(deviceId);

        if (deviceOpt.isPresent()) {
            DeviceStatus device = deviceOpt.get();
            device.setFirmwareVersion(firmwareVersion);
            log.info("Actualizando firmware del dispositivo {} a versión {}", deviceId, firmwareVersion);
            return Optional.of(deviceStatusRepository.save(device));
        }

        return Optional.empty();
    }
}
