package com.damiot.android.presentation.screens.home

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.damiot.android.presentation.components.DeviceCard
import kotlinx.coroutines.launch

/**
 * Pantalla principal - Lista de dispositivos
 * 
 * Muestra todos los dispositivos ESP32 habilitados con su estado
 * de conexión. Permite acceder al detalle de cada dispositivo
 * y al menú de configuración.
 * 
 * Funcionalidades:
 * - Lista de dispositivos activos
 * - Actualización manual con botón refresh
 * - Auto-refresh cada 10 segundos
 * - Menú con opciones de configuración y cambio de tema
 * 
 * @param onDeviceClick Callback al pulsar un dispositivo (navega a detalle)
 * @param onSettingsClick Callback al pulsar "Administrar dispositivos"
 * @param isDarkMode Estado actual del modo oscuro
 * @param onToggleDarkMode Callback para cambiar el tema (no usado, se maneja internamente)
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDeviceClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Estados del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    // Contexto para cerrar la app
    val context = LocalContext.current
    
    // Control del menú desplegable
    var showMenu by remember { mutableStateOf(false) }
    
    // Scope para corrutinas (cambiar tema)
    val scope = rememberCoroutineScope()
    
    // Obtener PreferencesManager del ViewModel para cambiar tema
    val preferencesManager = viewModel.preferencesManager
    
    // Recargar dispositivos cuando la pantalla vuelve a estar visible
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }
    
    // El auto-refresh ya está en el ViewModel (cada 10 segundos)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sistema IoT DAMIOT") },
                // Botón de salir (izquierda)
                navigationIcon = {
                    IconButton(
                        onClick = { (context as? Activity)?.finish() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Salir de la aplicación"
                        )
                    }
                },
                // Acciones (derecha)
                actions = {
                    // Menú desplegable
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menú de opciones"
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // Opción: Administrar dispositivos
                            DropdownMenuItem(
                                text = { Text("Administrar dispositivos") },
                                onClick = {
                                    showMenu = false
                                    onSettingsClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null
                                    )
                                }
                            )
                            
                            HorizontalDivider()
                            
                            // Opción: Cambiar tema (oscuro/claro)
                            DropdownMenuItem(
                                text = { 
                                    Text(if (isDarkMode) "Modo claro" else "Modo oscuro") 
                                },
                                onClick = {
                                    showMenu = false
                                    scope.launch {
                                        preferencesManager.toggleDarkMode(isDarkMode)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isDarkMode) 
                                            Icons.Default.LightMode 
                                        else 
                                            Icons.Default.DarkMode,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                },
                // Colores de la barra
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        // Contenido según el estado
        when (val state = uiState) {
            // Estado: Cargando
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Estado: Éxito
            is HomeUiState.Success -> {
                if (state.devices.isEmpty()) {
                    // Sin dispositivos activos
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay dispositivos activos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Lista de dispositivos
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.devices) { device ->
                            DeviceCard(
                                device = device,
                                onClick = { onDeviceClick(device.id) }
                            )
                        }
                    }
                }
            }
            
            // Estado: Error
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(onClick = { viewModel.loadDevices() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}
