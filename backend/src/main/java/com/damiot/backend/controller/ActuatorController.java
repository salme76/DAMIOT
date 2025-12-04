package com.damiot.backend.controller;

import com.damiot.backend.model.ActuatorEvent;
import com.damiot.backend.model.ActuatorState;
import com.damiot.backend.model.Device;
import com.damiot.backend.service.ActuatorService;
import com.damiot.backend.service.DeviceService;
import com.damiot.backend.service.MqttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestionar actuadores
 * 
 * Endpoints principales:
 * - GET /api/actuators/device/{deviceId} - Estados de actuadores de un dispositivo (Android)
 * - POST /api/actuators/command - Enviar comando a un actuador (Android)
 * - POST /api/actuators/led - Control de LED (endpoints legacy)
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
    private final MqttService mqttService;
    private final DeviceService deviceService;
    
    // Device ID por defecto para endpoints legacy
    private static final Long DEFAULT_DEVICE_ID = 1L;

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
     * POST /api/actuators/led
     * Controlar el LED del ESP32
     * Body: {"command": "ON"} o {"command": "OFF"}
     * 
     * Endpoint legacy que usa el dispositivo por defecto (ID=1)
     */
    @PostMapping("/led")
    public ResponseEntity<Map<String, Object>> controlLed(
            @RequestBody Map<String, String> request) {
        String command = request.get("command");
        log.info("POST /api/actuators/led - Command: {}", command);

        try {
            if (command == null || (!command.equals("ON") && !command.equals("OFF"))) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Command must be 'ON' or 'OFF'"));
            }

            // Obtener dispositivo por defecto
            Optional<Device> deviceOpt = deviceService.getDeviceById(DEFAULT_DEVICE_ID);
            if (deviceOpt.isEmpty()) {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Dispositivo por defecto no encontrado"));
            }
            
            Device device = deviceOpt.get();
            String macAddress = device.getMacAddress();

            // Registrar evento en BD
            ActuatorEvent event = actuatorService.createActuatorEvent("led_azul", command, DEFAULT_DEVICE_ID);

            // Enviar comando MQTT con MAC
            mqttService.sendLedCommand(macAddress, command);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "actuator", "led_azul",
                    "command", command,
                    "eventId", event.getId(),
                    "deviceId", DEFAULT_DEVICE_ID,
                    "message", "Comando enviado al LED"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al controlar LED: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "Error al enviar comando: " + e.getMessage()
            ));
        }
    }

    /**
     * POST /api/actuators/motor
     * Controlar el motor del ESP32
     * Body: {"command": "START"} o {"command": "STOP"} o {"command": "SPEED_50"}
     * 
     * Endpoint legacy que usa el dispositivo por defecto (ID=1)
     */
    @PostMapping("/motor")
    public ResponseEntity<Map<String, Object>> controlMotor(
            @RequestBody Map<String, String> request) {
        String command = request.get("command");
        log.info("POST /api/actuators/motor - Command: {}", command);

        try {
            if (command == null || command.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Command is required"));
            }

            // Obtener dispositivo por defecto
            Optional<Device> deviceOpt = deviceService.getDeviceById(DEFAULT_DEVICE_ID);
            if (deviceOpt.isEmpty()) {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Dispositivo por defecto no encontrado"));
            }
            
            Device device = deviceOpt.get();
            String macAddress = device.getMacAddress();

            // Registrar evento en BD
            ActuatorEvent event = actuatorService.createActuatorEvent("motor", command, DEFAULT_DEVICE_ID);

            // Enviar comando MQTT con MAC
            mqttService.sendMotorCommand(macAddress, command);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "actuator", "motor",
                    "command", command,
                    "eventId", event.getId(),
                    "deviceId", DEFAULT_DEVICE_ID,
                    "message", "Comando enviado al motor"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al controlar motor: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "Error al enviar comando: " + e.getMessage()
            ));
        }
    }

    /**
     * POST /api/actuators/{actuatorType}
     * Endpoint genérico para cualquier actuador
     * 
     * Endpoint legacy que usa el dispositivo por defecto (ID=1)
     */
    @PostMapping("/{actuatorType}")
    public ResponseEntity<Map<String, Object>> controlActuator(
            @PathVariable String actuatorType,
            @RequestBody Map<String, String> request) {
        String command = request.get("command");
        log.info("POST /api/actuators/{} - Command: {}", actuatorType, command);

        try {
            if (command == null || command.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Command is required"));
            }

            // Obtener dispositivo por defecto
            Optional<Device> deviceOpt = deviceService.getDeviceById(DEFAULT_DEVICE_ID);
            if (deviceOpt.isEmpty()) {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Dispositivo por defecto no encontrado"));
            }
            
            Device device = deviceOpt.get();
            String macAddress = device.getMacAddress();

            // Registrar evento en BD
            ActuatorEvent event = actuatorService.createActuatorEvent(actuatorType, command, DEFAULT_DEVICE_ID);

            // Enviar comando MQTT con MAC
            mqttService.sendActuatorCommand(macAddress, actuatorType, command);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "actuator", actuatorType,
                    "command", command,
                    "eventId", event.getId(),
                    "deviceId", DEFAULT_DEVICE_ID,
                    "message", "Comando enviado"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al controlar {}: {}", actuatorType, e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "Error al enviar comando: " + e.getMessage()
            ));
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
