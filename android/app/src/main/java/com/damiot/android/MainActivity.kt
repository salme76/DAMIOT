package com.damiot.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.damiot.android.data.preferences.PreferencesManager
import com.damiot.android.presentation.navigation.Screen
import com.damiot.android.presentation.screens.about.AboutScreen
import com.damiot.android.presentation.screens.detail.DetailScreen
import com.damiot.android.presentation.screens.home.HomeScreen
import com.damiot.android.presentation.screens.settings.SettingsScreen
import com.damiot.android.presentation.screens.splash.SplashScreen
import com.damiot.android.presentation.theme.DAMIOTTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Activity principal de la aplicación DAMIOT
 * 
 * Esta es la única Activity de la aplicación. Utiliza Jetpack Compose
 * para la interfaz de usuario y Navigation Compose para la navegación
 * entre pantallas.
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    /**
     * Gestor de preferencias inyectado por Hilt
     * Se usa para obtener la preferencia del modo oscuro
     */
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilitar diseño edge-to-edge (contenido bajo la barra de estado)
        enableEdgeToEdge()
        
        setContent {
            // Observar la preferencia de modo oscuro guardada
            val isDarkMode by preferencesManager.isDarkMode.collectAsState(initial = null)
            
            // Obtener el tema del sistema como fallback
            val systemDarkMode = isSystemInDarkTheme()
            
            // Usar preferencia guardada, o tema del sistema si no hay preferencia
            val effectiveDarkMode = isDarkMode ?: systemDarkMode
            
            // Aplicar el tema de la aplicación
            DAMIOTTheme(darkTheme = isDarkMode) {
                // Controlador de navegación
                val navController = rememberNavController()
                
                // Obtener la ruta actual para el manejo del botón atrás
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route
                
                // Manejar el botón atrás del sistema Android
                BackHandler {
                    when {
                        // Si estamos en Splash o Home, cerrar la aplicación
                        currentRoute == Screen.Splash.route -> finish()
                        currentRoute == Screen.Home.route -> finish()
                        // En otras pantallas, volver atrás
                        else -> navController.popBackStack()
                    }
                }
                
                // Grafo de navegación de la aplicación
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    // Pantalla de Splash - Logo y animación
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onSplashFinished = {
                                // Navegar a Home y eliminar Splash del backstack
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    
                    // Pantalla principal - Lista de dispositivos
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onDeviceClick = { deviceId ->
                                navController.navigate(Screen.Detail.createRoute(deviceId))
                            },
                            onSettingsClick = {
                                navController.navigate(Screen.Settings.route)
                            },
                            isDarkMode = effectiveDarkMode,
                            onToggleDarkMode = { }
                        )
                    }
                    
                    // Pantalla de detalle - Sensores y actuadores del dispositivo
                    composable(
                        route = Screen.Detail.route,
                        arguments = listOf(
                            navArgument("deviceId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val deviceId = backStackEntry.arguments?.getLong("deviceId") ?: 0L
                        DetailScreen(
                            deviceId = deviceId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    
                    // Pantalla de configuración - Administrar dispositivos
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            onBackClick = { navController.popBackStack() },
                            onAboutClick = { navController.navigate(Screen.About.route) }
                        )
                    }
                    
                    // Pantalla Acerca de - Información del proyecto
                    composable(Screen.About.route) {
                        AboutScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
