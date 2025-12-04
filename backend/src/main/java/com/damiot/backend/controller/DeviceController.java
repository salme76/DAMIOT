package com.damiot.backend.controller;

import com.damiot.backend.model.Device;
import com.damiot.backend.service.DeviceService;
import com.damiot.backend.service.MqttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar dispositivos ESP32
 * 
 * Endpoints principales:
 * - GET /api/devices - Obtener todos los dispositivos
 * - GET /api/devices/{id} - Obtener un dispositivo por ID
 * - PUT /api/devices/{id}/toggle - Habilitar/deshabilitar dispositivo
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;
    private final MqttService mqttService;

    /**
     * GET /api/devices
     * Obtener todos los dispositivos
     */
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        log.info("GET /api/devices");
        try {
            List<Device> devices = deviceService.getAllDevices();
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            log.error("Error al obtener dispositivos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/devices/enabled
     * Obtener dispositivos habilitados
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<Device>> getEnabledDevices() {
        log.info("GET /api/devices/enabled");
        try {
            List<Device> devices = deviceService.getAllEnabledDevices();
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            log.error("Error al obtener dispositivos habilitados: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/devices/{id}
     * Obtener un dispositivo por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        log.info("GET /api/devices/{}", id);
        try {
            return deviceService.getDeviceById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener dispositivo {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/devices/online
     * Obtener dispositivos online
     */
    @GetMapping("/online")
    public ResponseEntity<List<Device>> getOnlineDevices() {
        log.info("GET /api/devices/online");
        try {
            List<Device> devices = deviceService.getAllDevices().stream()
                    .filter(Device::isOnline)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            log.error("Error al obtener dispositivos online: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/devices/offline
     * Obtener dispositivos offline
     */
    @GetMapping("/offline")
    public ResponseEntity<List<Device>> getOfflineDevices() {
        log.info("GET /api/devices/offline");
        try {
            List<Device> devices = deviceService.getAllDevices().stream()
                    .filter(device -> !device.isOnline())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            log.error("Error al obtener dispositivos offline: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * PUT /api/devices/{id}/toggle
     * Habilitar/deshabilitar un dispositivo
     * 
     * COMPATIBILIDAD: Acepta tanto @RequestParam como @RequestBody
     * - Android envía: @Query("enabled") como parámetro de URL
     * - También acepta: {"enabled": true/false} en el body
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Device> toggleDevice(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean enabled,
            @RequestBody(required = false) Map<String, Boolean> requestBody) {
        log.info("PUT /api/devices/{}/toggle", id);
        
        try {
            // Priorizar @RequestParam (Android), si no existe usar @RequestBody
            Boolean enabledValue = enabled;
            if (enabledValue == null && requestBody != null) {
                enabledValue = requestBody.get("enabled");
            }
            
            if (enabledValue == null) {
                log.warn("Parámetro 'enabled' no proporcionado");
                return ResponseEntity.badRequest().build();
            }
            
            Device device = deviceService.toggleDevice(id, enabledValue);
            if (device == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(device);
        } catch (Exception e) {
            log.error("Error al cambiar estado del dispositivo {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/devices/mqtt/status
     * Verificar estado de conexión MQTT
     */
    @GetMapping("/mqtt/status")
    public ResponseEntity<Map<String, Object>> getMqttStatus() {
        log.info("GET /api/devices/mqtt/status");

        try {
            boolean connected = mqttService.isConnected();
            String info = mqttService.getClientInfo();

            Map<String, Object> response = Map.of(
                    "mqttConnected", connected,
                    "clientInfo", info
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al verificar estado MQTT: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "mqttConnected", false,
                    "clientInfo", "Error: " + e.getMessage()
            ));
        }
    }
}
