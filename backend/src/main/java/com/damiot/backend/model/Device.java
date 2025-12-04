package com.damiot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para dispositivos ESP32
 * Corresponde a la tabla 'device' en la base de datos
 * 
 * @author Emilio José Salmerón Arjona
 */
@Entity
@Table(name = "device")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "mac_address", nullable = false, unique = true, length = 17)
    private String macAddress;

    @Column(name = "ip_address", length = 15)
    private String ipAddress;

    @Column(nullable = false, length = 10)
    private String status = "offline"; // "online" u "offline"

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Column(name = "last_connection")
    private LocalDateTime lastConnection;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "offline";
        }
        if (isEnabled == null) {
            isEnabled = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el dispositivo está conectado
     */
    public boolean isOnline() {
        return "online".equalsIgnoreCase(status);
    }

    /**
     * Marca el dispositivo como online
     */
    public void markAsOnline(String ipAddress) {
        this.status = "online";
        this.ipAddress = ipAddress;
        this.lastConnection = LocalDateTime.now();
    }

    /**
     * Marca el dispositivo como offline
     */
    public void markAsOffline() {
        this.status = "offline";
    }
}
