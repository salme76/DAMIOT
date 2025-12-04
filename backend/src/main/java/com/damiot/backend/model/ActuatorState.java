package com.damiot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para estado de actuadores
 * Corresponde a la tabla 'actuator_state' en la base de datos
 * 
 * @author Emilio José Salmerón Arjona
 */
@Entity
@Table(name = "actuator_state",
       uniqueConstraints = {
           @UniqueConstraint(name = "unique_device_actuator", 
                           columnNames = {"device_id", "actuator_type"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActuatorState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "actuator_type", nullable = false, length = 50)
    private String actuatorType; // "led_azul", "led_verde", "bomba_riego"

    @Column(nullable = false, length = 50)
    private String state; // "ON", "OFF"

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el actuador está encendido
     */
    public boolean isOn() {
        return "ON".equalsIgnoreCase(state);
    }
}
