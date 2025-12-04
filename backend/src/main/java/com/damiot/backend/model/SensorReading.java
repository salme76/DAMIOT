package com.damiot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para lecturas de sensores
 * Corresponde a la tabla 'sensor_data' en la base de datos
 * 
 * @author Emilio José Salmerón Arjona
 */
@Entity
@Table(name = "sensor_data",
       indexes = {
           @Index(name = "idx_device_timestamp", columnList = "device_id, timestamp"),
           @Index(name = "idx_timestamp", columnList = "timestamp"),
           @Index(name = "idx_sensor_type", columnList = "sensor_type")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "sensor_type", nullable = false, length = 50)
    private String sensorType; // "temperatura", "humedad"

    @Column(nullable = false)
    private Double value;

    @Column(length = 20)
    private String unit; // "°C", "%"

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
