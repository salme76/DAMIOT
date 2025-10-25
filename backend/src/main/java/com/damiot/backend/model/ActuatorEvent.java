package com.damiot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "actuator_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActuatorEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String actuatorType;  // "led", "motor", etc.

    @Column(nullable = false, length = 20)
    private String command;  // "ON", "OFF", "SPEED_50", etc.

    @Column(length = 20)
    private String status;  // "SENT", "EXECUTED", "FAILED"

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 100)
    private String deviceId;  // Identificador del ESP32

    @Column(length = 500)
    private String response;  // Respuesta del ESP32 (opcional)

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (status == null) {
            status = "SENT";
        }
    }
}