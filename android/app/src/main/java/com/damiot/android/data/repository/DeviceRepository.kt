package com.damiot.android.data.repository

import com.damiot.android.data.api.DamiotApi
import com.damiot.android.data.model.ActuatorCommand
import com.damiot.android.data.model.ActuatorState
import com.damiot.android.data.model.Device
import com.damiot.android.data.model.SensorReading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para acceso a datos de dispositivos
 * 
 * Actúa como intermediario entre la capa de presentación (ViewModels)
 * y la capa de datos (API REST). Maneja las llamadas de red en un
 * hilo de background (IO) y devuelve Result para manejo de errores.
 * 
 * @property api Cliente de la API REST inyectado por Hilt
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@Singleton
class DeviceRepository @Inject constructor(
    private val api: DamiotApi
) {
    
    /**
     * Obtiene todos los dispositivos del sistema
     * @return Result con lista de dispositivos o error
     */
    suspend fun getDevices(): Result<List<Device>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getDevices()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene un dispositivo por su ID
     * @param id ID del dispositivo
     * @return Result con el dispositivo o error
     */
    suspend fun getDevice(id: Long): Result<Device> = withContext(Dispatchers.IO) {
        try {
            val response = api.getDevice(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Habilita o deshabilita un dispositivo
     * @param id ID del dispositivo
     * @param enabled Nuevo estado de habilitación
     * @return Result con el dispositivo actualizado o error
     */
    suspend fun toggleDevice(id: Long, enabled: Boolean): Result<Device> = 
        withContext(Dispatchers.IO) {
            try {
                val response = api.toggleDevice(id, enabled)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Obtiene las últimas lecturas de sensores de un dispositivo
     * @param deviceId ID del dispositivo
     * @return Result con mapa de lecturas (tipo -> lectura) o error
     */
    suspend fun getLatestSensorReadings(deviceId: Long): Result<Map<String, SensorReading>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = api.getLatestSensorReadings(deviceId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Obtiene los estados de actuadores de un dispositivo
     * @param deviceId ID del dispositivo
     * @return Result con lista de estados o error
     */
    suspend fun getActuatorStates(deviceId: Long): Result<List<ActuatorState>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = api.getActuatorStates(deviceId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Envía un comando a un actuador (encender/apagar)
     * @param deviceId ID del dispositivo
     * @param actuatorType Tipo de actuador (ej: "led_azul")
     * @param command Comando: "ON" u "OFF"
     * @return Result con el estado actualizado o error
     */
    suspend fun sendActuatorCommand(
        deviceId: Long,
        actuatorType: String,
        command: String
    ): Result<ActuatorState> = withContext(Dispatchers.IO) {
        try {
            val actuatorCommand = ActuatorCommand(deviceId, actuatorType, command)
            val response = api.sendActuatorCommand(actuatorCommand)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
