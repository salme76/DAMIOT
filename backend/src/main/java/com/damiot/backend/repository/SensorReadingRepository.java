package com.damiot.backend.repository;

import com.damiot.backend.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    // Buscar por tipo de sensor
    List<SensorReading> findBySensorType(String sensorType);

    // Buscar por dispositivo
    List<SensorReading> findByDeviceId(String deviceId);

    // Buscar por tipo de sensor y dispositivo
    List<SensorReading> findBySensorTypeAndDeviceId(String sensorType, String deviceId);

    // Buscar lecturas entre fechas
    List<SensorReading> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Obtener la última lectura de un tipo de sensor
    Optional<SensorReading> findFirstBySensorTypeOrderByTimestampDesc(String sensorType);

    // Obtener las últimas N lecturas de un sensor
    List<SensorReading> findTop10BySensorTypeOrderByTimestampDesc(String sensorType);

    // Query personalizada: obtener promedio de temperatura
    @Query("SELECT AVG(s.value) FROM SensorReading s WHERE s.sensorType = :sensorType AND s.timestamp >= :since")
    Double getAverageValue(String sensorType, LocalDateTime since);
}