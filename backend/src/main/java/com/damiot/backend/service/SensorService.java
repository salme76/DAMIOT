package com.damiot.backend.service;

import com.damiot.backend.model.SensorReading;
import com.damiot.backend.repository.SensorReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar lecturas de sensores
 * 
 * Proporciona métodos para:
 * - Guardar lecturas de sensores
 * - Consultar últimas lecturas
 * - Obtener historial
 * - Calcular promedios
 * 
 * Resiliencia: Maneja errores de BD sin crashear la aplicación
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {

    private final SensorReadingRepository sensorReadingRepository;

    /**
     * Guarda una nueva lectura de sensor
     * 
     * @param sensorType Tipo de sensor (temperature, humidity, etc.)
     * @param value Valor leído
     * @param unit Unidad de medida
     * @param deviceId ID del dispositivo
     * @return SensorReading guardada o null si hay error
     */
    @Transactional
    public SensorReading saveSensorReading(String sensorType, Double value, String unit, Long deviceId) {
        try {
            SensorReading reading = new SensorReading();
            reading.setDeviceId(deviceId);
            reading.setSensorType(sensorType);
            reading.setValue(value);
            reading.setUnit(unit);
            reading.setTimestamp(LocalDateTime.now());
            
            SensorReading saved = sensorReadingRepository.save(reading);
            log.debug("📊 Lectura guardada: {} = {} {} (Device ID: {})", 
                    sensorType, value, unit, deviceId);
            
            return saved;
            
        } catch (DataAccessException e) {
            log.error("Error al guardar lectura de sensor: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Guarda una lectura completa (para POST manual en testing)
     */
    @Transactional
    public SensorReading saveSensorReading(SensorReading reading) {
        try {
            if (reading.getTimestamp() == null) {
                reading.setTimestamp(LocalDateTime.now());
            }
            
            SensorReading saved = sensorReadingRepository.save(reading);
            log.debug("📊 Lectura guardada: {}", saved);
            
            return saved;
            
        } catch (DataAccessException e) {
            log.error("Error al guardar lectura de sensor: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene la última lectura de un tipo de sensor
     */
    public Optional<SensorReading> getLatestReading(String sensorType) {
        try {
            return sensorReadingRepository.findFirstBySensorTypeOrderByTimestampDesc(sensorType);
        } catch (DataAccessException e) {
            log.error("Error al obtener última lectura de {}: {}", sensorType, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Obtiene las últimas N lecturas de un tipo de sensor
     */
    public List<SensorReading> getLatestReadings(String sensorType, int limit) {
        try {
            return sensorReadingRepository.findTopNBySensorTypeOrderByTimestampDesc(sensorType, limit);
        } catch (DataAccessException e) {
            log.error("Error al obtener lecturas de {}: {}", sensorType, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene lecturas entre dos fechas
     */
    public List<SensorReading> getReadingsBetween(LocalDateTime start, LocalDateTime end) {
        try {
            return sensorReadingRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
        } catch (DataAccessException e) {
            log.error("Error al obtener lecturas entre fechas: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene todas las lecturas
     */
    public List<SensorReading> getAllReadings() {
        try {
            return sensorReadingRepository.findAllByOrderByTimestampDesc();
        } catch (DataAccessException e) {
            log.error("Error al obtener todas las lecturas: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene todas las lecturas de un dispositivo (por String ID para compatibilidad)
     */
    public List<SensorReading> getReadingsByDevice(String deviceId) {
        try {
            Long id = Long.parseLong(deviceId);
            return sensorReadingRepository.findByDeviceIdOrderByTimestampDesc(id);
        } catch (NumberFormatException e) {
            log.error("ID de dispositivo inválido: {}", deviceId);
            return new ArrayList<>();
        } catch (DataAccessException e) {
            log.error("Error al obtener lecturas del dispositivo {}: {}", deviceId, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Calcula el promedio de un sensor desde una fecha
     */
    public Double getAverageValue(String sensorType, LocalDateTime since) {
        try {
            List<SensorReading> readings = sensorReadingRepository
                    .findBySensorTypeAndTimestampAfter(sensorType, since);
            
            if (readings.isEmpty()) {
                return 0.0;
            }
            
            return readings.stream()
                    .mapToDouble(SensorReading::getValue)
                    .average()
                    .orElse(0.0);
                    
        } catch (DataAccessException e) {
            log.error("Error al calcular promedio de {}: {}", sensorType, e.getMessage());
            return 0.0;
        }
    }

    /**
     * Obtiene las últimas lecturas de todos los sensores de un dispositivo
     * Retorna un mapa donde la clave es el tipo de sensor y el valor es la lectura
     * 
     * REQUERIDO POR ANDROID: Este método es llamado desde SensorController
     * para el endpoint /api/sensors/device/{deviceId}/latest
     */
    public Map<String, SensorReading> getLatestReadingsByDevice(Long deviceId) {
        try {
            List<SensorReading> readings = sensorReadingRepository.findLatestReadingsByDeviceId(deviceId);
            
            Map<String, SensorReading> readingsMap = new HashMap<>();
            for (SensorReading reading : readings) {
                readingsMap.put(reading.getSensorType(), reading);
            }
            
            return readingsMap;
            
        } catch (DataAccessException e) {
            log.error("Error al obtener últimas lecturas del dispositivo {}: {}", deviceId, e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Obtiene todas las lecturas de un dispositivo por ID numérico
     */
    public List<SensorReading> getAllReadingsByDevice(Long deviceId) {
        try {
            return sensorReadingRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
        } catch (DataAccessException e) {
            log.error("Error al obtener lecturas del dispositivo {}: {}", deviceId, e.getMessage());
            return new ArrayList<>();
        }
    }
}
