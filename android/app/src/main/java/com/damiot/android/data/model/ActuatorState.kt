package com.damiot.android.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para el estado de un actuador
 * 
 * Representa el estado actual de un actuador (LED, bomba, etc.)
 * conectado a un dispositivo ESP32.
 * 
 * @property id Identificador único en la base de datos
 * @property deviceId ID del dispositivo al que pertenece
 * @property actuatorType Tipo de actuador: "led_azul", "led_verde", "bomba_riego"
 * @property state Estado actual: "ON" u "OFF"
 * @property updatedAt Última actualización del estado (ISO 8601)
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
data class ActuatorState(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("deviceId")
    val deviceId: Long,
    
    @SerializedName("actuatorType")
    val actuatorType: String,
    
    @SerializedName("state")
    val state: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String
) {
    /**
     * Comprueba si el actuador está encendido
     * @return true si el estado es "ON"
     */
    fun isOn(): Boolean = state.equals("ON", ignoreCase = true)
    
    /**
     * Obtiene el nombre legible del tipo de actuador
     * Traduce los identificadores técnicos a nombres en español
     * @return Nombre formateado para mostrar al usuario
     */
    fun getDisplayName(): String {
        return when (actuatorType.lowercase()) {
            "led_azul" -> "LED Azul"
            "led_verde" -> "LED Verde"
            "bomba_riego" -> "Bomba de Riego"
            else -> actuatorType.replace("_", " ")
                .replaceFirstChar { it.uppercase() }
        }
    }
}

/**
 * Comando para enviar a un actuador
 * 
 * Se envía al backend para cambiar el estado de un actuador.
 * El backend traduce el comando y lo envía al ESP32 via MQTT.
 * 
 * @property deviceId ID del dispositivo destino
 * @property actuatorType Tipo de actuador a controlar
 * @property command Comando a enviar: "ON" u "OFF"
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
data class ActuatorCommand(
    @SerializedName("deviceId")
    val deviceId: Long,
    
    @SerializedName("actuatorType")
    val actuatorType: String,
    
    @SerializedName("command")
    val command: String
)
