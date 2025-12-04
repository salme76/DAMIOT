package com.damiot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para eventos de actuadores
 * Corresponde a la tabla 'actuator_events' en la base de datos
 * 
 * Registra el historial de comandos enviados a los actuadores ESP32.
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@Entity
@Table(name = "actuator_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActuatorEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actuator_type", nullable = false, length = 50)
    private String actuatorType;  // "led_azul", "motor", "bomba_riego", etc.

    @Column(nullable = false, length = 20)
    private String command;  // "ON", "OFF", "SPEED_50", etc.

    @Column(name = "device_id", nullable = false)
    private Long deviceId;  // ID del dispositivo ESP32 (FK a device.id)

    @Column(length = 20)
    private String status;  // "SENT", "CONFIRMED", "FAILED"

    @Column(length = 500)
    private String response;  // Respuesta del ESP32 (opcional)

    @Column(nullable = false)
    private LocalDateTime timestamp;

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
