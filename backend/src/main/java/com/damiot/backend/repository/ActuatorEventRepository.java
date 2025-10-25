package com.damiot.backend.repository;

import com.damiot.backend.model.ActuatorEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActuatorEventRepository extends JpaRepository<ActuatorEvent, Long> {

    // Buscar por tipo de actuador
    List<ActuatorEvent> findByActuatorType(String actuatorType);

    // Buscar por dispositivo
    List<ActuatorEvent> findByDeviceId(String deviceId);

    // Buscar por estado
    List<ActuatorEvent> findByStatus(String status);

    // Buscar por tipo de actuador y dispositivo
    List<ActuatorEvent> findByActuatorTypeAndDeviceId(String actuatorType, String deviceId);

    // Obtener el último evento de un actuador
    Optional<ActuatorEvent> findFirstByActuatorTypeOrderByTimestampDesc(String actuatorType);

    // Buscar eventos entre fechas
    List<ActuatorEvent> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Obtener últimos 10 eventos
    List<ActuatorEvent> findTop10ByOrderByTimestampDesc();

    // Buscar eventos fallidos
    List<ActuatorEvent> findByStatusOrderByTimestampDesc(String status);
}