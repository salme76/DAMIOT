package com.damiot.backend.service;

import com.damiot.backend.model.ActuatorEvent;
import com.damiot.backend.model.ActuatorState;
import com.damiot.backend.model.Device;
import com.damiot.backend.repository.ActuatorEventRepository;
import com.damiot.backend.repository.ActuatorStateRepository;
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
 * Servicio para gestionar actuadores y sus estados
 * 
 * Proporciona métodos para:
 * - Obtener estados de actuadores
 * - Enviar comandos via MQTT (con soporte multi-dispositivo)
 * - Registrar eventos de actuadores
 * 
 * Resiliencia: Maneja errores de BD y MQTT sin crashear
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActuatorService {

    private final ActuatorStateRepository actuatorStateRepository;
    private final ActuatorEventRepository actuatorEventRepository;
    private final MqttService mqttService;
    private final DeviceService deviceService;

    /**
     * Obtiene todos los estados de actuadores de un dispositivo
     * 
     * @param deviceId ID del dispositivo
     * @return Lista de estados de actuadores
     */
    public List<ActuatorState> getActuatorStatesByDevice(Long deviceId) {
        try {
            return actuatorStateRepository.findByDeviceId(deviceId);
        } catch (DataAccessException e) {
            log.error("Error al obtener actuadores del dispositivo {}: {}", deviceId, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Envía un comando a un actuador
     * 
     * Obtiene la MAC del dispositivo y construye el topic MQTT apropiado:
     * damiot/actuadores/{MAC}/{actuatorType}
     * 
     * @param deviceId ID del dispositivo
     * @param actuatorType Tipo de actuador (led_azul, bomba_riego, etc.)
     * @param command Comando a enviar (ON, OFF, etc.)
     * @return Estado actualizado del actuador
     */
    @Transactional
    public ActuatorState sendActuatorCommand(Long deviceId, String actuatorType, String command) {
        try {
            // Obtener el dispositivo para conocer su MAC
            Optional<Device> deviceOpt = deviceService.getDeviceById(deviceId);
            
            if (deviceOpt.isEmpty()) {
                log.error("❌ Dispositivo no encontrado con ID: {}", deviceId);
                throw new IllegalArgumentException("Dispositivo no encontrado: " + deviceId);
            }
            
            Device device = deviceOpt.get();
            String macAddress = device.getMacAddress();
            
            // Enviar comando via MQTT con la MAC del dispositivo
            mqttService.sendActuatorCommand(macAddress, actuatorType, command);
            
            // Buscar o crear estado del actuador
            Optional<ActuatorState> existingState = 
                actuatorStateRepository.findByDeviceIdAndActuatorType(deviceId, actuatorType);
            
            ActuatorState state;
            if (existingState.isPresent()) {
                state = existingState.get();
                state.setState(command);
            } else {
                state = new ActuatorState();
                state.setDeviceId(deviceId);
                state.setActuatorType(actuatorType);
                state.setState(command);
            }
            
            state.setUpdatedAt(LocalDateTime.now());
            ActuatorState saved = actuatorStateRepository.save(state);
            log.info("🎮 Comando enviado: {} -> {} (Device: {}, MAC: {})", 
                    actuatorType, command, deviceId, macAddress);
            
            return saved;
            
        } catch (DataAccessException e) {
            log.error("Error de BD al enviar comando de actuador: {}", e.getMessage());
            
            // Aún así intentar enviar por MQTT si conocemos la MAC
            try {
                Optional<Device> deviceOpt = deviceService.getDeviceById(deviceId);
                if (deviceOpt.isPresent()) {
                    mqttService.sendActuatorCommand(deviceOpt.get().getMacAddress(), actuatorType, command);
                }
            } catch (Exception mqttEx) {
                log.error("Error al enviar comando MQTT después de fallo de BD: {}", mqttEx.getMessage());
            }
            
            // Devolver un estado temporal
            ActuatorState tempState = new ActuatorState();
            tempState.setId(0L);
            tempState.setDeviceId(deviceId);
            tempState.setActuatorType(actuatorType);
            tempState.setState(command);
            tempState.setUpdatedAt(LocalDateTime.now());
            return tempState;
        }
    }

    /**
     * Actualiza el estado de un actuador (usado cuando el ESP32 confirma)
     */
    @Transactional
    public void updateActuatorState(Long deviceId, String actuatorType, String state) {
        try {
            Optional<ActuatorState> existingState = 
                actuatorStateRepository.findByDeviceIdAndActuatorType(deviceId, actuatorType);
            
            if (existingState.isPresent()) {
                ActuatorState actuator = existingState.get();
                actuator.setState(state);
                actuator.setUpdatedAt(LocalDateTime.now());
                actuatorStateRepository.save(actuator);
                log.debug("✅ Estado de actuador actualizado: {} -> {} (Device: {})", 
                        actuatorType, state, deviceId);
            } else {
                // Crear nuevo estado si no existe
                ActuatorState newState = new ActuatorState();
                newState.setDeviceId(deviceId);
                newState.setActuatorType(actuatorType);
                newState.setState(state);
                newState.setUpdatedAt(LocalDateTime.now());
                actuatorStateRepository.save(newState);
                log.info("➕ Nuevo estado de actuador creado: {} -> {} (Device: {})", 
                        actuatorType, state, deviceId);
            }
        } catch (DataAccessException e) {
            log.error("Error al actualizar estado de actuador: {}", e.getMessage());
        }
    }

    /**
     * Crea un evento de actuador (registro de comando enviado)
     */
    @Transactional
    public ActuatorEvent createActuatorEvent(String actuatorType, String command, Long deviceId) {
        try {
            ActuatorEvent event = new ActuatorEvent();
            event.setActuatorType(actuatorType);
            event.setCommand(command);
            event.setDeviceId(deviceId);
            event.setStatus("SENT");
            event.setTimestamp(LocalDateTime.now());
            
            ActuatorEvent saved = actuatorEventRepository.save(event);
            log.debug("📝 Evento de actuador creado: {} -> {} (Device: {}, Event ID: {})", 
                    actuatorType, command, deviceId, saved.getId());
            
            return saved;
            
        } catch (DataAccessException e) {
            log.error("Error al crear evento de actuador: {}", e.getMessage());
            // Devolver un evento temporal sin persistir
            ActuatorEvent tempEvent = new ActuatorEvent();
            tempEvent.setId(0L);
            tempEvent.setActuatorType(actuatorType);
            tempEvent.setCommand(command);
            tempEvent.setDeviceId(deviceId);
            tempEvent.setStatus("SENT");
            tempEvent.setTimestamp(LocalDateTime.now());
            return tempEvent;
        }
    }

    /**
     * Actualiza el estado de un evento
     */
    @Transactional
    public void updateEventStatus(Long eventId, String status, String response) {
        try {
            if (eventId == null || eventId == 0L) {
                return; // Evento temporal, no persistido
            }
            
            Optional<ActuatorEvent> eventOpt = actuatorEventRepository.findById(eventId);
            
            if (eventOpt.isPresent()) {
                ActuatorEvent event = eventOpt.get();
                event.setStatus(status);
                event.setResponse(response);
                actuatorEventRepository.save(event);
                log.debug("📝 Estado de evento actualizado: {} -> {}", eventId, status);
            }
        } catch (DataAccessException e) {
            log.error("Error al actualizar estado de evento: {}", e.getMessage());
        }
    }

    /**
     * Obtiene el historial de eventos de un tipo de actuador
     */
    public List<ActuatorEvent> getEventsByActuatorType(String actuatorType) {
        try {
            return actuatorEventRepository.findByActuatorTypeOrderByTimestampDesc(actuatorType);
        } catch (DataAccessException e) {
            log.error("Error al obtener historial de {}: {}", actuatorType, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene el último evento de un tipo de actuador
     */
    public Optional<ActuatorEvent> getLatestEvent(String actuatorType) {
        try {
            return actuatorEventRepository.findFirstByActuatorTypeOrderByTimestampDesc(actuatorType);
        } catch (DataAccessException e) {
            log.error("Error al obtener último evento de {}: {}", actuatorType, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Obtiene todos los eventos
     */
    public List<ActuatorEvent> getAllEvents() {
        try {
            return actuatorEventRepository.findAllByOrderByTimestampDesc();
        } catch (DataAccessException e) {
            log.error("Error al obtener todos los eventos: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene los últimos N eventos
     */
    public List<ActuatorEvent> getLatestEvents() {
        try {
            return actuatorEventRepository.findTop10ByOrderByTimestampDesc();
        } catch (DataAccessException e) {
            log.error("Error al obtener últimos eventos: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene eventos fallidos
     */
    public List<ActuatorEvent> getFailedEvents() {
        try {
            return actuatorEventRepository.findByStatusOrderByTimestampDesc("FAILED");
        } catch (DataAccessException e) {
            log.error("Error al obtener eventos fallidos: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
