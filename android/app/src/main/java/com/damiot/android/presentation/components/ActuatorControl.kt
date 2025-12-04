package com.damiot.android.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Garage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.damiot.android.data.model.ActuatorState

/**
 * Control de actuador con interruptor ON/OFF
 * 
 * Permite controlar actuadores del ESP32 (LEDs, bombas, puertas, etc.)
 * mediante un switch que envía comandos al backend.
 * 
 * Comportamiento según estado del dispositivo:
 * - Online: Colores normales y switch habilitado
 * - Offline: Todo en escala de grises y switch deshabilitado
 * 
 * Actuadores soportados:
 * - led_azul: LED Azul (icono bombilla)
 * - led_verde: LED Verde (icono bombilla)
 * - luz_garaje: Luz del garaje (icono bombilla amarilla)
 * - bomba_riego: Bomba de riego (icono agua)
 * - puerta_garaje: Puerta del garaje (icono puerta)
 * - ventilador: Ventilador (icono aire)
 * 
 * @param actuatorState Estado actual del actuador
 * @param isDeviceOnline Si el dispositivo está conectado (habilita/deshabilita control)
 * @param onToggle Callback cuando se cambia el switch
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@Composable
fun ActuatorControl(
    actuatorState: ActuatorState,
    isDeviceOnline: Boolean = true,
    onToggle: (Boolean) -> Unit
) {
    // Obtener visuales según el tipo de actuador
    val (icon, iconOff, originalColor, stateName) = getActuatorVisuals(actuatorState.actuatorType, actuatorState.state)
    
    // Color gris para cuando el dispositivo está offline
    val offlineGray = Color(0xFF9E9E9E)
    
    // El switch solo está habilitado si el dispositivo está online
    val isEnabled = isDeviceOnline
    
    // Usar color original si online, gris si offline
    val color = if (isDeviceOnline) originalColor else offlineGray
    
    // Determinar si está activo (puede ser ON, OPEN, etc.)
    val isActive = actuatorState.isActive()
    
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
            // Icono del actuador (cambia según estado ON/OFF)
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = if (isDeviceOnline && isActive) {
                    color.copy(alpha = 0.15f)
                } else {
                    if (isDeviceOnline) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        offlineGray.copy(alpha = 0.15f)
                    }
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = if (isActive) icon else iconOff,
                        contentDescription = actuatorState.getDisplayName(),
                        tint = if (isDeviceOnline) {
                            if (isActive) color else MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            offlineGray
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del actuador
            Column(modifier = Modifier.weight(1f)) {
                // Nombre del actuador
                Text(
                    text = actuatorState.getDisplayName(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDeviceOnline) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        offlineGray
                    }
                )
                
                // Estado actual
                Text(
                    text = stateName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                    color = if (isDeviceOnline) {
                        if (isActive) color else MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        offlineGray.copy(alpha = 0.7f)
                    }
                )
            }
            
            // Switch para controlar el actuador
            Switch(
                checked = isActive,
                onCheckedChange = onToggle,
                enabled = isEnabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = if (isDeviceOnline) color else offlineGray,
                    checkedTrackColor = if (isDeviceOnline) color.copy(alpha = 0.5f) else offlineGray.copy(alpha = 0.3f),
                    disabledCheckedThumbColor = offlineGray,
                    disabledCheckedTrackColor = offlineGray.copy(alpha = 0.3f),
                    disabledUncheckedThumbColor = offlineGray,
                    disabledUncheckedTrackColor = offlineGray.copy(alpha = 0.2f)
                )
            )
        }
    }
}

/**
 * Obtiene los elementos visuales según el tipo de actuador
 * @param actuatorType Tipo de actuador
 * @param state Estado actual (ON, OFF, OPEN, CLOSED)
 * @return Cuádruple con (icono encendido, icono apagado, color, nombre del estado)
 */
private fun getActuatorVisuals(actuatorType: String, state: String): Quad<ImageVector, ImageVector, Color, String> {
    return when (actuatorType.lowercase()) {
        "led_azul" -> Quad(
            Icons.Filled.Lightbulb,
            Icons.Outlined.Lightbulb,
            Color(0xFF2196F3), // Azul
            if (state.uppercase() == "ON") "Encendido" else "Apagado"
        )
        "led_verde" -> Quad(
            Icons.Filled.Lightbulb,
            Icons.Outlined.Lightbulb,
            Color(0xFF4CAF50), // Verde
            if (state.uppercase() == "ON") "Encendido" else "Apagado"
        )
        "luz_garaje" -> Quad(
            Icons.Filled.Lightbulb,
            Icons.Outlined.Lightbulb,
            Color(0xFFFFC107), // Amarillo/Ámbar
            if (state.uppercase() == "ON") "Encendida" else "Apagada"
        )
        "bomba_riego" -> Quad(
            Icons.Filled.Water,
            Icons.Filled.Water,
            Color(0xFF00BCD4), // Cian
            if (state.uppercase() == "ON") "Activa" else "Inactiva"
        )
        "puerta_garaje" -> Quad(
            Icons.Filled.Garage,
            Icons.Outlined.Garage,
            Color(0xFF795548), // Marrón
            if (state.uppercase() == "OPEN") "Abierta" else "Cerrada"
        )
        "ventilador" -> Quad(
            Icons.Filled.Air,
            Icons.Filled.Air,
            Color(0xFF03A9F4), // Azul claro
            if (state.uppercase() == "ON") "Funcionando" else "Apagado"
        )
        else -> Quad(
            Icons.Filled.Lightbulb,
            Icons.Outlined.Lightbulb,
            Color(0xFF9E9E9E), // Gris
            if (state.uppercase() == "ON") "Encendido" else "Apagado"
        )
    }
}

/**
 * Clase auxiliar para devolver 4 valores
 */
private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Extensión para determinar si el actuador está activo
 * Soporta estados: ON, OPEN
 */
private fun ActuatorState.isActive(): Boolean {
    return when (state.uppercase()) {
        "ON", "OPEN" -> true
        else -> false
    }
}
