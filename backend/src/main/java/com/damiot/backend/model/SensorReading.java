package com.damiot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String sensorType;  // "temperature", "humidity", etc.

    @Column(nullable = false)
    private Double value;

    @Column(length = 10)
    private String unit;  // "°C", "%", etc.

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 100)
    private String deviceId;  // Identificador del ESP32

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}