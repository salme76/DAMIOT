# ESP32 Firmware - DAMIOT

Firmware optimizado para dispositivos ESP32 en el sistema DAMIOT.

---

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Hardware Soportado](#hardware-soportado)
- [Configuración](#configuración)
- [Características](#características)
- [Estructura del Código](#estructura-del-código)
- [Compilación y Flasheo](#compilación-y-flasheo)
- [Topics MQTT](#topics-mqtt)
- [Troubleshooting](#troubleshooting)

---

## 📖 Descripción

Firmware v2.1 para ESP32 que implementa:
- Lectura de sensores (DHT11, HC-SR04, MQ-135)
- Control de actuadores (LEDs, relés, servos)
- Comunicación MQTT con topics dinámicos por MAC
- Heartbeat para monitoreo de conexión
- Last Will & Testament (LWT)
- Reconexión automática WiFi/MQTT

### Versión Actual: 2.1

**Optimizaciones v2.1:**
- ✅ Eliminada fragmentación de heap (buffers estáticos)
- ✅ Sin concatenación de `String` en runtime
- ✅ Memoria predecible y estable
- ✅ Puede correr semanas/meses sin crash

---

## 🔌 Hardware Soportado

### ESP32 Principal
- **Modelo:** ESP32 DevKitC WROOM-32D
- **MAC:** 7C:9E:BD:F1:DA:E4 (ESP32-Salón)
- **IP Asignada:** 192.168.8.130

### Sensores Compatibles
| Sensor | Pin | Tipo | Unidad |
|--------|-----|------|--------|
| DHT11 | GPIO4 | Temperatura/Humedad | °C / % |
| HC-SR04 | GPIO18/19 | Distancia | cm |
| MQ-135 | GPIO34 | CO₂ | ppm |
| YL-69 | GPIO35 | Humedad Suelo | ADC |

### Actuadores Compatibles
| Actuador | Pin | Tipo |
|----------|-----|------|
| LED Azul | GPIO5 | Digital |
| LED Verde | GPIO25 | Digital |
| Relé 1 | GPIO26 | Digital |
| Servo | GPIO27 | PWM |

---

## ⚙️ Configuración

### Archivo: `config.h`

```cpp
// === RED WIFI ===
const char* WIFI_SSID = "DAMIOT";
const char* WIFI_PASSWORD = "12345678";

// === MQTT BROKER ===
const char* MQTT_SERVER = "192.168.8.136";
const int MQTT_PORT = 1883;
const char* MQTT_CLIENT_ID = "ESP32-Salon";

// === PINES HARDWARE ===
#define DHTPIN 4                // DHT11 en GPIO4
#define DHTTYPE DHT11
#define LED_AZUL 5              // LED en GPIO5

// === INTERVALOS (milisegundos) ===
#define INTERVALO_LECTURA 5000      // 5 segundos
#define INTERVALO_HEARTBEAT 10000   // 10 segundos
#define INTERVALO_RECONEXION 5000   // 5 segundos

// === NOMBRES DE SENSORES/ACTUADORES ===
const char* SENSOR_TEMPERATURA = "temperatura";
const char* SENSOR_HUMEDAD = "humedad";
const char* ACTUADOR_LED_AZUL = "led_azul";
```

### Modificar para Nuevo Dispositivo

**Solo cambiar 3 líneas:**
```cpp
const char* MQTT_CLIENT_ID = "ESP32-Garaje";  // Nombre único
// PIN_LED puede cambiar si hardware diferente
// ¡La MAC se detecta automáticamente!
```

---

## ✨ Características

### 1. Topics MQTT Dinámicos

El firmware construye topics automáticamente incluyendo la MAC:

```cpp
// Formato: damiot/{categoria}/{MAC}/{elemento}

// Ejemplos ESP32-Salón (MAC: 7C:9E:BD:F1:DA:E4):
damiot/sensores/7C:9E:BD:F1:DA:E4/temperatura
damiot/sensores/7C:9E:BD:F1:DA:E4/humedad
damiot/actuadores/7C:9E:BD:F1:DA:E4/led_azul
damiot/heartbeat/7C:9E:BD:F1:DA:E4
```

### 2. Gestión de Memoria Optimizada

**ANTES (v2.0):**
```cpp
// ❌ Fragmentación de heap
String topicHeartbeat = "damiot/heartbeat/" + macAddress;
```

**AHORA (v2.1):**
```cpp
// ✅ Buffers estáticos (sin heap allocation)
char topicHeartbeat[50];
snprintf(topicHeartbeat, sizeof(topicHeartbeat), 
         "damiot/heartbeat/%s", macAddress);
```

### 3. Temporización No Bloqueante

```cpp
// ✅ Usa millis() en lugar de delay()
unsigned long tiempoActual = millis();
if (tiempoActual - ultimaLectura >= INTERVALO_LECTURA) {
    leerYPublicarSensor();
    ultimaLectura = tiempoActual;
}
```

### 4. Last Will & Testament (LWT)

```cpp
// Si el ESP32 se desconecta abruptamente (corte luz, crash),
// el broker publica automáticamente "offline"
mqttClient.connect(MQTT_CLIENT_ID, 
                  topicHeartbeat,    // LWT topic
                  0,                 // QoS
                  true,              // retain
                  "offline");        // LWT message
```

---

## 📂 Estructura del Código

```
damiot-esp32/
├── damiot-esp32.ino        # Firmware principal (v2.1)
├── config.h                # Configuración
└── README.md              # Este archivo

test/
├── test-led/              # Test básico LED
│   └── test-led.ino
└── test-dht11/            # Test sensor DHT11
    └── test-dht11.ino
```

### Funciones Principales

| Función | Descripción |
|---------|-------------|
| `setup()` | Inicialización única al arrancar |
| `loop()` | Bucle principal (no bloqueante) |
| `conectarWiFi()` | Conexión WiFi con timeout |
| `reconectarMQTT()` | Reconexión MQTT con LWT |
| `callbackMQTT()` | Procesa comandos entrantes |
| `enviarHeartbeat()` | Publica heartbeat con IP |
| `leerYPublicarSensor()` | Lee y envía datos sensores |

---

## 🔨 Compilación y Flasheo

### 1. Instalar Arduino IDE

```
Descargar: https://www.arduino.cc/en/software
Versión recomendada: 2.3.2 o superior
```

### 2. Configurar Board Manager

```
1. File → Preferences
2. Additional Board Manager URLs:
   https://dl.espressif.com/dl/package_esp32_index.json
3. Tools → Board → Boards Manager
4. Buscar "esp32" e instalar
```

### 3. Instalar Librerías

```
Tools → Manage Libraries...

Instalar:
- PubSubClient (v2.8.0+)
- DHT sensor library (v1.4.6+)
- Adafruit Unified Sensor (v1.1.14+)
```

### 4. Compilar

```
1. Abrir: damiot-esp32.ino
2. Tools → Board → ESP32 Dev Module
3. Tools → Port → [seleccionar puerto COM]
4. Sketch → Verify/Compile
```

### 5. Flashear

```
1. Conectar ESP32 via USB
2. Sketch → Upload
3. Abrir Serial Monitor (115200 baudios)
4. Verificar logs de conexión
```

---

## 📡 Topics MQTT

### Publicación (ESP32 → Backend)

| Topic | QoS | Frecuencia | Contenido |
|-------|-----|------------|-----------|
| `damiot/sensores/{MAC}/temperatura` | 0 | 5s | Float (ej: "23.50") |
| `damiot/sensores/{MAC}/humedad` | 0 | 5s | Float (ej: "65.20") |
| `damiot/heartbeat/{MAC}` | 0 | 10s | IP (ej: "192.168.8.130") |
| `damiot/actuadores/{MAC}/led_azul/estado` | 0 | On change | "ON" o "OFF" |

### Suscripción (Backend → ESP32)

| Topic | QoS | Contenido |
|-------|-----|-----------|
| `damiot/actuadores/{MAC}/led_azul` | 1 | "ON" o "OFF" |
| `damiot/actuadores/{MAC}/bomba_riego` | 1 | "ON" o "OFF" |

**Nota:** QoS 0 para telemetría (no crítico), QoS 1 para comandos (garantizado)

---

## 🐛 Troubleshooting

### No conecta a WiFi

```
Síntomas: "WiFi conectando..." infinito
Solución:
1. Verificar SSID y password en config.h
2. Verificar que router está encendido
3. Revisar que IP del router es 192.168.8.1
4. Probar reset del ESP32
```

### No conecta a MQTT

```
Síntomas: "Conectando a MQTT..." reintenta
Solución:
1. Verificar broker Mosquitto corriendo
2. Ping a 192.168.8.136
3. Verificar firewall no bloquea puerto 1883
4. Revisar logs del broker
```

### Lecturas DHT11 "nan"

```
Síntomas: "[ERROR] Lectura DHT11 fallida"
Solución:
1. Verificar conexiones físicas
2. Verificar pin correcto en config.h
3. Esperar 2 segundos entre lecturas (DHT11 es lento)
4. Probar con test/test-dht11/
```

### ESP32 Crashea Después de Horas/Días

```
Síntomas: Watchdog reset, reset espontáneos
Solución:
1. ✅ Ya resuelto en v2.1 (buffers estáticos)
2. Si persiste: Verificar fuente de alimentación
3. Añadir watchdog timer explícito si necesario
```

### LED no responde a comandos MQTT

```
Síntomas: Comando enviado pero LED no cambia
Solución:
1. Verificar topic correcto con MAC
2. Revisar Serial Monitor por logs de callback
3. Verificar que device está ONLINE en BD
4. Probar comando manual:
   mosquitto_pub -h 192.168.8.136 -t "damiot/actuadores/7C:9E:BD:F1:DA:E4/led_azul" -m "ON"
```

---

## 📊 Uso de Memoria

### v2.1 (Optimizada)

```
Compilación:
Sketch uses 295,024 bytes (22%) of program storage
Global variables use 18,436 bytes (5%) of dynamic memory

Runtime (después de 24h):
Free heap: ~280 KB (estable)
Heap fragmentation: <1% (excelente)
```

### Buffers Estáticos Definidos

```cpp
char macAddress[18];           // 18 bytes
char topicHeartbeat[50];       // 50 bytes
char topicLED[60];             // 60 bytes
char topicLEDEstado[70];       // 70 bytes
char topicTemperatura[60];     // 60 bytes
char topicHumedad[60];         // 60 bytes
char ipBuffer[16];             // 16 bytes
char valueBuffer[8];           // 8 bytes
char messageBuffer[32];        // 32 bytes
// Total: ~374 bytes (permanentes en stack)
```

---

## 🔄 Ciclo de Vida

```
[BOOT]
  ↓
[setup()]
  ├─ Inicializar Serial
  ├─ Configurar pines
  ├─ Inicializar DHT11
  ├─ Conectar WiFi
  ├─ Obtener MAC
  ├─ Construir topics (1 vez)
  └─ Configurar MQTT
  
[loop()] ← Ejecuta continuamente
  ├─ Verificar WiFi → reconectar si necesario
  ├─ Verificar MQTT → reconectar si necesario
  ├─ mqttClient.loop() → procesar mensajes
  ├─ Cada 5s → leerYPublicarSensor()
  └─ Cada 10s → enviarHeartbeat()
```

---

## 📝 Registro de Cambios

### v2.1 (Diciembre 2025)
- ✅ Eliminada concatenación de String
- ✅ Buffers estáticos para topics
- ✅ Optimización de memoria
- ✅ Topics construidos una sola vez en setup()

### v2.0 (Diciembre 2025)
- ✅ Topics dinámicos con MAC
- ✅ Soporte multi-dispositivo
- ✅ Last Will & Testament
- ✅ Temporización no bloqueante

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
- [Documentación Base de Datos](../database/README.md)
- [Documentación Backend](../backend/README.md)
- [Documentación Android](../android/README.md)
