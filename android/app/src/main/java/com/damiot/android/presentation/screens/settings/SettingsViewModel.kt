package com.damiot.android.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiot.android.data.model.Device
import com.damiot.android.data.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estados posibles de la pantalla de configuración
 */
sealed class SettingsUiState {
    /** Cargando datos */
    object Loading : SettingsUiState()
    /** Datos cargados correctamente */
    data class Success(val devices: List<Device>) : SettingsUiState()
    /** Error al cargar datos */
    data class Error(val message: String) : SettingsUiState()
}

/**
 * ViewModel de la pantalla de configuración
 * 
 * Gestiona la lista de TODOS los dispositivos (habilitados y no habilitados)
 * y permite cambiar el estado de habilitación de cada uno.
 * 
 * @property repository Repositorio para acceder a los datos
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {
    
    /** Estado de la UI observable */
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        // Cargar todos los dispositivos al crear el ViewModel
        loadAllDevices()
    }
    
    /**
     * Carga todos los dispositivos del sistema
     * (tanto habilitados como deshabilitados)
     */
    fun loadAllDevices() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            
            repository.getDevices()
                .onSuccess { devices ->
                    _uiState.value = SettingsUiState.Success(devices)
                }
                .onFailure { exception ->
                    _uiState.value = SettingsUiState.Error(
                        exception.message ?: "Error al cargar dispositivos"
                    )
                }
        }
    }
    
    /**
     * Habilita o deshabilita un dispositivo
     * 
     * @param deviceId ID del dispositivo
     * @param enabled true para habilitar, false para deshabilitar
     */
    fun toggleDevice(deviceId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleDevice(deviceId, enabled)
                .onSuccess {
                    // Recargar lista para reflejar el cambio
                    loadAllDevices()
                }
                .onFailure { exception ->
                    _uiState.value = SettingsUiState.Error(
                        "Error al actualizar dispositivo: ${exception.message}"
                    )
                }
        }
    }
}
