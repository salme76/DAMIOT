package com.damiot.backend.service;

import com.damiot.backend.model.ActuatorEvent;
import com.damiot.backend.repository.ActuatorEventRepository;
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
public class ActuatorService {

    private final ActuatorEventRepository actuatorEventRepository;

    /**
     * Registrar un nuevo comando de actuador
     */
    @Transactional
    public ActuatorEvent saveActuatorEvent(ActuatorEvent event) {
        log.info("Registrando evento de actuador: {} -> {}",
                event.getActuatorType(), event.getCommand());
        return actuatorEventRepository.save(event);
    }

    /**
     * Crear y guardar un evento de actuador
     */
    @Transactional
    public ActuatorEvent createActuatorEvent(String actuatorType, String command, String deviceId) {
        ActuatorEvent event = new ActuatorEvent();
        event.setActuatorType(actuatorType);
        event.setCommand(command);
        event.setDeviceId(deviceId);
        event.setStatus("SENT");
        event.setTimestamp(LocalDateTime.now());

        return saveActuatorEvent(event);
    }

    /**
     * Actualizar el estado de un evento
     */
    @Transactional
    public Optional<ActuatorEvent> updateEventStatus(Long eventId, String status, String response) {
        Optional<ActuatorEvent> eventOpt = actuatorEventRepository.findById(eventId);

        if (eventOpt.isPresent()) {
            ActuatorEvent event = eventOpt.get();
            event.setStatus(status);
            event.setResponse(response);
            log.info("Actualizando estado del evento {} a: {}", eventId, status);
            return Optional.of(actuatorEventRepository.save(event));
        }

        log.warn("Evento no encontrado: {}", eventId);
        return Optional.empty();
    }

    /**
     * Obtener el último evento de un actuador
     */
    public Optional<ActuatorEvent> getLatestEvent(String actuatorType) {
        log.debug("Obteniendo último evento de: {}", actuatorType);
        return actuatorEventRepository.findFirstByActuatorTypeOrderByTimestampDesc(actuatorType);
    }

    /**
     * Obtener eventos por tipo de actuador
     */
    public List<ActuatorEvent> getEventsByActuatorType(String actuatorType) {
        log.debug("Obteniendo eventos de actuador: {}", actuatorType);
        return actuatorEventRepository.findByActuatorType(actuatorType);
    }

    /**
     * Obtener eventos por dispositivo
     */
    public List<ActuatorEvent> getEventsByDevice(String deviceId) {
        log.debug("Obteniendo eventos del dispositivo: {}", deviceId);
        return actuatorEventRepository.findByDeviceId(deviceId);
    }

    /**
     * Obtener eventos por estado
     */
    public List<ActuatorEvent> getEventsByStatus(String status) {
        log.debug("Obteniendo eventos con estado: {}", status);
        return actuatorEventRepository.findByStatus(status);
    }

    /**
     * Obtener historial entre fechas
     */
    public List<ActuatorEvent> getEventsBetween(LocalDateTime start, LocalDateTime end) {
        log.debug("Obteniendo eventos entre {} y {}", start, end);
        return actuatorEventRepository.findByTimestampBetween(start, end);
    }

    /**
     * Obtener últimos eventos
     */
    public List<ActuatorEvent> getLatestEvents() {
        log.debug("Obteniendo últimos eventos");
        return actuatorEventRepository.findTop10ByOrderByTimestampDesc();
    }

    /**
     * Obtener todos los eventos
     */
    public List<ActuatorEvent> getAllEvents() {
        log.debug("Obteniendo todos los eventos");
        return actuatorEventRepository.findAll();
    }

    /**
     * Obtener eventos fallidos
     */
    public List<ActuatorEvent> getFailedEvents() {
        log.debug("Obteniendo eventos fallidos");
        return actuatorEventRepository.findByStatusOrderByTimestampDesc("FAILED");
    }
}