package com.damiot.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application principal de DAMIOT
 * 
 * La anotación @HiltAndroidApp inicializa Hilt para la inyección
 * de dependencias en toda la aplicación.
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@HiltAndroidApp
class DamiotApplication : Application()
