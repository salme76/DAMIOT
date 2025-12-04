package com.damiot.android.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Definición de tipografía de la aplicación DAMIOT
 * 
 * Personaliza los estilos de texto de Material 3 para
 * mantener una apariencia consistente en toda la app.
 * 
 * Estilos definidos:
 * - bodyLarge: Texto principal de contenido
 * - titleLarge: Títulos grandes (pantallas, secciones principales)
 * - titleMedium: Títulos medianos (cards, subsecciones)
 * - labelSmall: Etiquetas pequeñas (badges, hints)
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
val Typography = Typography(
    // Texto principal de contenido
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Títulos grandes
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Títulos medianos (nombre de dispositivo en card)
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    // Etiquetas pequeñas
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
