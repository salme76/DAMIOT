package com.damiot.backend.controller;

import com.damiot.backend.model.ActuatorEvent;
import com.damiot.backend.service.ActuatorService;
import com.damiot.backend.service.MqttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actuators")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ActuatorController {

    private final ActuatorService actuatorService;
    private final MqttService mqttService;

    /**
     * POST /api/actuators/led
     * Controlar el LED del ESP32
     * Body: {"command": "ON"} o {"command": "OFF"}
     */
    @PostMapping("/led")
    public ResponseEntity<Map<String, Object>> controlLed(
            @RequestBody Map<String, String> request) {
        String command = request.get("command");
        log.info("POST /api/actuators/led - Command: {}", command);

        if (command == null || (!command.equals("ON") && !command.equals("OFF"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Command must be 'ON' or 'OFF'"));
        }

        // Registrar evento en BD
        String deviceId = "ESP32-001";
        ActuatorEvent event = actuatorService.createActuatorEvent("led", command, deviceId);

        // Enviar comando MQTT
        mqttService.sendLedCommand(command);

        Map<String, Object> response = Map.of(
                "success", true,
                "actuator", "led",
                "command", command,
                "eventId", event.getId(),
                "message", "Comando enviado al LED"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/actuators/motor
     * Controlar el motor del ESP32
     * Body: {"command": "START"} o {"command": "STOP"} o {"command": "SPEED_50"}
     */
    @PostMapping("/motor")
    public ResponseEntity<Map<String, Object>> controlMotor(
            @RequestBody Map<String, String> request) {
        String command = request.get("command");
        log.info("POST /api/actuators/motor - Command: {}", command);

        if (command == null || command.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Command is required"));
        }

        // Registrar evento en BD
        String deviceId = "ESP32-001";
        ActuatorEvent event = actuatorService.createActuatorEvent("motor", command, deviceId);

        // Enviar comando MQTT
        mqttService.sendMotorCommand(command);

        Map<String, Object> response = Map.of(
                "success", true,
                "actuator", "motor",
                "command", command,
                "eventId", event.getId(),
                "message", "Comando enviado al motor"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/actuators/{actuatorType}
     * Endpoint genérico para cualquier actuador
     */
    @PostMapping("/{actuatorType}")
    public ResponseEntity<Map<String, Object>> controlActuator(
            @PathVariable String actuatorType,
            @RequestBody Map<String, String> request) {
        String command = request.get("command");
        log.info("POST /api/actuators/{} - Command: {}", actuatorType, command);

        if (command == null || command.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Command is required"));
        }

        // Registrar evento en BD
        String deviceId = "ESP32-001";
        ActuatorEvent event = actuatorService.createActuatorEvent(actuatorType, command, deviceId);

        // Enviar comando MQTT
        mqttService.sendActuatorCommand(actuatorType, command);

        Map<String, Object> response = Map.of(
                "success", true,
                "actuator", actuatorType,
                "command", command,
                "eventId", event.getId(),
                "message", "Comando enviado"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/actuators/history/{actuatorType}
     * Obtener historial de comandos de un actuador
     */
    @GetMapping("/history/{actuatorType}")
    public ResponseEntity<List<ActuatorEvent>> getActuatorHistory(
            @PathVariable String actuatorType) {
        log.info("GET /api/actuators/history/{}", actuatorType);

        List<ActuatorEvent> events = actuatorService.getEventsByActuatorType(actuatorType);
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/actuators/latest/{actuatorType}
     * Obtener el último evento de un actuador
     */
    @GetMapping("/latest/{actuatorType}")
    public ResponseEntity<ActuatorEvent> getLatestEvent(
            @PathVariable String actuatorType) {
        log.info("GET /api/actuators/latest/{}", actuatorType);

        return actuatorService.getLatestEvent(actuatorType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/actuators/history
     * Obtener todo el historial de eventos
     */
    @GetMapping("/history")
    public ResponseEntity<List<ActuatorEvent>> getAllHistory() {
        log.info("GET /api/actuators/history");

        List<ActuatorEvent> events = actuatorService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/actuators/latest
     * Obtener los últimos 10 eventos
     */
    @GetMapping("/latest")
    public ResponseEntity<List<ActuatorEvent>> getLatestEvents() {
        log.info("GET /api/actuators/latest");

        List<ActuatorEvent> events = actuatorService.getLatestEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/actuators/failed
     * Obtener eventos fallidos
     */
    @GetMapping("/failed")
    public ResponseEntity<List<ActuatorEvent>> getFailedEvents() {
        log.info("GET /api/actuators/failed");

        List<ActuatorEvent> events = actuatorService.getFailedEvents();
        return ResponseEntity.ok(events);
    }
}
