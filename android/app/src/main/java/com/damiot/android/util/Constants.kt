package com.damiot.android.util

/**
 * Constantes globales de la aplicación
 * 
 * Centraliza valores que se usan en múltiples lugares para
 * facilitar su mantenimiento y evitar errores de tipeo.
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
object Constants {
    
    /**
     * Configuración de red
     * 
     * BASE_URL: Dirección del servidor backend Spring Boot
     * Cambiar esta URL si el servidor está en otra dirección
     */
    object Network {
        const val BASE_URL = "http://192.168.8.136:8080/"
    }
    
    /**
     * Comandos para actuadores
     * 
     * ON: Encender actuador (LED, bomba, etc.)
     * OFF: Apagar actuador
     */
    object ActuatorCommands {
        const val ON = "ON"
        const val OFF = "OFF"
    }
}
