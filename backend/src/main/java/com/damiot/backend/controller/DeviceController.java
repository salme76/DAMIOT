package com.damiot.backend.controller;

import com.damiot.backend.model.DeviceStatus;
import com.damiot.backend.service.DeviceService;
import com.damiot.backend.service.MqttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;
    private final MqttService mqttService;

    /**
     * GET /api/device/status/{deviceId}
     * Obtener estado de un dispositivo
     */
    @GetMapping("/status/{deviceId}")
    public ResponseEntity<DeviceStatus> getDeviceStatus(
            @PathVariable String deviceId) {
        log.info("GET /api/device/status/{}", deviceId);

        return deviceService.getDeviceStatus(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/device/status
     * Obtener estado de todos los dispositivos
     */
    @GetMapping("/status")
    public ResponseEntity<List<DeviceStatus>> getAllDevicesStatus() {
        log.info("GET /api/device/status");

        List<DeviceStatus> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    /**
     * GET /api/device/online
     * Obtener dispositivos online
     */
    @GetMapping("/online")
    public ResponseEntity<List<DeviceStatus>> getOnlineDevices() {
        log.info("GET /api/device/online");

        List<DeviceStatus> devices = deviceService.getOnlineDevices();
        return ResponseEntity.ok(devices);
    }

    /**
     * GET /api/device/offline
     * Obtener dispositivos offline
     */
    @GetMapping("/offline")
    public ResponseEntity<List<DeviceStatus>> getOfflineDevices() {
        log.info("GET /api/device/offline");

        List<DeviceStatus> devices = deviceService.getOfflineDevices();
        return ResponseEntity.ok(devices);
    }

    /**
     * POST /api/device/request-status/{deviceId}
     * Solicitar estado actual del dispositivo vía MQTT
     */
    @PostMapping("/request-status/{deviceId}")
    public ResponseEntity<Map<String, Object>> requestDeviceStatus(
            @PathVariable String deviceId) {
        log.info("POST /api/device/request-status/{}", deviceId);

        mqttService.requestDeviceStatus(deviceId);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Solicitud de estado enviada al dispositivo",
                "deviceId", deviceId
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/device/mqtt/status
     * Verificar estado de conexión MQTT
     */
    @GetMapping("/mqtt/status")
    public ResponseEntity<Map<String, Object>> getMqttStatus() {
        log.info("GET /api/device/mqtt/status");

        boolean connected = mqttService.isConnected();
        String info = mqttService.getClientInfo();

        Map<String, Object> response = Map.of(
                "mqttConnected", connected,
                "clientInfo", info
        );

        return ResponseEntity.ok(response);
    }
}
