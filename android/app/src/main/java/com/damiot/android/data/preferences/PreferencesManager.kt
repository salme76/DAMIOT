package com.damiot.android.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extensión para crear el DataStore de preferencias
 * El nombre "settings" será el archivo de preferencias
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Gestor de preferencias de la aplicación
 * 
 * Utiliza DataStore (reemplazo moderno de SharedPreferences) para
 * almacenar preferencias de forma persistente y asíncrona.
 * 
 * Preferencias disponibles:
 * - Modo oscuro: Guarda si el usuario prefiere tema oscuro o claro
 * 
 * @property context Contexto de la aplicación inyectado por Hilt
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        /** Clave para la preferencia del modo oscuro */
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }
    
    /**
     * Flow que emite el estado actual del modo oscuro
     * 
     * null = No hay preferencia guardada, usar tema del sistema
     * true = Modo oscuro activado
     * false = Modo claro activado
     */
    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY]
    }
    
    /**
     * Guarda la preferencia del modo oscuro
     * @param enabled true para modo oscuro, false para modo claro
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }
    
    /**
     * Alterna entre modo oscuro y claro
     * @param currentValue Valor actual del modo oscuro
     */
    suspend fun toggleDarkMode(currentValue: Boolean) {
        setDarkMode(!currentValue)
    }
}
