package com.damiot.backend.repository;

import com.damiot.backend.model.ActuatorEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para eventos de actuadores
 * 
 * Proporciona métodos para consultar el historial de comandos
 * enviados a los actuadores ESP32.
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@Repository
public interface ActuatorEventRepository extends JpaRepository<ActuatorEvent, Long> {

    // Buscar por tipo de actuador
    List<ActuatorEvent> findByActuatorType(String actuatorType);

    // Buscar por dispositivo (device_id es BIGINT en BD)
    List<ActuatorEvent> findByDeviceId(Long deviceId);

    // Buscar por estado
    List<ActuatorEvent> findByStatus(String status);

    // Buscar por tipo de actuador y dispositivo
    List<ActuatorEvent> findByActuatorTypeAndDeviceId(String actuatorType, Long deviceId);

    // Obtener el último evento de un actuador
    Optional<ActuatorEvent> findFirstByActuatorTypeOrderByTimestampDesc(String actuatorType);

    // Buscar eventos entre fechas
    List<ActuatorEvent> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Obtener últimos 10 eventos
    List<ActuatorEvent> findTop10ByOrderByTimestampDesc();

    // Buscar eventos fallidos
    List<ActuatorEvent> findByStatusOrderByTimestampDesc(String status);

    // Obtener todos los eventos ordenados por timestamp descendente
    List<ActuatorEvent> findAllByOrderByTimestampDesc();

    // Buscar por tipo de actuador ordenados por timestamp descendente
    List<ActuatorEvent> findByActuatorTypeOrderByTimestampDesc(String actuatorType);
}
