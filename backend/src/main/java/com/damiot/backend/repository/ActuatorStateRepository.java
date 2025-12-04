package com.damiot.backend.repository;

import com.damiot.backend.model.ActuatorState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para estados de actuadores
 * 
 * @author Emilio José Salmerón Arjona
 */
@Repository
public interface ActuatorStateRepository extends JpaRepository<ActuatorState, Long> {
    
    /**
     * Obtiene todos los actuadores de un dispositivo
     */
    List<ActuatorState> findByDeviceId(Long deviceId);
    
    /**
     * Busca un actuador específico de un dispositivo
     */
    Optional<ActuatorState> findByDeviceIdAndActuatorType(Long deviceId, String actuatorType);
    
    /**
     * Verifica si existe un actuador para un dispositivo
     */
    boolean existsByDeviceIdAndActuatorType(Long deviceId, String actuatorType);
}
