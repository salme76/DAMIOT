package com.damiot.android.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para un dispositivo ESP32
 * 
 * Representa la información de un dispositivo IoT registrado en el sistema.
 * Los campos coinciden con la entidad DeviceStatus del backend.
 * 
 * @property id Identificador único en la base de datos
 * @property name Nombre descriptivo del dispositivo (ej: "ESP32-Salón")
 * @property macAddress Dirección MAC única del ESP32
 * @property ipAddress Dirección IP actual (puede cambiar con DHCP)
 * @property status Estado de conexión: "online" u "offline"
 * @property isEnabled Si el dispositivo está habilitado para mostrarse
 * @property lastConnection Última vez que el dispositivo envió heartbeat
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
data class Device(
    val id: Long,
    val name: String,
    val macAddress: String,
    val ipAddress: String?,
    val status: String,
    @SerializedName("isEnabled")
    val isEnabled: Boolean,
    val lastConnection: String?
) {
    /**
     * Comprueba si el dispositivo está conectado
     * @return true si el estado es "online"
     */
    fun isOnline(): Boolean = status.equals("online", ignoreCase = true)
}
