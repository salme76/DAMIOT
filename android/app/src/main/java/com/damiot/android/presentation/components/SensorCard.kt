package com.damiot.android.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.damiot.android.data.model.SensorReading

/**
 * Tarjeta para mostrar la lectura de un sensor
 * 
 * Muestra el valor actual de un sensor con su icono correspondiente,
 * unidad de medida y timestamp de la última lectura.
 * 
 * Comportamiento según estado del dispositivo:
 * - Online: Colores normales (rojo para temperatura, azul para humedad, etc.)
 * - Offline: Todo en escala de grises para indicar datos no actualizados
 * 
 * Sensores soportados:
 * - temperatura: Termómetro (°C)
 * - humedad: Gota de agua (%)
 * - higrómetro_suelo: Planta (ADC)
 * - distancia: Regla (cm)
 * - co2: Aire (ppm)
 * 
 * @param sensorReading Datos de la lectura del sensor
 * @param isDeviceOnline Si el dispositivo está conectado (afecta colores)
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@Composable
fun SensorCard(
    sensorReading: SensorReading,
    isDeviceOnline: Boolean = true
) {
    // Obtener icono y color según el tipo de sensor
    val (icon, originalColor, displayName) = getSensorVisuals(sensorReading.sensorType)
    
    // Color gris para cuando el dispositivo está offline
    val offlineGray = Color(0xFF9E9E9E)
    
    // Usar color original si online, gris si offline
    val color = if (isDeviceOnline) originalColor else offlineGray
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del sensor con fondo circular
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = color.copy(alpha = 0.15f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = displayName,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del sensor
            Column(modifier = Modifier.weight(1f)) {
                // Nombre del sensor
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDeviceOnline) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        offlineGray
                    }
                )
                
                // Timestamp de la lectura
                Text(
                    text = formatTimestamp(sensorReading.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDeviceOnline) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        offlineGray.copy(alpha = 0.7f)
                    }
                )
            }
            
            // Valor de la lectura
            Text(
                text = "${formatValue(sensorReading.value)} ${sensorReading.unit}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = color
            )
        }
    }
}

/**
 * Obtiene los elementos visuales según el tipo de sensor
 * @param sensorType Tipo de sensor
 * @return Triple con (icono, color, nombre para mostrar)
 */
private fun getSensorVisuals(sensorType: String): Triple<ImageVector, Color, String> {
    return when (sensorType.lowercase()) {
        "temperatura" -> Triple(
            Icons.Default.Thermostat,
            Color(0xFFE53935), // Rojo
            "Temperatura"
        )
        "humedad" -> Triple(
            Icons.Default.WaterDrop,
            Color(0xFF1E88E5), // Azul
            "Humedad"
        )
        "higrómetro_suelo", "higrometro_suelo" -> Triple(
            Icons.Default.Grass,
            Color(0xFF43A047), // Verde
            "Humedad del Suelo"
        )
        "distancia" -> Triple(
            Icons.Default.Straighten,
            Color(0xFFFF6F00), // Naranja
            "Distancia"
        )
        "co2" -> Triple(
            Icons.Default.Air,
            Color(0xFF7B1FA2), // Púrpura
            "CO₂"
        )
        else -> Triple(
            Icons.Default.Thermostat,
            Color(0xFF757575), // Gris
            sensorType.replaceFirstChar { it.uppercase() }
        )
    }
}

/**
 * Formatea el valor numérico para mostrar
 * Muestra entero si no tiene decimales, o con 1 decimal si los tiene
 */
private fun formatValue(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format("%.1f", value)
    }
}

/**
 * Formatea el timestamp ISO 8601 a formato legible
 * Entrada: "2025-01-15T10:30:45"
 * Salida: "15/01/2025 10:30"
 */
private fun formatTimestamp(timestamp: String): String {
    return try {
        // Formato: 2025-01-15T10:30:45
        val parts = timestamp.split("T")
        if (parts.size == 2) {
            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")
            if (dateParts.size == 3 && timeParts.size >= 2) {
                "${dateParts[2]}/${dateParts[1]}/${dateParts[0]} ${timeParts[0]}:${timeParts[1]}"
            } else {
                timestamp
            }
        } else {
            timestamp
        }
    } catch (e: Exception) {
        timestamp
    }
}
