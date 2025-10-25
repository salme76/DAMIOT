package com.damiot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String deviceId;  // Identificador único del ESP32

    @Column(nullable = false)
    private Boolean online;

    @Column(nullable = false)
    private LocalDateTime lastSeen;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 100)
    private String firmwareVersion;

    @Column
    private LocalDateTime connectedSince;

    @PrePersist
    protected void onCreate() {
        if (lastSeen == null) {
            lastSeen = LocalDateTime.now();
        }
        if (online == null) {
            online = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastSeen = LocalDateTime.now();
    }
}