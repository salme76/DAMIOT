package com.damiot.backend.controller;

import com.damiot.backend.model.ActuatorEvent;
import com.damiot.backend.model.ActuatorState;
import com.damiot.backend.service.ActuatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar actuadores
 * 
 * Endpoints principales:
 * - GET /api/actuators/device/{deviceId} - Estados de actuadores de un dispositivo (Android)
 * - POST /api/actuators/command - Enviar comando a un actuador (Android)
 * - GET /api/actuators/history - Historial de eventos
 * - GET /api/actuators/latest - Últimos eventos
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@RestController
@RequestMapping("/api/actuators")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ActuatorController {

    private final ActuatorService actuatorService;

    /**
     * GET /api/actuators/device/{deviceId}
     * Obtiene todos los actuadores de un dispositivo
     * 
     * REQUERIDO POR ANDROID: DamiotApi.getActuatorStates()
     * 
     * @param deviceId ID del dispositivo
     * @return Lista de estados de actuadores
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<ActuatorState>> getActuatorsByDevice(
            @PathVariable Long deviceId) {
        log.info("GET /api/actuators/device/{}", deviceId);
        
        try {
            List<ActuatorState> states = actuatorService.getActuatorStatesByDevice(deviceId);
            return ResponseEntity.ok(states);
        } catch (Exception e) {
            log.error("Error al obtener actuadores del dispositivo {}: {}", deviceId, e.getMessage());
            return ResponseEntity.ok(List.of()); // Devolver lista vacía en lugar de error
        }
    }

    /**
     * POST /api/actuators/command
     * Envía un comando a un actuador
     * 
     * REQUERIDO POR ANDROID: DamiotApi.sendActuatorCommand()
     * Body: {"deviceId": 1, "actuatorType": "led_azul", "command": "ON"}
     * 
     * @param command Objeto con deviceId, actuatorType y command
     * @return Estado actualizado del actuador
     */
    @PostMapping("/command")
    public ResponseEntity<ActuatorState> sendCommand(
            @RequestBody Map<String, Object> command) {
        log.info("POST /api/actuators/command - {}", command);
        
        try {
            // Extraer parámetros del body
            Long deviceId = ((Number) command.get("deviceId")).longValue();
            String actuatorType = (String) command.get("actuatorType");
            String commandValue = (String) command.get("command");
            
            if (deviceId == null || actuatorType == null || commandValue == null) {
                log.warn("Parámetros incompletos en comando de actuador");
                return ResponseEntity.badRequest().build();
            }
            
            // Enviar comando y actualizar estado
            ActuatorState state = actuatorService.sendActuatorCommand(deviceId, actuatorType, commandValue);
            
            // También registrar como evento para historial
            actuatorService.createActuatorEvent(actuatorType, commandValue, deviceId);
            
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            log.error("Error al enviar comando de actuador: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/actuators/history/{actuatorType}
     * Obtener historial de comandos de un actuador
     */
    @GetMapping("/history/{actuatorType}")
    public ResponseEntity<List<ActuatorEvent>> getActuatorHistory(
            @PathVariable String actuatorType) {
        log.info("GET /api/actuators/history/{}", actuatorType);

        try {
            List<ActuatorEvent> events = actuatorService.getEventsByActuatorType(actuatorType);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Error al obtener historial de {}: {}", actuatorType, e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * GET /api/actuators/latest/{actuatorType}
     * Obtener el último evento de un actuador
     */
    @GetMapping("/latest/{actuatorType}")
    public ResponseEntity<ActuatorEvent> getLatestEvent(
            @PathVariable String actuatorType) {
        log.info("GET /api/actuators/latest/{}", actuatorType);

        try {
            return actuatorService.getLatestEvent(actuatorType)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener último evento de {}: {}", actuatorType, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/actuators/history
     * Obtener todo el historial de eventos
     */
    @GetMapping("/history")
    public ResponseEntity<List<ActuatorEvent>> getAllHistory() {
        log.info("GET /api/actuators/history");

        try {
            List<ActuatorEvent> events = actuatorService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Error al obtener historial de eventos: {}", e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * GET /api/actuators/latest
     * Obtener los últimos 10 eventos
     */
    @GetMapping("/latest")
    public ResponseEntity<List<ActuatorEvent>> getLatestEvents() {
        log.info("GET /api/actuators/latest");

        try {
            List<ActuatorEvent> events = actuatorService.getLatestEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Error al obtener últimos eventos: {}", e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * GET /api/actuators/failed
     * Obtener eventos fallidos
     */
    @GetMapping("/failed")
    public ResponseEntity<List<ActuatorEvent>> getFailedEvents() {
        log.info("GET /api/actuators/failed");

        try {
            List<ActuatorEvent> events = actuatorService.getFailedEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Error al obtener eventos fallidos: {}", e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }
}
