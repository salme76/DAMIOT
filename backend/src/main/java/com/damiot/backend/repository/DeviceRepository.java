package com.damiot.backend.repository;

import com.damiot.backend.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para dispositivos ESP32
 * 
 * @author Emilio José Salmerón Arjona
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    /**
     * Busca un dispositivo por su dirección MAC
     */
    Optional<Device> findByMacAddress(String macAddress);
    
    /**
     * Obtiene todos los dispositivos habilitados
     */
    List<Device> findByIsEnabledTrue();
    
    /**
     * Verifica si existe un dispositivo con esa MAC
     */
    boolean existsByMacAddress(String macAddress);
}
