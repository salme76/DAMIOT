package com.damiot.backend.scheduler;

import com.damiot.backend.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tareas programadas para el sistema DAMIOT
 * 
 * Verifica periódicamente:
 * - Dispositivos inactivos (sin heartbeat)
 * 
 * Resiliencia: Captura excepciones para no crashear si BD no disponible
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceMonitorScheduler {

    private final DeviceService deviceService;
    private boolean lastCheckFailed = false;

    /**
     * Verifica dispositivos inactivos cada 15 segundos
     * Si un dispositivo no envía heartbeat en 30 segundos, se marca como offline
     * 
     * Valores optimizados para demo (detección rápida)
     * 
     * Maneja errores de BD sin propagar excepciones
     */
    @Scheduled(fixedRate = 15000) // Cada 15 segundos
    public void checkInactiveDevices() {
        try {
            // Solo loguear en debug si la BD está disponible
            if (deviceService.isDatabaseAvailable()) {
                log.debug("🔍 Verificando dispositivos inactivos...");
            }
            
            deviceService.checkInactiveDevices();
            
            // Si antes falló y ahora funciona, notificar
            if (lastCheckFailed) {
                log.info("✅ Verificación de dispositivos restaurada");
                lastCheckFailed = false;
            }
            
        } catch (Exception e) {
            // Solo loguear el primer fallo, luego silenciar
            if (!lastCheckFailed) {
                log.warn("⚠️ No se pudo verificar dispositivos inactivos: {}", e.getMessage());
                lastCheckFailed = true;
            }
            // No propagar la excepción - el scheduler debe continuar
        }
    }
}
