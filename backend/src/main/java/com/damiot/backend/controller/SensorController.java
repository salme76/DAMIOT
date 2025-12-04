package com.damiot.backend.controller;

import com.damiot.backend.model.SensorReading;
import com.damiot.backend.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar lecturas de sensores
 * 
 * Endpoints principales:
 * - GET /api/sensors/device/{deviceId}/latest - Últimas lecturas de un dispositivo (Android)
 * - GET /api/sensors/latest - Última lectura de todos los sensores
 * - GET /api/sensors/history/{sensorType} - Historial de un sensor
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SensorController {

    private final SensorService sensorService;

    /**
     * GET /api/sensors/device/{deviceId}/latest
     * Obtiene las últimas lecturas de todos los sensores de un dispositivo
     * 
     * REQUERIDO POR ANDROID: DamiotApi.getLatestSensorReadings()
     * Retorna Map<String, SensorReading> donde la clave es el tipo de sensor
     * 
     * @param deviceId ID del dispositivo
     * @return Mapa con tipo de sensor como clave y lectura como valor
     */
    @GetMapping("/device/{deviceId}/latest")
    public ResponseEntity<Map<String, SensorReading>> getLatestReadingsByDevice(
            @PathVariable Long deviceId) {
        log.info("GET /api/sensors/device/{}/latest", deviceId);
        
        try {
            Map<String, SensorReading> readings = sensorService.getLatestReadingsByDevice(deviceId);
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            log.error("Error al obtener lecturas del dispositivo {}: {}", deviceId, e.getMessage());
            return ResponseEntity.ok(new HashMap<>()); // Devolver mapa vacío en lugar de error
        }
    }

    /**
     * GET /api/sensors/latest
     * Obtener la última lectura de todos los sensores (genérico)
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestReadings() {
        log.info("GET /api/sensors/latest");

        try {
            var temperature = sensorService.getLatestReading("temperature");
            var humidity = sensorService.getLatestReading("humidity");

            Map<String, Object> response = new HashMap<>();
            response.put("temperature", temperature.orElse(null));
            response.put("humidity", humidity.orElse(null));
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener últimas lecturas: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Base de datos no disponible");
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * GET /api/sensors/latest/{sensorType}
     * Obtener la última lectura de un sensor específico
     */
    @GetMapping("/latest/{sensorType}")
    public ResponseEntity<SensorReading> getLatestReadingBySensor(
            @PathVariable String sensorType) {
        log.info("GET /api/sensors/latest/{}", sensorType);

        try {
            return sensorService.getLatestReading(sensorType)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener lectura de {}: {}", sensorType, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
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

        try {
            List<SensorReading> readings = sensorService.getLatestReadings(sensorType, limit);
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            log.error("Error al obtener historial de {}: {}", sensorType, e.getMessage());
            return ResponseEntity.ok(List.of()); // Devolver lista vacía en lugar de error
        }
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

        try {
            List<SensorReading> readings = sensorService.getReadingsBetween(start, end);
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            log.error("Error al obtener historial entre fechas: {}", e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * GET /api/sensors/all
     * Obtener todas las lecturas
     */
    @GetMapping("/all")
    public ResponseEntity<List<SensorReading>> getAllReadings() {
        log.info("GET /api/sensors/all");

        try {
            List<SensorReading> readings = sensorService.getAllReadings();
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            log.error("Error al obtener todas las lecturas: {}", e.getMessage());
            return ResponseEntity.ok(List.of());
        }
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

        try {
            LocalDateTime since = LocalDateTime.now().minusHours(hours);
            Double average = sensorService.getAverageValue(sensorType, since);

            Map<String, Object> response = Map.of(
                    "sensorType", sensorType,
                    "average", average != null ? average : 0.0,
                    "hours", hours,
                    "since", since
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al calcular promedio de {}: {}", sensorType, e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "sensorType", sensorType,
                    "average", 0.0,
                    "error", "No hay datos disponibles"
            ));
        }
    }

    /**
     * GET /api/sensors/device/{deviceId}
     * Obtener todas las lecturas de un dispositivo
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<SensorReading>> getReadingsByDevice(
            @PathVariable Long deviceId) {
        log.info("GET /api/sensors/device/{}", deviceId);

        try {
            List<SensorReading> readings = sensorService.getAllReadingsByDevice(deviceId);
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            log.error("Error al obtener lecturas del dispositivo {}: {}", deviceId, e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * POST /api/sensors
     * Crear una lectura manualmente (para testing)
     */
    @PostMapping
    public ResponseEntity<SensorReading> createReading(
            @RequestBody SensorReading reading) {
        log.info("POST /api/sensors - {}", reading);

        try {
            SensorReading saved = sensorService.saveSensorReading(reading);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error al guardar lectura: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
