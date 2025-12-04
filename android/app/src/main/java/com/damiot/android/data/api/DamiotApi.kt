package com.damiot.android.data.api

import com.damiot.android.data.model.ActuatorCommand
import com.damiot.android.data.model.ActuatorState
import com.damiot.android.data.model.Device
import com.damiot.android.data.model.SensorReading
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de la API REST del backend DAMIOT
 * 
 * Define todos los endpoints disponibles para comunicarse con el
 * servidor Spring Boot. Retrofit genera la implementación automáticamente.
 * 
 * Base URL: http://192.168.8.136:8080/
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
interface DamiotApi {
    
    // ==================== DISPOSITIVOS ====================
    
    /**
     * Obtiene todos los dispositivos registrados
     * @return Lista de dispositivos
     */
    @GET("api/devices")
    suspend fun getDevices(): Response<List<Device>>
    
    /**
     * Obtiene un dispositivo por su ID
     * @param id ID del dispositivo
     * @return Dispositivo encontrado
     */
    @GET("api/devices/{id}")
    suspend fun getDevice(@Path("id") id: Long): Response<Device>
    
    /**
     * Habilita o deshabilita un dispositivo
     * Los dispositivos deshabilitados no aparecen en la pantalla principal
     * @param id ID del dispositivo
     * @param enabled true para habilitar, false para deshabilitar
     * @return Dispositivo actualizado
     */
    @PUT("api/devices/{id}/toggle")
    suspend fun toggleDevice(
        @Path("id") id: Long,
        @Query("enabled") enabled: Boolean
    ): Response<Device>
    
    // ==================== SENSORES ====================
    
    /**
     * Obtiene las últimas lecturas de todos los sensores de un dispositivo
     * @param deviceId ID del dispositivo
     * @return Mapa con tipo de sensor como clave y lectura como valor
     */
    @GET("api/sensors/device/{deviceId}/latest")
    suspend fun getLatestSensorReadings(
        @Path("deviceId") deviceId: Long
    ): Response<Map<String, SensorReading>>
    
    // ==================== ACTUADORES ====================
    
    /**
     * Obtiene todos los actuadores de un dispositivo
     * @param deviceId ID del dispositivo
     * @return Lista de estados de actuadores
     */
    @GET("api/actuators/device/{deviceId}")
    suspend fun getActuatorStates(
        @Path("deviceId") deviceId: Long
    ): Response<List<ActuatorState>>
    
    /**
     * Envía un comando a un actuador
     * El backend reenvía el comando al ESP32 via MQTT
     * @param command Objeto con deviceId, actuatorType y comando (ON/OFF)
     * @return Estado actualizado del actuador
     */
    @POST("api/actuators/command")
    suspend fun sendActuatorCommand(
        @Body command: ActuatorCommand
    ): Response<ActuatorState>
}
