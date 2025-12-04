package com.damiot.backend.controller;

import com.damiot.backend.service.DeviceService;
import com.damiot.backend.service.MqttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controlador REST para verificar el estado del sistema
 * 
 * Endpoints:
 * - GET /api/health - Estado general del sistema
 * - GET /api/health/mqtt - Estado de la conexión MQTT
 * - GET /api/health/database - Estado de la base de datos
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HealthController {

    private final MqttService mqttService;
    private final DeviceService deviceService;

    /**
     * GET /api/health
     * Obtener estado general del sistema
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        log.debug("GET /api/health");
        
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("service", "DAMIOT Backend");
        status.put("version", "1.0.0");
        
        // Estado MQTT
        boolean mqttConnected = mqttService.isConnected();
        status.put("mqtt", Map.of(
                "connected", mqttConnected,
                "info", mqttService.getClientInfo()
        ));
        
        // Estado BD
        boolean dbAvailable = deviceService.isDatabaseAvailable();
        status.put("database", Map.of(
                "available", dbAvailable
        ));
        
        // Resumen general
        String overallStatus = (mqttConnected && dbAvailable) ? "HEALTHY" : 
                               (mqttConnected || dbAvailable) ? "DEGRADED" : "UNHEALTHY";
        status.put("overall", overallStatus);
        
        return ResponseEntity.ok(status);
    }

    /**
     * GET /api/health/mqtt
     * Obtener estado de la conexión MQTT
     */
    @GetMapping("/mqtt")
    public ResponseEntity<Map<String, Object>> getMqttHealth() {
        log.debug("GET /api/health/mqtt");
        
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("connected", mqttService.isConnected());
        status.put("clientInfo", mqttService.getClientInfo());
        status.put("connectionLost", mqttService.wasConnectionLost());
        
        String lastError = mqttService.getLastError();
        if (lastError != null) {
            status.put("lastError", lastError);
        }
        
        status.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(status);
    }

    /**
     * GET /api/health/database
     * Obtener estado de la base de datos
     */
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> getDatabaseHealth() {
        log.debug("GET /api/health/database");
        
        Map<String, Object> status = new LinkedHashMap<>();
        
        try {
            // Intentar una consulta simple
            int deviceCount = deviceService.getAllDevices().size();
            status.put("available", true);
            status.put("deviceCount", deviceCount);
            status.put("timestamp", LocalDateTime.now().toString());
        } catch (Exception e) {
            status.put("available", false);
            status.put("error", e.getMessage());
            status.put("timestamp", LocalDateTime.now().toString());
        }
        
        return ResponseEntity.ok(status);
    }

    /**
     * GET /api/health/ping
     * Endpoint simple para verificar que el servidor responde
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok(Map.of(
                "pong", true,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
