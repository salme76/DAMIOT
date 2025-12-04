package com.damiot.android.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.damiot.android.data.model.Device

/**
 * Pantalla de administración de dispositivos
 * 
 * Permite habilitar o deshabilitar dispositivos del sistema.
 * Los dispositivos deshabilitados no aparecen en la pantalla principal.
 * 
 * @param onBackClick Callback para volver a la pantalla anterior
 * @param onAboutClick Callback para ir a la pantalla "Acerca de"
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onAboutClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrar dispositivos") },
                // Botón volver
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver"
                        )
                    }
                },
                // Botón info
                actions = {
                    IconButton(onClick = onAboutClick) {
                        Icon(
                            imageVector = Icons.Default.Info, 
                            contentDescription = "Acerca de"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            // Estado: Cargando
            is SettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Estado: Datos cargados
            is SettingsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Título de sección
                    item {
                        Text(
                            text = "Gestión de dispositivos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Activa o desactiva dispositivos del sistema",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    
                    // Lista de dispositivos
                    items(state.devices) { device ->
                        DeviceSettingsCard(
                            device = device,
                            onToggle = { enabled ->
                                viewModel.toggleDevice(device.id, enabled)
                            }
                        )
                    }
                    
                    // Botón "Acerca de"
                    item {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                        
                        OutlinedButton(
                            onClick = onAboutClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Acerca de DAMIOT")
                        }
                    }
                }
            }
            
            // Estado: Error
            is SettingsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message, 
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAllDevices() }) { 
                            Text("Reintentar") 
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de dispositivo para la pantalla de configuración
 * 
 * Muestra información del dispositivo y un switch para habilitarlo/deshabilitarlo.
 * 
 * @param device Datos del dispositivo
 * @param onToggle Callback cuando se cambia el switch
 */
@Composable
fun DeviceSettingsCard(
    device: Device, 
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Información del dispositivo
                Column(Modifier.weight(1f)) {
                    Text(
                        text = device.name, 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "MAC: ${device.macAddress}", 
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "IP: ${device.ipAddress ?: "N/A"}", 
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Estado: ${if (device.isOnline()) "Online" else "Offline"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (device.isOnline()) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                }
                
                // Switch para habilitar/deshabilitar
                Switch(
                    checked = device.isEnabled, 
                    onCheckedChange = onToggle
                )
            }
        }
    }
}
