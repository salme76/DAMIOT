package com.damiot.android.data.model

/**
 * Modelo de datos para una lectura de sensor
 * 
 * Representa una medición tomada por un sensor del ESP32.
 * 
 * @property id Identificador único de la lectura
 * @property sensorType Tipo de sensor: "temperatura", "humedad", "higrómetro_suelo"
 * @property value Valor numérico de la lectura
 * @property unit Unidad de medida: "°C", "%", "ADC"
 * @property timestamp Momento en que se tomó la lectura (ISO 8601)
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
data class SensorReading(
    val id: Long,
    val sensorType: String,
    val value: Double,
    val unit: String,
    val timestamp: String
)
