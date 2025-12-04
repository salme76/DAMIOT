package com.damiot.android.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiot.android.data.model.Device
import com.damiot.android.data.preferences.PreferencesManager
import com.damiot.android.data.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estados posibles de la pantalla principal
 */
sealed class HomeUiState {
    /** Cargando datos */
    object Loading : HomeUiState()
    /** Datos cargados correctamente */
    data class Success(val devices: List<Device>) : HomeUiState()
    /** Error al cargar datos */
    data class Error(val message: String) : HomeUiState()
}

/**
 * ViewModel de la pantalla principal
 * 
 * Gestiona el estado de la lista de dispositivos y las operaciones
 * de carga y refresco de datos.
 * 
 * Incluye refresco automático cada 10 segundos para detectar
 * cambios de estado de los dispositivos (online/offline).
 * 
 * @property repository Repositorio para acceder a los datos
 * @property preferencesManager Gestor de preferencias (para cambio de tema)
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DeviceRepository,
    val preferencesManager: PreferencesManager
) : ViewModel() {
    
    /** Estado de la UI observable */
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    /** Job para el refresco automático */
    private var autoRefreshJob: Job? = null
    
    /** Intervalo de refresco automático en milisegundos (10 segundos) */
    private val autoRefreshInterval = 10_000L
    
    init {
        // Cargar dispositivos al crear el ViewModel
        loadDevices()
        // Iniciar refresco automático
        startAutoRefresh()
    }
    
    /**
     * Carga inicial de dispositivos
     * Muestra indicador de carga mientras obtiene los datos
     */
    fun loadDevices() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            fetchDevices()
        }
    }
    
    /**
     * Refresco de dispositivos (sin indicador de carga)
     * Mantiene los datos actuales mientras obtiene los nuevos
     */
    fun refresh() {
        viewModelScope.launch {
            fetchDevices()
        }
    }
    
    /**
     * Inicia el refresco automático periódico
     * Actualiza los dispositivos cada 10 segundos para detectar
     * cambios de estado (online/offline)
     */
    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(autoRefreshInterval)
                // Solo refrescar si no estamos en estado de carga
                if (_uiState.value !is HomeUiState.Loading) {
                    fetchDevicesSilently()
                }
            }
        }
    }
    
    /**
     * Detiene el refresco automático
     */
    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }
    
    /**
     * Reinicia el refresco automático
     */
    fun resumeAutoRefresh() {
        if (autoRefreshJob == null || autoRefreshJob?.isActive != true) {
            startAutoRefresh()
        }
    }
    
    /**
     * Obtiene los dispositivos del repositorio
     * Filtra solo los dispositivos habilitados (isEnabled = true)
     */
    private suspend fun fetchDevices() {
        repository.getDevices()
            .onSuccess { allDevices ->
                // Filtrar solo dispositivos habilitados
                val activeDevices = allDevices.filter { it.isEnabled }
                _uiState.value = HomeUiState.Success(activeDevices)
            }
            .onFailure { exception ->
                _uiState.value = HomeUiState.Error(
                    exception.message ?: "Error al cargar dispositivos"
                )
            }
    }
    
    /**
     * Refresco silencioso sin cambiar estado a Loading ni Error
     * Solo actualiza si hay éxito, mantiene datos anteriores si falla
     */
    private suspend fun fetchDevicesSilently() {
        repository.getDevices()
            .onSuccess { allDevices ->
                // Filtrar solo dispositivos habilitados
                val activeDevices = allDevices.filter { it.isEnabled }
                _uiState.value = HomeUiState.Success(activeDevices)
            }
        // En caso de fallo, simplemente no actualizamos (silencioso)
    }
    
    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}
