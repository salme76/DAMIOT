# Aplicación Android - DAMIOT

Aplicación móvil moderna para monitoreo y control del sistema DAMIOT IoT.

---

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Características](#características)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Configuración](#configuración)
- [Compilación e Instalación](#compilación-e-instalación)
- [Pantallas](#pantallas)
- [Componentes UI](#componentes-ui)
- [Troubleshooting](#troubleshooting)

---

## 📖 Descripción

Aplicación Android nativa desarrollada en Kotlin con Jetpack Compose que permite:
- Visualizar lecturas de sensores en tiempo real
- Controlar actuadores remotamente (LEDs, puertas, ventiladores)
- Monitorear estado de dispositivos (online/offline)
- Interfaz moderna con Material Design 3
- Pull-to-refresh para actualizar datos

---

## ✨ Características

### Funcionalidades Principales

- ✅ **Dashboard de Dispositivos:** Lista todos los ESP32 registrados
- ✅ **Monitoreo de Sensores:** Temperatura, humedad, CO₂, distancia
- ✅ **Control de Actuadores:** Switch ON/OFF para LEDs, relés, etc.
- ✅ **Detección de Offline:** Indica visualmente dispositivos desconectados
- ✅ **Actualización Manual:** Pull-to-refresh en todas las pantallas
- ✅ **Iconos Personalizados:** Icono específico para cada sensor/actuador
- ✅ **Modo Claro/Oscuro:** Soporta tema del sistema

### Iconos por Tipo

**Sensores:**
- 🌡️ Temperatura → Termómetro (rojo)
- 💧 Humedad → Gota de agua (azul)
- 🌱 Higrómetro Suelo → Planta (verde)
- 📏 Distancia → Regla (naranja)
- 💨 CO₂ → Aire (púrpura)

**Actuadores:**
- 💡 LED Azul → Bombilla (azul)
- 💡 LED Verde → Bombilla (verde)
- 💡 Luz Garaje → Bombilla (amarillo)
- 💧 Bomba Riego → Agua (cian)
- 🚪 Puerta Garaje → Puerta (marrón)
- 🌀 Ventilador → Aire (azul claro)

---

## 🏗️ Arquitectura

### Patrón MVVM + Clean Architecture

```
┌─────────────────────────────────┐
│         Presentation            │
│                                 │
│  ┌──────────┐    ┌───────────┐ │
│  │  Views   │ ←─ │ ViewModels│ │
│  │(Compose) │    │           │ │
│  └──────────┘    └─────┬─────┘ │
└────────────────────────┼────────┘
                         │
┌────────────────────────┼────────┐
│         Domain          │        │
│                         ▼        │
│              ┌────────────────┐ │
│              │  Repositories  │ │
│              └────────┬───────┘ │
└───────────────────────┼──────────┘
                        │
┌───────────────────────┼──────────┐
│          Data         │          │
│                       ▼          │
│     ┌──────────┐  ┌─────────┐   │
│     │   API    │  │  Models │   │
│     │(Retrofit)│  │   (DTO) │   │
│     └──────────┘  └─────────┘   │
└──────────────────────────────────┘
```

### Flujo de Datos

```
User Action → View → ViewModel → Repository → API → Backend
                                                      ↓
Backend Response → DTO → Repository → ViewModel → View → UI Update
```

---

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Kotlin | 1.9+ | Lenguaje principal |
| Jetpack Compose | 1.7.6 | UI moderna declarativa |
| Material Design 3 | 1.3.1 | Componentes UI |
| Hilt | 2.52 | Inyección de dependencias |
| Retrofit | 2.11.0 | Cliente HTTP |
| OkHttp | 4.12.0 | Cliente HTTP bajo nivel |
| Coroutines | 1.9.0 | Programación asíncrona |
| ViewModel | 2.8.7 | Gestión de estado |
| Navigation Compose | 2.8.5 | Navegación entre pantallas |

---

## 📁 Estructura del Proyecto

```
android/app/src/main/java/com/damiot/android/
├── data/                           # Capa de datos
│   ├── api/
│   │   └── DamiotApi.kt           # Interface Retrofit
│   ├── model/
│   │   ├── Device.kt              # Modelo Dispositivo
│   │   ├── SensorReading.kt       # Modelo Sensor
│   │   └── ActuatorState.kt       # Modelo Actuador
│   ├── preferences/
│   │   └── PreferencesManager.kt  # SharedPreferences
│   └── repository/
│       └── DeviceRepository.kt    # Repositorio principal
│
├── di/                             # Inyección de dependencias
│   ├── AppModule.kt               # Módulos Hilt
│   └── NetworkModule.kt           # Configuración Retrofit
│
├── presentation/                   # Capa de presentación
│   ├── components/                # Componentes reutilizables
│   │   ├── DeviceCard.kt         # Tarjeta de dispositivo
│   │   ├── SensorCard.kt         # Tarjeta de sensor
│   │   └── ActuatorControl.kt    # Control de actuador
│   │
│   ├── screens/                   # Pantallas principales
│   │   ├── home/
│   │   │   ├── HomeScreen.kt     # Pantalla principal
│   │   │   └── HomeViewModel.kt  # ViewModel principal
│   │   ├── detail/
│   │   │   ├── DetailScreen.kt   # Detalle dispositivo
│   │   │   └── DetailViewModel.kt
│   │   ├── settings/
│   │   │   ├── SettingsScreen.kt
│   │   │   └── SettingsViewModel.kt
│   │   ├── about/
│   │   │   └── AboutScreen.kt
│   │   └── splash/
│   │       └── SplashScreen.kt
│   │
│   ├── navigation/
│   │   └── Screen.kt             # Definición de rutas
│   │
│   └── theme/
│       ├── Color.kt              # Paleta de colores
│       ├── Theme.kt              # Tema Material 3
│       └── Type.kt               # Tipografía
│
├── util/
│   └── Constants.kt              # Constantes globales
│
├── DamiotApplication.kt          # Application class
└── MainActivity.kt               # Activity principal
```

---

## ⚙️ Configuración

### Archivo: `Constants.kt`

```kotlin
object Constants {
    // URL del backend (modificar según tu red)
    const val BASE_URL = "http://192.168.8.136:8080/"
    
    // Timeouts
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
    
    // Refresh intervals
    const val HOME_REFRESH_INTERVAL = 10000L      // 10 segundos
    const val DETAIL_REFRESH_INTERVAL = 5000L     // 5 segundos
}
```

**Cambiar IP del Backend:**
```kotlin
// Si tu backend está en otra IP
const val BASE_URL = "http://192.168.8.100:8080/"
```

### Permisos: `AndroidManifest.xml`

```xml
<!-- Permisos necesarios -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## 🔨 Compilación e Instalación

### Requisitos

- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK 17** o superior
- **Android SDK 34** (API 34)
- Dispositivo Android 7.0+ (API 24+) o emulador

### Pasos

1. **Abrir proyecto:**
```
Android Studio → Open → D:/DAMIOT/android
```

2. **Sync Gradle:**
```
File → Sync Project with Gradle Files
```

3. **Configurar IP del backend:**
```kotlin
// En util/Constants.kt
const val BASE_URL = "http://TU_IP:8080/"
```

4. **Compilar:**
```bash
# Desde terminal
cd D:/DAMIOT/android
./gradlew assembleDebug

# O desde Android Studio
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

5. **Instalar en dispositivo:**
```bash
# Via ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# O desde Android Studio
Run → Run 'app'
```

---

## 📱 Pantallas

### 1. Splash Screen
- Logo de DAMIOT
- Transición automática a Home

### 2. Home Screen
- Lista de dispositivos registrados
- Estado online/offline visual
- Resumen de sensores y actuadores
- Pull-to-refresh
- Actualización automática cada 10 segundos

### 3. Detail Screen
- Información completa del dispositivo
- Todas las lecturas de sensores
- Controles de todos los actuadores
- Pull-to-refresh
- Actualización automática cada 5 segundos

### 4. Settings Screen
- Configuración de URL del backend
- Intervalos de actualización
- Preferencias de la app

### 5. About Screen
- Información del proyecto
- Autor
- Versión de la app
- Enlaces

---

## 🎨 Componentes UI

### DeviceCard

Tarjeta resumen de dispositivo en lista principal.

```kotlin
DeviceCard(
    device = device,
    onClick = { /* navegar a detalle */ }
)
```

**Características:**
- Nombre del dispositivo
- Estado (ONLINE/OFFLINE) con badge coloreado
- Número de sensores y actuadores
- Indicador visual cuando offline (escala de grises)

### SensorCard

Tarjeta para mostrar lectura de sensor.

```kotlin
SensorCard(
    sensorReading = reading,
    isDeviceOnline = device.isOnline
)
```

**Características:**
- Icono específico por tipo de sensor
- Valor con unidad (°C, %, ppm, cm)
- Timestamp de última lectura
- Colores diferenciados
- Gris cuando dispositivo offline

### ActuatorControl

Control interactivo de actuador.

```kotlin
ActuatorControl(
    actuatorState = state,
    isDeviceOnline = device.isOnline,
    onToggle = { newState -> 
        viewModel.sendCommand(deviceId, actuatorType, newState)
    }
)
```

**Características:**
- Switch ON/OFF
- Icono que cambia según estado
- Deshabilitado cuando dispositivo offline
- Feedback visual del estado
- Estados personalizados (Abierta/Cerrada para puertas)

---

## 🔄 Gestión de Estado

### ViewModel Pattern

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {
    
    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadDevices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAllDevices()
                _devices.value = result
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

### Actualización Automática

```kotlin
// En DetailScreen.kt
LaunchedEffect(deviceId) {
    while (true) {
        viewModel.loadDeviceDetails(deviceId)
        delay(DETAIL_REFRESH_INTERVAL)
    }
}
```

---

## 🌐 Comunicación con Backend

### Retrofit Interface

```kotlin
interface DamiotApi {
    @GET("api/devices")
    suspend fun getAllDevices(): List<Device>
    
    @GET("api/devices/{id}")
    suspend fun getDevice(@Path("id") id: Long): Device
    
    @GET("api/sensors/device/{deviceId}/latest")
    suspend fun getLatestSensorReadings(
        @Path("deviceId") deviceId: Long
    ): List<SensorReading>
    
    @GET("api/actuators/device/{deviceId}")
    suspend fun getActuatorStates(
        @Path("deviceId") deviceId: Long
    ): List<ActuatorState>
    
    @POST("api/actuators/command")
    suspend fun sendActuatorCommand(
        @Body command: ActuatorCommandRequest
    ): ActuatorState
}
```

### Request/Response

**Enviar Comando:**
```kotlin
// Request
{
    "deviceId": 1,
    "actuatorType": "led_azul",
    "command": "ON"
}

// Response
{
    "id": 1,
    "deviceId": 1,
    "actuatorType": "led_azul",
    "state": "ON",
    "updatedAt": "2025-12-04T10:30:45"
}
```

---

## 🐛 Troubleshooting

### App no conecta al backend

```
Error: Unable to resolve host
Solución:
1. Verificar backend corriendo
2. Verificar IP correcta en Constants.kt
3. Verificar móvil en misma red que backend
4. Ping desde móvil: ping 192.168.8.136
5. Verificar firewall no bloquea puerto 8080
```

### Dispositivos aparecen siempre offline

```
Síntoma: Badge "OFFLINE" aunque ESP32 funciona
Solución:
1. Verificar ESP32 enviando heartbeat
2. Revisar logs del backend
3. Verificar umbral de timeout (30s)
4. Verificar timestamp del último heartbeat en BD
```

### Actuadores no responden

```
Síntoma: Switch cambia pero LED no
Solución:
1. Verificar request llega al backend (logs)
2. Verificar comando MQTT publicado
3. Verificar ESP32 suscrito al topic
4. Verificar deviceId correcto en request
```

### Iconos no se muestran correctamente

```
Síntoma: Todos los sensores muestran termómetro
Solución:
1. Verificar sensor_type en respuesta API
2. Revisar SensorCard.kt función getSensorVisuals()
3. Verificar spelling exacto (ej: "co2" no "CO2")
4. Recompilar app
```

---

## 🎨 Personalización

### Cambiar Colores

```kotlin
// En presentation/theme/Color.kt

// Tema claro
val md_theme_light_primary = Color(0xFF006C51)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)

// Tema oscuro
val md_theme_dark_primary = Color(0xFF6FDBAD)
val md_theme_dark_onPrimary = Color(0xFF003826)
```

### Agregar Nuevo Sensor

1. **Backend:** Asegurar que sensor_type correcto en BD
2. **Android:** Actualizar `SensorCard.kt`:

```kotlin
private fun getSensorVisuals(sensorType: String): Triple<...> {
    return when (sensorType.lowercase()) {
        // ... casos existentes ...
        "nuevo_sensor" -> Triple(
            Icons.Default.IconoNuevo,
            Color(0xFFCOLOR),
            "Nombre Mostrar"
        )
        // ...
    }
}
```

### Agregar Nuevo Actuador

Similarmente en `ActuatorControl.kt`:

```kotlin
private fun getActuatorVisuals(...): Quad<...> {
    return when (actuatorType.lowercase()) {
        // ... casos existentes ...
        "nuevo_actuador" -> Quad(
            Icons.Filled.IconoOn,
            Icons.Outlined.IconoOff,
            Color(0xFFCOLOR),
            "Estado Mostrar"
        )
        // ...
    }
}
```

---

## 📦 Build Variants

### Debug
```gradle
buildTypes {
    debug {
        applicationIdSuffix ".debug"
        versionNameSuffix "-DEBUG"
        isDebuggable = true
    }
}
```

### Release
```gradle
buildTypes {
    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

---

## 📝 Registro de Cambios

### v2.1 (Diciembre 2025)
- ✅ Iconos personalizados para todos los sensores/actuadores
- ✅ Soporte para CO₂, distancia
- ✅ Soporte para puerta garaje, luz garaje, ventilador
- ✅ Estados personalizados (Abierta/Cerrada, Funcionando/Apagado)

### v2.0 (Diciembre 2025)
- ✅ Material Design 3
- ✅ Pull-to-refresh
- ✅ Indicador visual offline
- ✅ Actualización automática

### v1.0 (Noviembre 2025)
- Versión inicial básica

---

## 👨‍💻 Autor

**Emilio José Salmerón Arjona**  
IES Azarquiel - Toledo  
CFGS DAM - Curso 2025/2026

---

## 🔗 Enlaces

- [Volver al README principal](../README.md)
- [Documentación ESP32](../esp32/README.md)
- [Documentación Base de Datos](../database/README.md)
- [Documentación Backend](../backend/README.md)
