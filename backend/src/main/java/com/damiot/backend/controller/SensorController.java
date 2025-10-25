package com.damiot.backend.controller;

import com.damiot.backend.model.SensorReading;
import com.damiot.backend.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SensorController {

    private final SensorService sensorService;

    /**
     * GET /api/sensors/latest
     * Obtener la última lectura de todos los sensores
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestReadings() {
        log.info("GET /api/sensors/latest");

        var temperature = sensorService.getLatestReading("temperature");
        var humidity = sensorService.getLatestReading("humidity");

        Map<String, Object> response = Map.of(
                "temperature", temperature.orElse(null),
                "humidity", humidity.orElse(null),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/sensors/latest/{sensorType}
     * Obtener la última lectura de un sensor específico
     */
    @GetMapping("/latest/{sensorType}")
    public ResponseEntity<SensorReading> getLatestReadingBySensor(
            @PathVariable String sensorType) {
        log.info("GET /api/sensors/latest/{}", sensorType);

        return sensorService.getLatestReading(sensorType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/sensors/history/{sensorType}
     * Obtener historial de un sensor (últimas 10 lecturas por defecto)
     */
    @GetMapping("/history/{sensorType}")
    public ResponseEntity<List<SensorReading>> getSensorHistory(
            @PathVariable String sensorType,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /api/sensors/history/{} (limit: {})", sensorType, limit);

        List<SensorReading> readings = sensorService.getLatestReadings(sensorType, limit);
        return ResponseEntity.ok(readings);
    }

    /**
     * GET /api/sensors/history
     * Obtener historial entre fechas
     */
    @GetMapping("/history")
    public ResponseEntity<List<SensorReading>> getHistoryBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("GET /api/sensors/history (desde {} hasta {})", start, end);

        List<SensorReading> readings = sensorService.getReadingsBetween(start, end);
        return ResponseEntity.ok(readings);
    }

    /**
     * GET /api/sensors/all
     * Obtener todas las lecturas
     */
    @GetMapping("/all")
    public ResponseEntity<List<SensorReading>> getAllReadings() {
        log.info("GET /api/sensors/all");

        List<SensorReading> readings = sensorService.getAllReadings();
        return ResponseEntity.ok(readings);
    }

    /**
     * GET /api/sensors/{sensorType}/average
     * Obtener promedio de un sensor desde hace X horas
     */
    @GetMapping("/{sensorType}/average")
    public ResponseEntity<Map<String, Object>> getAverageValue(
            @PathVariable String sensorType,
            @RequestParam(defaultValue = "24") int hours) {
        log.info("GET /api/sensors/{}/average (últimas {} horas)", sensorType, hours);

        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        Double average = sensorService.getAverageValue(sensorType, since);

        Map<String, Object> response = Map.of(
                "sensorType", sensorType,
                "average", average,
                "hours", hours,
                "since", since
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/sensors/device/{deviceId}
     * Obtener todas las lecturas de un dispositivo
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<SensorReading>> getReadingsByDevice(
            @PathVariable String deviceId) {
        log.info("GET /api/sensors/device/{}", deviceId);

        List<SensorReading> readings = sensorService.getReadingsByDevice(deviceId);
        return ResponseEntity.ok(readings);
    }

    /**
     * POST /api/sensors
     * Crear una lectura manualmente (para testing)
     */
    @PostMapping
    public ResponseEntity<SensorReading> createReading(
            @RequestBody SensorReading reading) {
        log.info("POST /api/sensors - {}", reading);

        SensorReading saved = sensorService.saveSensorReading(reading);
        return ResponseEntity.ok(saved);
    }
}