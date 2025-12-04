package com.damiot.android.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Esquema de colores para modo oscuro
 * Define qué colores usar en cada rol del tema Material 3
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    error = DarkError,
    onError = DarkOnError,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface
)

/**
 * Esquema de colores para modo claro
 * Define qué colores usar en cada rol del tema Material 3
 */
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    error = LightError,
    onError = LightOnError,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface
)

/**
 * Tema principal de la aplicación DAMIOT
 * 
 * Aplica el esquema de colores y tipografía a toda la interfaz.
 * Soporta modo claro, oscuro y colores dinámicos (Material You en Android 12+).
 * 
 * @param darkTheme Control del tema:
 *   - null: Usar tema del sistema
 *   - true: Forzar modo oscuro
 *   - false: Forzar modo claro
 * @param dynamicColor Si true, usa colores dinámicos de Material You (Android 12+)
 * @param content Contenido composable que usará este tema
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@Composable
fun DAMIOTTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Si darkTheme es null, usar el tema del sistema Android
    val useDarkTheme = darkTheme ?: isSystemInDarkTheme()
    
    // Seleccionar esquema de colores según configuración
    val colorScheme = when {
        // Colores dinámicos disponibles en Android 12+ (API 31)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        // Tema oscuro personalizado
        useDarkTheme -> DarkColorScheme
        // Tema claro personalizado
        else -> LightColorScheme
    }

    // Aplicar tema Material 3
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
