package com.damiot.android.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiot.android.data.model.ActuatorState
import com.damiot.android.data.model.Device
import com.damiot.android.data.model.SensorReading
import com.damiot.android.data.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la UI de la pantalla de detalle
 * 
 * @property isLoading Si se están cargando datos por primera vez
 * @property isRefreshing Si se está refrescando manualmente
 * @property device Información del dispositivo
 * @property sensorReadings Lecturas de sensores (tipo -> lectura)
 * @property actuatorStates Estados de actuadores
 * @property error Mensaje de error si lo hay
 * @property snackbarMessage Mensaje temporal para mostrar al usuario
 */
data class DetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val device: Device? = null,
    val sensorReadings: Map<String, SensorReading> = emptyMap(),
    val actuatorStates: List<ActuatorState> = emptyList(),
    val error: String? = null,
    val snackbarMessage: String? = null
)

/**
 * ViewModel de la pantalla de detalle
 * 
 * Gestiona la carga de datos del dispositivo (sensores y actuadores)
 * y el envío de comandos a los actuadores.
 * 
 * Incluye refresco automático cada 5 segundos para mantener
 * actualizados los datos de sensores y el estado del dispositivo.
 * 
 * @property savedStateHandle Handle para obtener argumentos de navegación
 * @property repository Repositorio para acceder a los datos
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DeviceRepository
) : ViewModel() {
    
    /** ID del dispositivo obtenido de los argumentos de navegación */
    private val deviceId: Long = savedStateHandle.get<Long>("deviceId") ?: 0L
    
    /** Estado de la UI observable */
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()
    
    /** Job para el refresco automático */
    private var autoRefreshJob: Job? = null
    
    /** Intervalo de refresco automático en milisegundos (5 segundos) */
    private val autoRefreshInterval = 5_000L
    
    init {
        // Cargar datos al crear el ViewModel
        loadDeviceData()
        // Iniciar refresco automático
        startAutoRefresh()
    }
    
    /**
     * Carga todos los datos del dispositivo:
     * - Información del dispositivo
     * - Últimas lecturas de sensores
     * - Estados de actuadores
     * 
     * Las llamadas se hacen en paralelo para mayor velocidad
     */
    fun loadDeviceData() {
        viewModelScope.launch {
            // Indicar que estamos cargando
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Cargar todo en paralelo
            val deviceDeferred = async { repository.getDevice(deviceId) }
            val sensorsDeferred = async { repository.getLatestSensorReadings(deviceId) }
            val actuatorsDeferred = async { repository.getActuatorStates(deviceId) }
            
            // Esperar resultados
            val deviceResult = deviceDeferred.await()
            val sensorsResult = sensorsDeferred.await()
            val actuatorsResult = actuatorsDeferred.await()
            
            // Procesar dispositivo
            deviceResult
                .onSuccess { device ->
                    _uiState.value = _uiState.value.copy(device = device)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al cargar dispositivo: ${exception.message}"
                    )
                    return@launch
                }
            
            // Procesar sensores
            sensorsResult.onSuccess { readings ->
                _uiState.value = _uiState.value.copy(sensorReadings = readings)
            }
            
            // Procesar actuadores
            actuatorsResult.onSuccess { states ->
                _uiState.value = _uiState.value.copy(actuatorStates = states)
            }
            
            // Indicar que terminamos de cargar
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    /**
     * Refresco manual activado por el usuario (botón refresh)
     * Muestra indicador en el botón pero mantiene el contenido visible
     */
    fun refreshManually() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            // Cargar todo en paralelo
            coroutineScope {
                val deviceDeferred = async { repository.getDevice(deviceId) }
                val sensorsDeferred = async { repository.getLatestSensorReadings(deviceId) }
                val actuatorsDeferred = async { repository.getActuatorStates(deviceId) }
                
                // Esperar y procesar resultados
                deviceDeferred.await().onSuccess { device ->
                    _uiState.value = _uiState.value.copy(device = device)
                }
                
                sensorsDeferred.await().onSuccess { readings ->
                    _uiState.value = _uiState.value.copy(sensorReadings = readings)
                }
                
                actuatorsDeferred.await().onSuccess { states ->
                    _uiState.value = _uiState.value.copy(actuatorStates = states)
                }
            }
            
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }
    
    /**
     * Inicia el refresco automático periódico
     * Actualiza los datos cada 5 segundos para mostrar lecturas
     * de sensores actualizadas y detectar cambios de estado
     */
    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(autoRefreshInterval)
                // Solo refrescar si no estamos cargando ni refrescando manualmente
                if (!_uiState.value.isLoading && !_uiState.value.isRefreshing) {
                    refreshDataSilently()
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
     * Refresco silencioso de todos los datos (para auto-refresh)
     * No cambia el estado a loading ni muestra errores
     * Las llamadas se hacen en paralelo para mayor velocidad
     */
    private suspend fun refreshDataSilently() {
        coroutineScope {
            val deviceDeferred = async { repository.getDevice(deviceId) }
            val sensorsDeferred = async { repository.getLatestSensorReadings(deviceId) }
            val actuatorsDeferred = async { repository.getActuatorStates(deviceId) }
            
            // Esperar y procesar resultados
            deviceDeferred.await().onSuccess { device ->
                _uiState.value = _uiState.value.copy(device = device)
            }
            
            sensorsDeferred.await().onSuccess { readings ->
                _uiState.value = _uiState.value.copy(sensorReadings = readings)
            }
            
            actuatorsDeferred.await().onSuccess { states ->
                _uiState.value = _uiState.value.copy(actuatorStates = states)
            }
        }
    }
    
    /**
     * Envía un comando a un actuador (encender/apagar)
     * 
     * @param actuatorType Tipo de actuador (ej: "led_azul")
     * @param command Comando a enviar: "ON" u "OFF"
     */
    fun sendActuatorCommand(actuatorType: String, command: String) {
        viewModelScope.launch {
            repository.sendActuatorCommand(deviceId, actuatorType, command)
                .onSuccess { actuatorState ->
                    // Recargar estados de actuadores para reflejar el cambio
                    repository.getActuatorStates(deviceId)
                        .onSuccess { states ->
                            _uiState.value = _uiState.value.copy(actuatorStates = states)
                        }
                    
                    // Mostrar mensaje de confirmación al usuario
                    val actionText = if (command == "ON") "encendido" else "apagado"
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "${actuatorState.getDisplayName()} $actionText"
                    )
                }
                .onFailure { exception ->
                    // Mostrar mensaje de error
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Error: ${exception.message}"
                    )
                }
        }
    }
    
    /**
     * Limpia el mensaje del Snackbar después de mostrarlo
     */
    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}
