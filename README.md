# DAMIOT - Sistema IoT Multiplataforma

![Version](https://img.shields.io/badge/version-2.1-blue)
![Status](https://img.shields.io/badge/status-production--ready-green)
![License](https://img.shields.io/badge/license-Academic-orange)

**Proyecto Final - CFGS Desarrollo de Aplicaciones Multiplataforma**  
**IES Azarquiel - Toledo**  
**Curso 2025/2026**

---

## 📋 Índice

- [Descripción](#descripción)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Componentes](#componentes)
- [Características Principales](#características-principales)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Uso](#uso)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Autor](#autor)

---

## 📖 Descripción

DAMIOT (Desarrollo de Aplicaciones Multiplataforma - IoT) es un sistema IoT completo que integra dispositivos ESP32, un backend en Spring Boot, base de datos MySQL, comunicación MQTT y una aplicación móvil Android. El proyecto demuestra capacidades full-stack y conocimientos en desarrollo multiplataforma.

### Objetivo del Proyecto

Crear una solución IoT escalable que permita:
- Monitoreo remoto de sensores (temperatura, humedad, CO₂, distancia)
- Control remoto de actuadores (LEDs, puertas, ventiladores)
- Comunicación eficiente vía MQTT (sin polling)
- Interfaz móvil moderna con Jetpack Compose
- Soporte multi-dispositivo con identificación por MAC

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────┐
│   Android App   │ ← Usuario interactúa aquí
│   (Kotlin)      │
└────────┬────────┘
         │ REST API (HTTP)
         ▼
┌─────────────────┐       ┌──────────────┐
│  Spring Boot    │◄─────►│    MySQL     │
│   Backend       │       │   Database   │
└────────┬────────┘       └──────────────┘
         │ MQTT
         ▼
┌─────────────────┐
│  MQTT Broker    │
│  (Mosquitto)    │
└────────┬────────┘
         │ MQTT
         ▼
┌─────────────────┐
│     ESP32       │ ← Sensores y actuadores
│   (Arduino)     │
└─────────────────┘
```

### Flujo de Datos

**Lectura de sensores:**
```
ESP32 → MQTT → Backend → MySQL → REST API → Android App
```

**Control de actuadores:**
```
Android App → REST API → Backend → MQTT → ESP32
```

---

## 🧩 Componentes

### 1. **ESP32 (Firmware)**
- **Ubicación:** `esp32/damiot-esp32/`
- **Lenguaje:** C++ (Arduino Framework)
- **Hardware:** ESP32 DevKitC WROOM-32D
- **Sensores implementados:** DHT11 (temperatura/humedad)
- **Actuadores implementados:** LED azul
- **Comunicación:** MQTT con topics dinámicos por MAC
- **Nota:** Arquitectura extensible para otros sensores/actuadores

**📝 Nota sobre dispositivos ficticios:**  
El sistema incluye 3 dispositivos en la base de datos:
- **ESP32-Salón:** Dispositivo REAL con DHT11 + LED azul
- **ESP32-Jardín:** Dispositivo FICTICIO (datos de prueba en BD)
- **ESP32-Garaje:** Dispositivo FICTICIO (datos de prueba en BD)

Los dispositivos ficticios demuestran la capacidad multi-dispositivo del sistema y cómo se adaptaría el firmware para diferentes sensores/actuadores.

### 2. **Base de Datos**
- **Ubicación:** `database/`
- **Motor:** MySQL 8.4.3 (Laragon)
- **Tablas:** `device`, `sensor_data`, `actuator_state`, `actuator_events`
- **Características:** Foreign keys, índices optimizados, stored procedures

### 3. **Backend**
- **Ubicación:** `backend/`
- **Framework:** Spring Boot 3.5.6 (Java 21)
- **Funciones:** API REST, cliente MQTT, persistencia de datos
- **Arquitectura:** Clean Architecture con MVVM

### 4. **Aplicación Android**
- **Ubicación:** `android/`
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Arquitectura:** MVVM + Clean Architecture
- **Inyección de dependencias:** Hilt

---

## ✨ Características Principales

### Soporte Multi-Dispositivo
- ✅ Identificación única por dirección MAC
- ✅ Topics MQTT dinámicos: `damiot/{categoria}/{MAC}/{elemento}`
- ✅ Escalable a N dispositivos sin cambios en código

### Comunicación Eficiente
- ✅ MQTT para telemetría en tiempo real (QoS 0)
- ✅ MQTT para comandos garantizados (QoS 1)
- ✅ Heartbeat cada 10 segundos
- ✅ Last Will & Testament (LWT) para detección de desconexión

### Gestión de Estado
- ✅ Detección de dispositivos offline (umbral 30 segundos)
- ✅ Sincronización automática de estado al reiniciar
- ✅ Historial de eventos de actuadores
- ✅ Persistencia de lecturas de sensores

### Optimizaciones
- ✅ Sin fragmentación de heap en ESP32 (buffers estáticos)
- ✅ Temporización no bloqueante con `millis()`
- ✅ Reconexión automática WiFi/MQTT
- ✅ Actualización automática de datos en Android

---

## 📦 Requisitos Previos

### Hardware
- ESP32 DevKitC WROOM-32D
- Sensor DHT11 (temperatura/humedad)
- LED azul + resistencia 220Ω
- Cables Dupont
- Opcional: Breadboard
- Router GLi.Net Mango (o router compatible)

### Software
- **Desarrollo ESP32:** Arduino IDE 2.x
- **Base de datos:** Laragon con MySQL 8.4.3
- **Backend:** JDK 21, Maven, IntelliJ IDEA
- **Android:** Android Studio Hedgehog+, JDK 17+
- **MQTT Broker:** Eclipse Mosquitto

### Red
- Router configurado con SSID: `DAMIOT`
- IP Router: `192.168.8.1/24`
- IP Broker MQTT: `192.168.8.136`

---

## 🚀 Instalación y Configuración

### 1. Configurar Red

```bash
# Configurar router GLi.Net Mango
SSID: DAMIOT
Password: 12345678
IP: 192.168.8.1
Subnet: 255.255.255.0
```

### 2. Instalar Base de Datos

```bash
# En Laragon MySQL
mysql -u root -p

# Ejecutar scripts en orden
source D:/DAMIOT/database/01_esquema.sql
source D:/DAMIOT/database/02_dispositivos.sql
```

### 3. Instalar MQTT Broker

```bash
# Windows (Mosquitto)
# Instalar desde: https://mosquitto.org/download/
# Configurar para escuchar en 192.168.8.136:1883
```

### 4. Compilar Backend

```bash
cd D:/DAMIOT/backend
mvn clean install
mvn spring-boot:run
```

### 5. Flashear ESP32

```
1. Abrir Arduino IDE
2. Abrir: D:/DAMIOT/esp32/damiot-esp32/damiot-esp32.ino
3. Configurar MAC en config.h si es necesario
4. Compilar y subir
```

### 6. Compilar App Android

```bash
cd D:/DAMIOT/android
./gradlew assembleDebug
# O usar Android Studio: Run > Run 'app'
```

---

## 📁 Estructura del Proyecto

```
DAMIOT/
├── esp32/                      # Firmware ESP32
│   ├── damiot-esp32/          # Código principal
│   │   ├── damiot-esp32.ino  # Firmware v2.1
│   │   └── config.h          # Configuración
│   └── test/                  # Tests de hardware
│
├── database/                   # Scripts SQL
│   ├── 01_esquema.sql        # Estructura BD
│   ├── 02_dispositivos.sql   # Datos de prueba
│   ├── 03_reset_datos.sql    # Limpieza
│   └── README.md             # Documentación BD
│
├── backend/                    # Spring Boot
│   ├── src/
│   │   └── main/
│   │       ├── java/         # Código Java
│   │       └── resources/    # Configuración
│   ├── pom.xml              # Dependencias Maven
│   └── README.md            # Documentación Backend
│
└── android/                    # App móvil
    ├── app/
    │   └── src/
    │       └── main/
    │           ├── java/     # Código Kotlin
    │           └── res/      # Recursos
    ├── build.gradle.kts     # Configuración Gradle
    └── README.md            # Documentación Android
```

---

## 🎯 Uso

### Agregar Nuevo Dispositivo

1. **Insertar en base de datos:**
```sql
INSERT INTO device (name, mac_address, ip_address, status, is_enabled) 
VALUES ('ESP32-Nuevo', 'AA:BB:CC:DD:EE:FF', '192.168.8.140', 'offline', TRUE);
```

2. **Flashear firmware estándar** (sin cambios de código)

3. **El sistema automáticamente:**
   - Detecta el dispositivo por MAC
   - Crea topics MQTT dinámicos
   - Enruta datos al `device_id` correcto
   - Muestra el dispositivo en la app

### Monitorear Logs

```bash
# Backend (Spring Boot)
tail -f backend/logs/spring.log

# MQTT Broker
mosquitto_sub -h 192.168.8.136 -t "damiot/#" -v

# ESP32
# Serial Monitor en Arduino IDE (115200 baudios)
```

---

## 🛠️ Tecnologías Utilizadas

### Embedded
- **Arduino Framework** - Programación ESP32
- **PubSubClient** - Cliente MQTT
- **DHT Sensor Library** - Lectura sensores

### Backend
- **Spring Boot 3.5.6** - Framework principal
- **Spring Data JPA** - ORM
- **Eclipse Paho** - Cliente MQTT
- **MySQL Connector** - Driver JDBC
- **Lombok** - Reducción boilerplate

### Android
- **Kotlin 1.9+** - Lenguaje principal
- **Jetpack Compose** - UI moderna
- **Material Design 3** - Componentes UI
- **Hilt** - Inyección de dependencias
- **Retrofit** - Cliente HTTP
- **Coroutines** - Programación asíncrona

### Infraestructura
- **MySQL 8.4.3** - Base de datos relacional
- **Eclipse Mosquitto** - Broker MQTT
- **Laragon** - Entorno desarrollo local

---

## 👨‍💻 Autor

**Emilio José Salmerón Arjona**  
Estudiante de CFGS Desarrollo de Aplicaciones Multiplataforma  
IES Azarquiel - Toledo  
Curso 2025/2026

---

## 📄 Licencia

Proyecto académico - IES Azarquiel

---

## 🔗 Enlaces

- **Repositorio:** https://github.com/salme76/DAMIOT
- **Documentación Adicional:** Ver READMEs en cada carpeta
- **Presentación:** 8 de Diciembre de 2025

---

## 📊 Estado del Proyecto

- ✅ **ESP32 Firmware:** v2.1 (optimizado sin fugas de memoria)
- ✅ **Base de Datos:** Esquema completo con 3 dispositivos
- ✅ **Backend:** API REST funcional + MQTT integrado
- ✅ **Android App:** UI moderna con todos los iconos
- ✅ **Sistema Multi-dispositivo:** Funcionando correctamente
- ✅ **Documentación:** Completa

**Estado:** ✅ LISTO PARA PRESENTACIÓN
