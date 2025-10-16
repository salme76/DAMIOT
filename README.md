# Proyecto IoT Multiplataforma - DAMIOT

**Sistema de monitorización y control IoT con ESP32, Backend Spring Boot y App Android**

---

## 📋 Descripción

Solución IoT integral que permite la lectura de sensores (temperatura, humedad) y el control de actuadores (LEDs, motores) mediante comunicación eficiente MQTT y API REST. El sistema está compuesto por tres componentes principales que interactúan entre sí para ofrecer monitorización en tiempo real desde dispositivos móviles Android.

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────┐      MQTT        ┌──────────────┐      REST API       ┌──────────────┐
│   ESP32     │ ◄──────────────► │   Backend    │ ◄─────────────────► │  App Android │
│  (Arduino)  │                  │ (Spring Boot)│                     │   (Kotlin)   │
└─────────────┘                  └──────┬───────┘                     └──────────────┘
                                        │
                                        ▼
                               ┌──────────────────┐
                               │  Base de Datos   │
                               │     (MySQL)      │
                               └──────────────────┘
```

---

## 📁 Estructura del Proyecto

```
DAMIOT/
├── esp32/              # Código Arduino para el microcontrolador ESP32
├── backend/            # API REST y cliente MQTT en Spring Boot
├── android/            # Aplicación móvil en Kotlin con Jetpack Compose
├── documentacion/      # Documentación del proyecto (memoria, manuales)
├── diagramas/          # Esquemas Fritzing y diagramas de flujo
└── README.md           # Este archivo
```

---

## 🧩 Componentes

### 1️⃣ **ESP32 (Dispositivo IoT)**
- **Lenguaje**: C/C++ (Arduino IDE)
- **Funciones**:
  - Lectura de sensores ambientales
  - Control de actuadores
  - Publicación de datos vía MQTT
  - Recepción de comandos remotos

### 2️⃣ **Backend (Servidor)**
- **Lenguaje**: Java
- **Framework**: Spring Boot
- **Funciones**:
  - API REST para comunicación con app móvil
  - Cliente MQTT (suscriptor y publicador)
  - Gestión de base de datos (MySQL)
  - Broker MQTT (Mosquitto)
  - Almacenamiento de histórico de lecturas

### 3️⃣ **App Android (Cliente Móvil)**
- **Lenguaje**: Kotlin
- **Framework**: Jetpack Compose
- **Funciones**:
  - Visualización de datos de sensores en tiempo real
  - Envío de comandos de control
  - Interfaz gráfica intuitiva
  - Historial de lecturas

---

## 🔄 Flujo de Comunicación

1. **ESP32 → MQTT Broker → Backend**: El ESP32 publica datos de sensores
2. **App Android → API REST → Backend**: La app consulta datos históricos
3. **App Android → API REST → Backend → MQTT Broker → ESP32**: La app envía comandos de control
4. **Backend → Base de Datos**: Almacenamiento persistente de todas las lecturas

---

## 🚀 Tecnologías Utilizadas

| Componente | Tecnologías |
|------------|-------------|
| **ESP32** | Arduino IDE, WiFi, MQTT Client |
| **Backend** | Java 17, Spring Boot 3.x, MQTT Paho, MySQL, Mosquitto |
| **Android** | Kotlin, Jetpack Compose, Retrofit/Ktor, Material Design 3 |
| **Protocolos** | MQTT, REST API, HTTP/HTTPS |

---

## ✅ Características Principales

- ✔️ Comunicación bidireccional en tiempo real sin polling
- ✔️ Arquitectura escalable y modular
- ✔️ Almacenamiento persistente de datos
- ✔️ Interfaz móvil moderna y responsive
- ✔️ Separación clara de responsabilidades (IoT, Backend, Frontend)
- ✔️ Uso de protocolos estándar de la industria

---

## 📦 Requisitos Previos

### Hardware
- Placa ESP32 DevKit C
- Sensor DHT11
- LED azul
- Protoboard y cables

### Software
- Arduino IDE 2.x
- Java JDK 17+
- Android Studio (última versión)
- MySQL (Se ha usado la versión 8.4. que con Laragoon)
- Mosquitto MQTT Broker

---

## 🛠️ Instalación y Configuración

### 1. Backend
```bash
cd backend
./mvnw spring-boot:run
```

### 2. ESP32
1. Abrir Arduino IDE
2. Cargar el sketch desde `esp32/codigo/`
3. Configurar WiFi y servidor MQTT
4. Subir código a la placa

### 3. App Android
1. Abrir Android Studio
2. Abrir el proyecto desde `android/`
3. Sincronizar Gradle
4. Ejecutar en dispositivo/emulador

---

## 📝 Estado del Proyecto

🚧 **En desarrollo** - Proyecto para el módulo de Proyecto del CFGS DAM

---

## 👨‍💻 Autor

**Emilio José Salmerón Arjona**  
Ciclo Formativo de Grado Superior - Desarrollo de Aplicaciones Multiplataforma  
IES Azarquiel - Toledo  
Curso 2024/2025

---

## 📄 Licencia

Este proyecto es parte de un trabajo académico para el IES Azarquiel.

---

## 📞 Contacto

Para cualquier consulta sobre el proyecto, contactar a través de GitHub.