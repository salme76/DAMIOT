package com.damiot.android.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.damiot.android.presentation.components.ActuatorControl
import com.damiot.android.presentation.components.SensorCard
import com.damiot.android.util.Constants

/**
 * Pantalla de detalle de un dispositivo
 * 
 * Muestra la información completa de un dispositivo ESP32:
 * - Estado de conexión (banner si está offline)
 * - Lecturas de sensores (temperatura, humedad, etc.)
 * - Controles de actuadores (LEDs, bombas, etc.)
 * 
 * @param deviceId ID del dispositivo a mostrar
 * @param onBackClick Callback para volver a la pantalla anterior
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    deviceId: Long,
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    // Estados del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val isDeviceOnline = uiState.device?.isOnline() ?: false
    
    // Host para mostrar mensajes Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Mostrar Snackbar cuando hay mensaje del ViewModel
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbar()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                // Título: nombre del dispositivo
                title = { Text(uiState.device?.name ?: "Dispositivo") },
                // Botón volver
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver"
                        )
                    }
                },
                // Botón actualizar
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshManually() },
                        enabled = !uiState.isRefreshing
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh, 
                                contentDescription = "Actualizar datos"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            // Estado: Cargando inicial (sin datos previos)
            uiState.isLoading && uiState.device == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Estado: Error
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error ?: "", 
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadDeviceData() }) { 
                            Text("Reintentar") 
                        }
                    }
                }
            }
            
            // Estado: Datos cargados (o cargando con datos previos)
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Banner de advertencia si el dispositivo está offline
                    if (!isDeviceOnline) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.errorContainer)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = "Sin conexión",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Dispositivo sin conexión",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    
                    // Lista de sensores y actuadores
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Sección: Sensores
                        item {
                            Text(
                                text = "Sensores",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        
                        if (uiState.sensorReadings.isEmpty()) {
                            item {
                                Text(
                                    text = "Sin lecturas disponibles",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(uiState.sensorReadings.values.sortedBy { it.sensorType }) { reading ->
                                SensorCard(
                                    sensorReading = reading,
                                    isDeviceOnline = isDeviceOnline
                                )
                            }
                        }
                        
                        // Sección: Actuadores
                        item {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Actuadores",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        
                        if (uiState.actuatorStates.isEmpty()) {
                            item {
                                Text(
                                    text = "Sin actuadores configurados",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(uiState.actuatorStates) { actuator ->
                                ActuatorControl(
                                    actuatorState = actuator,
                                    isDeviceOnline = isDeviceOnline,
                                    onToggle = { isOn ->
                                        // Enviar comando ON u OFF según el estado del switch
                                        val command = if (isOn) {
                                            Constants.ActuatorCommands.ON
                                        } else {
                                            Constants.ActuatorCommands.OFF
                                        }
                                        viewModel.sendActuatorCommand(
                                            actuator.actuatorType, 
                                            command
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
