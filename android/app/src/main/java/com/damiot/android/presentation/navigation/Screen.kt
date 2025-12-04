package com.damiot.android.presentation.navigation

/**
 * Definición de las pantallas de la aplicación
 * 
 * Cada objeto representa una pantalla con su ruta de navegación.
 * Se usa con Navigation Compose para navegar entre pantallas.
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
sealed class Screen(val route: String) {
    
    /** Pantalla de splash - Logo y animación de inicio */
    object Splash : Screen("splash")
    
    /** Pantalla principal - Lista de dispositivos activos */
    object Home : Screen("home")
    
    /** Pantalla de detalle - Muestra sensores y actuadores de un dispositivo */
    object Detail : Screen("detail/{deviceId}") {
        /**
         * Crea la ruta con el ID del dispositivo
         * @param deviceId ID del dispositivo a mostrar
         * @return Ruta completa para navegación
         */
        fun createRoute(deviceId: Long) = "detail/$deviceId"
    }
    
    /** Pantalla de configuración - Administrar dispositivos (habilitar/deshabilitar) */
    object Settings : Screen("settings")
    
    /** Pantalla Acerca de - Información del proyecto y autor */
    object About : Screen("about")
}
