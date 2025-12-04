package com.damiot.android.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.damiot.android.R
import com.damiot.android.data.model.Device

/**
 * Tarjeta de dispositivo para la pantalla principal
 * 
 * Muestra la información básica de un dispositivo ESP32:
 * - Imagen del ESP32
 * - Nombre del dispositivo
 * - Dirección IP
 * - Estado de conexión (online/offline)
 * 
 * @param device Datos del dispositivo a mostrar
 * @param onClick Callback cuando se pulsa la tarjeta
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@Composable
fun DeviceCard(
    device: Device,
    onClick: () -> Unit
) {
    // Colores según el estado de conexión
    val statusColor = if (device.isOnline()) {
        Color(0xFF4CAF50) // Verde para online
    } else {
        Color(0xFFF44336) // Rojo para offline
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del ESP32
            Icon(
                painter = painterResource(id = R.drawable.esp32iot),
                contentDescription = "ESP32",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                tint = Color.Unspecified // Mantener colores originales de la imagen
            )
            
            Spacer(modifier = Modifier.width(20.dp))
            
            // Información del dispositivo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre del dispositivo
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Dirección IP (si está disponible)
                device.ipAddress?.let { ip ->
                    Text(
                        text = "IP: $ip",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Badge de estado (Online/Offline)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f) // Fondo semitransparente
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Indicador circular de color
                        Surface(
                            modifier = Modifier.size(10.dp),
                            shape = RoundedCornerShape(50),
                            color = statusColor
                        ) {}
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Texto del estado
                        Text(
                            text = if (device.isOnline()) "Online" else "Offline",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }
                }
            }
        }
    }
}
