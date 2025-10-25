package com.damiot.backend.service;

import com.damiot.backend.model.SensorReading;
import com.damiot.backend.repository.SensorReadingRepository;
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
public class SensorService {

    private final SensorReadingRepository sensorReadingRepository;

    /**
     * Guardar una nueva lectura de sensor
     */
    @Transactional
    public SensorReading saveSensorReading(SensorReading reading) {
        log.info("Guardando lectura de sensor: {} = {} {}",
                reading.getSensorType(), reading.getValue(), reading.getUnit());
        return sensorReadingRepository.save(reading);
    }

    /**
     * Guardar lectura desde datos MQTT
     */
    @Transactional
    public SensorReading saveSensorReading(String sensorType, Double value, String unit, String deviceId) {
        SensorReading reading = new SensorReading();
        reading.setSensorType(sensorType);
        reading.setValue(value);
        reading.setUnit(unit);
        reading.setDeviceId(deviceId);
        reading.setTimestamp(LocalDateTime.now());

        return saveSensorReading(reading);
    }

    /**
     * Obtener la última lectura de un sensor
     */
    public Optional<SensorReading> getLatestReading(String sensorType) {
        log.debug("Obteniendo última lectura de: {}", sensorType);
        return sensorReadingRepository.findFirstBySensorTypeOrderByTimestampDesc(sensorType);
    }

    /**
     * Obtener últimas N lecturas de un sensor
     */
    public List<SensorReading> getLatestReadings(String sensorType, int limit) {
        log.debug("Obteniendo últimas {} lecturas de: {}", limit, sensorType);
        return sensorReadingRepository.findTop10BySensorTypeOrderByTimestampDesc(sensorType);
    }

    /**
     * Obtener historial de lecturas entre fechas
     */
    public List<SensorReading> getReadingsBetween(LocalDateTime start, LocalDateTime end) {
        log.debug("Obteniendo lecturas entre {} y {}", start, end);
        return sensorReadingRepository.findByTimestampBetween(start, end);
    }

    /**
     * Obtener todas las lecturas de un tipo de sensor
     */
    public List<SensorReading> getAllReadingsBySensorType(String sensorType) {
        log.debug("Obteniendo todas las lecturas de: {}", sensorType);
        return sensorReadingRepository.findBySensorType(sensorType);
    }

    /**
     * Obtener lecturas por dispositivo
     */
    public List<SensorReading> getReadingsByDevice(String deviceId) {
        log.debug("Obteniendo lecturas del dispositivo: {}", deviceId);
        return sensorReadingRepository.findByDeviceId(deviceId);
    }

    /**
     * Calcular promedio de un sensor desde una fecha
     */
    public Double getAverageValue(String sensorType, LocalDateTime since) {
        log.debug("Calculando promedio de {} desde {}", sensorType, since);
        Double average = sensorReadingRepository.getAverageValue(sensorType, since);
        return average != null ? average : 0.0;
    }

    /**
     * Obtener todas las lecturas
     */
    public List<SensorReading> getAllReadings() {
        log.debug("Obteniendo todas las lecturas");
        return sensorReadingRepository.findAll();
    }

    /**
     * Eliminar lecturas antiguas (limpieza)
     */
    @Transactional
    public void deleteOldReadings(LocalDateTime before) {
        log.info("Eliminando lecturas anteriores a: {}", before);
        List<SensorReading> oldReadings = sensorReadingRepository.findByTimestampBetween(
                LocalDateTime.of(2000, 1, 1, 0, 0), before);
        sensorReadingRepository.deleteAll(oldReadings);
        log.info("Eliminadas {} lecturas antiguas", oldReadings.size());
    }
}