package com.damiot.backend.repository;

import com.damiot.backend.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para lecturas de sensores
 * 
 * @author Emilio José Salmerón Arjona
 */
@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    
    /**
     * Obtiene todas las lecturas de un dispositivo ordenadas por fecha descendente
     */
    List<SensorReading> findByDeviceIdOrderByTimestampDesc(Long deviceId);
    
    /**
     * Obtiene la última lectura de un tipo de sensor específico para un dispositivo
     */
    @Query("SELECT s FROM SensorReading s WHERE s.deviceId = :deviceId AND s.sensorType = :sensorType ORDER BY s.timestamp DESC LIMIT 1")
    Optional<SensorReading> findLatestByDeviceIdAndSensorType(
        @Param("deviceId") Long deviceId, 
        @Param("sensorType") String sensorType
    );
    
    /**
     * Obtiene las últimas lecturas de todos los sensores de un dispositivo
     * Una lectura por cada tipo de sensor
     */
    @Query("""
        SELECT s FROM SensorReading s 
        WHERE s.deviceId = :deviceId 
        AND s.timestamp = (
            SELECT MAX(s2.timestamp) 
            FROM SensorReading s2 
            WHERE s2.deviceId = :deviceId 
            AND s2.sensorType = s.sensorType
        )
        """)
    List<SensorReading> findLatestReadingsByDeviceId(@Param("deviceId") Long deviceId);

    /**
     * Obtiene la última lectura de un tipo de sensor (cualquier dispositivo)
     */
    Optional<SensorReading> findFirstBySensorTypeOrderByTimestampDesc(String sensorType);

    /**
     * Obtiene las últimas N lecturas de un tipo de sensor
     */
    @Query("SELECT s FROM SensorReading s WHERE s.sensorType = :sensorType ORDER BY s.timestamp DESC LIMIT :limit")
    List<SensorReading> findTopNBySensorTypeOrderByTimestampDesc(
        @Param("sensorType") String sensorType, 
        @Param("limit") int limit
    );

    /**
     * Obtiene lecturas entre dos fechas
     */
    List<SensorReading> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime start, 
        LocalDateTime end
    );

    /**
     * Obtiene todas las lecturas ordenadas por fecha descendente
     */
    List<SensorReading> findAllByOrderByTimestampDesc();

    /**
     * Obtiene lecturas de un sensor después de una fecha
     */
    List<SensorReading> findBySensorTypeAndTimestampAfter(
        String sensorType, 
        LocalDateTime timestamp
    );
}
