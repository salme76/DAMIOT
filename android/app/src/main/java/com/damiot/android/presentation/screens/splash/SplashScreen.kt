package com.damiot.android.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.damiot.android.R
import kotlinx.coroutines.delay

/**
 * Pantalla de Splash con animaciones
 * 
 * Muestra el logo del ESP32 y el nombre DAMIOT con efectos de entrada:
 * - Logo: escala desde pequeño + fade in
 * - Letras DAMIOT: aparecen una a una con fade in
 * - Subtítulo: fade in al final
 * 
 * Duración total: ~2.5 segundos
 * 
 * @param onSplashFinished Callback cuando termina la animación
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Estado para controlar las animaciones
    var startAnimation by remember { mutableStateOf(false) }
    
    // Animación del logo (escala)
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "logoScale"
    )
    
    // Animación del logo (alpha)
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = LinearEasing
        ),
        label = "logoAlpha"
    )
    
    // Letras de DAMIOT para animar individualmente
    val letters = listOf("D", "A", "M", "I", "O", "T")
    
    // Estados de alpha para cada letra
    val letterAlphas = letters.mapIndexed { index, _ ->
        animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = 600 + (index * 150), // Cada letra aparece 150ms después
                easing = LinearEasing
            ),
            label = "letter$index"
        )
    }
    
    // Animación del subtítulo
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 1600, // Después de las letras
            easing = LinearEasing
        ),
        label = "subtitleAlpha"
    )
    
    // Iniciar animación y navegar cuando termine
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // Duración total del splash
        onSplashFinished()
    }
    
    // Colores del gradiente de fondo
    val gradientColors = listOf(
        Color(0xFF1A237E), // Azul oscuro
        Color(0xFF0D47A1), // Azul medio
        Color(0xFF1565C0)  // Azul claro
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = gradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo ESP32
            Image(
                painter = painterResource(id = R.drawable.esp32iot),
                contentDescription = "Logo DAMIOT",
                modifier = Modifier
                    .size(180.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Letras DAMIOT animadas
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                letters.forEachIndexed { index, letter ->
                    Text(
                        text = letter,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.alpha(letterAlphas[index].value)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtítulo
            Text(
                text = "Sistema IoT Multiplataforma",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.alpha(subtitleAlpha)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Autor
            Text(
                text = "IES Azarquiel - Toledo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.alpha(subtitleAlpha)
            )
        }
    }
}
