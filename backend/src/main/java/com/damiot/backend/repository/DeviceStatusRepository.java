package com.damiot.backend.repository;

import com.damiot.backend.model.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, Long> {

    // Buscar por deviceId (debe ser único)
    Optional<DeviceStatus> findByDeviceId(String deviceId);

    // Buscar dispositivos online
    List<DeviceStatus> findByOnline(Boolean online);

    // Buscar dispositivos que no se han visto desde una fecha
    List<DeviceStatus> findByLastSeenBefore(LocalDateTime dateTime);

    // Verificar si un dispositivo existe
    boolean existsByDeviceId(String deviceId);

    // Eliminar por deviceId
    void deleteByDeviceId(String deviceId);
}
