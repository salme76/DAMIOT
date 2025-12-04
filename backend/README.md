# Backend Spring Boot - DAMIOT

API REST y cliente MQTT para el sistema DAMIOT IoT.

---

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Configuración](#configuración)
- [Compilación y Ejecución](#compilación-y-ejecución)
- [API REST Endpoints](#api-rest-endpoints)
- [MQTT Integration](#mqtt-integration)
- [Características](#características)
- [Troubleshooting](#troubleshooting)

---

## 📖 Descripción

Backend Spring Boot que actúa como puente entre dispositivos ESP32, base de datos MySQL y aplicación Android.

### Funciones Principales

- **API REST:** Endpoints para consultar sensores y enviar comandos
- **Cliente MQTT:** Escucha topics de sensores y publica comandos a actuadores
- **Persistencia:** Almacena lecturas y eventos en MySQL
- **Monitoreo:** Sistema de heartbeat para detectar dispositivos offline
- **Gestión de Estado:** Sincronización de estado de actuadores

---

## 🏗️ Arquitectura

```
┌─────────────┐
│  Android    │ ← REST API (HTTP)
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│       Spring Boot Backend           │
│                                     │
│  ┌────────────┐    ┌─────────────┐ │
│  │    API     │    │    MQTT     │ │
│  │Controllers │    │   Client    │ │
│  └─────┬──────┘    └──────┬──────┘ │
│        │                  │         │
│        ▼                  ▼         │
│  ┌────────────────────────────┐    │
│  │        Services            │    │
│  │  - DeviceService           │    │
│  │  - SensorService           │    │
│  │  - ActuatorService         │    │
│  │  - MqttService             │    │
│  └────────────┬───────────────┘    │
│               │                     │
│               ▼                     │
│  ┌────────────────────────────┐    │
│  │      Repositories          │    │
│  │  (Spring Data JPA)         │    │
│  └────────────┬───────────────┘    │
└───────────────┼─────────────────────┘
                │
                ▼
        ┌───────────────┐
        │     MySQL     │
        └───────────────┘
        
        ┌───────────────┐
        │ MQTT Broker   │ ← Mosquitto
        └───────────────┘
                ▲
                │
        ┌───────┴───────┐
        │     ESP32     │
        └───────────────┘
```

---

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Spring Boot | 3.5.6 | Framework principal |
| Java | 21 | Lenguaje |
| Spring Data JPA | 3.5.6 | ORM |
| MySQL Connector | 8.0.33 | Driver JDBC |
| Eclipse Paho | 1.2.5 | Cliente MQTT |
| Lombok | 1.18.34 | Reducción boilerplate |
| Maven | 3.9+ | Gestión dependencias |

---

## 📁 Estructura del Proyecto

```
backend/
├── src/
│   └── main/
│       ├── java/com/damiot/backend/
│       │   ├── controller/          # Endpoints REST
│       │   │   ├── ActuatorController.java
│       │   │   ├── DeviceController.java
│       │   │   ├── SensorController.java
│       │   │   └── HealthController.java
│       │   │
│       │   ├── service/            # Lógica de negocio
│       │   │   ├── ActuatorService.java
│       │   │   ├── DeviceService.java
│       │   │   ├── SensorService.java
│       │   │   └── MqttService.java
│       │   │
│       │   ├── repository/         # Acceso a datos
│       │   │   ├── ActuatorEventRepository.java
│       │   │   ├── ActuatorStateRepository.java
│       │   │   ├── DeviceRepository.java
│       │   │   └── SensorReadingRepository.java
│       │   │
│       │   ├── entity/             # Modelos JPA
│       │   │   ├── ActuatorEvent.java
│       │   │   ├── ActuatorState.java
│       │   │   ├── Device.java
│       │   │   └── SensorReading.java
│       │   │
│       │   ├── dto/                # Data Transfer Objects
│       │   │   └── ActuatorCommandRequest.java
│       │   │
│       │   ├── config/             # Configuración
│       │   │   └── MqttConfig.java
│       │   │
│       │   ├── mqtt/               # MQTT
│       │   │   └── MqttMessageHandler.java
│       │   │
│       │   └── scheduler/          # Tareas programadas
│       │       └── DeviceMonitorScheduler.java
│       │
│       └── resources/
│           ├── application.properties  # Configuración principal
│           └── application-dev.properties
│
├── pom.xml                         # Dependencias Maven
└── README.md                       # Este archivo
```

---

## ⚙️ Configuración

### Archivo: `application.properties`

```properties
# === SERVIDOR ===
server.port=8080

# === BASE DE DATOS ===
spring.datasource.url=jdbc:mysql://localhost:3306/damiot_db?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# === MQTT ===
mqtt.broker.url=tcp://192.168.8.136:1883
mqtt.client.id=damiot-backend
mqtt.username=
mqtt.password=
mqtt.connection.timeout=10
mqtt.keep.alive.interval=60
mqtt.clean.session=true
mqtt.automatic.reconnect=true

# Topics MQTT (con wildcards para multi-dispositivo)
mqtt.topics.subscribe=damiot/sensores/#,damiot/actuadores/+/+/estado,damiot/heartbeat/+,damiot/dispositivo/estado

# === CORS ===
cors.allowed.origins=*

# === LOGGING ===
logging.level.root=INFO
logging.level.com.damiot.backend=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.eclipse.paho=DEBUG
```

### Variables de Entorno (Opcional)

```bash
# Producción
export MYSQL_HOST=192.168.8.1
export MYSQL_PORT=3306
export MYSQL_DATABASE=damiot_db
export MYSQL_USER=damiot_user
export MYSQL_PASSWORD=secure_password

export MQTT_BROKER=tcp://192.168.8.136:1883
export MQTT_USERNAME=mqtt_user
export MQTT_PASSWORD=mqtt_password
```

---

## 🔨 Compilación y Ejecución

### Requisitos

- **JDK 21** instalado
- **Maven 3.9+** instalado
- **MySQL** corriendo con BD creada
- **Mosquitto MQTT Broker** corriendo

### Compilar

```bash
cd D:/DAMIOT/backend

# Limpiar y compilar
mvn clean install

# Solo compilar (sin tests)
mvn clean install -DskipTests
```

### Ejecutar

**Opción 1: Maven**
```bash
mvn spring-boot:run
```

**Opción 2: JAR**
```bash
java -jar target/backend-1.0.0.jar
```

**Opción 3: IntelliJ IDEA**
```
Run → Run 'DamiotApplication'
```

### Verificar

```bash
# Health check
curl http://localhost:8080/api/health

# Ver dispositivos
curl http://localhost:8080/api/devices
```

---

## 📡 API REST Endpoints

### Base URL
```
http://localhost:8080/api
```

### Dispositivos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/devices` | Lista todos los dispositivos |
| GET | `/devices/{id}` | Obtiene un dispositivo |
| GET | `/devices/online` | Lista dispositivos online |
| GET | `/devices/offline` | Lista dispositivos offline |
| POST | `/devices` | Registra nuevo dispositivo |

**Ejemplo:**
```bash
curl http://localhost:8080/api/devices
```

### Sensores

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/sensors/device/{deviceId}` | Lecturas de un dispositivo |
| GET | `/sensors/device/{deviceId}/latest` | Últimas lecturas |
| GET | `/sensors/device/{deviceId}/type/{type}` | Lecturas de un tipo |

**Ejemplo:**
```bash
curl http://localhost:8080/api/sensors/device/1/latest
```

### Actuadores

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/actuators/device/{deviceId}` | Estado actuadores de dispositivo |
| POST | `/actuators/command` | Envía comando a actuador |
| GET | `/actuators/events/device/{deviceId}` | Historial de eventos |

**Enviar Comando:**
```bash
curl -X POST http://localhost:8080/api/actuators/command \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": 1,
    "actuatorType": "led_azul",
    "command": "ON"
  }'
```

### Health Check

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/health` | Estado del backend |
| GET | `/health/mqtt` | Estado conexión MQTT |
| GET | `/health/database` | Estado conexión MySQL |

---

## 🔌 MQTT Integration

### Topics Suscritos

```
# Sensores (cualquier MAC, cualquier tipo)
damiot/sensores/#

# Estado de actuadores
damiot/actuadores/+/+/estado

# Heartbeat de dispositivos
damiot/heartbeat/+

# Estado general
damiot/dispositivo/estado
```

### Topics Publicados

```
# Comandos a actuadores (con MAC específica)
damiot/actuadores/{MAC}/led_azul
damiot/actuadores/{MAC}/bomba_riego
damiot/actuadores/{MAC}/puerta_garaje
damiot/actuadores/{MAC}/luz_garaje
damiot/actuadores/{MAC}/ventilador
```

### Flujo MQTT

**Recepción de Sensores:**
```
1. ESP32 publica: damiot/sensores/7C:9E:BD:F1:DA:E4/temperatura → "23.5"
2. MqttMessageHandler recibe mensaje
3. Extrae MAC del topic: "7C:9E:BD:F1:DA:E4"
4. Busca device_id en BD usando MAC
5. Guarda en sensor_data con device_id correcto
```

**Envío de Comandos:**
```
1. Android POST /api/actuators/command {deviceId: 1, actuatorType: "led_azul", command: "ON"}
2. ActuatorService busca Device por ID
3. Obtiene MAC del Device
4. Construye topic: damiot/actuadores/7C:9E:BD:F1:DA:E4/led_azul
5. Publica comando "ON" via MQTT
6. Guarda evento en actuator_events
```

---

## ✨ Características

### 1. Soporte Multi-Dispositivo

El backend enruta automáticamente mensajes MQTT al dispositivo correcto:

```java
// Extrae MAC del topic MQTT
// Ejemplo: "damiot/sensores/7C:9E:BD:F1:DA:E4/temperatura"
String[] parts = topic.split("/");
String macAddress = parts[2];  // Obtiene la MAC

// Busca dispositivo en BD
Device device = deviceRepository.findByMacAddress(macAddress);
Long deviceId = device.getId();

// Guarda con device_id correcto
sensorReading.setDeviceId(deviceId);
```

### 2. Monitoreo de Dispositivos

**DeviceMonitorScheduler** ejecuta cada 15 segundos:

```java
@Scheduled(fixedDelay = 15000)
public void checkDeviceStatus() {
    // Para cada dispositivo online:
    // Si no hay heartbeat en 30 segundos → marcar OFFLINE
    deviceService.checkOfflineDevices();
}
```

### 3. Resilencia

- ✅ Reconexión automática MQTT si broker cae
- ✅ Continúa funcionando si MySQL falla (degrada funcionalidad)
- ✅ Reintentos automáticos en operaciones fallidas
- ✅ Logging detallado para debugging

### 4. Last Will & Testament

Procesa mensajes LWT del broker:

```java
// Si ESP32 se desconecta abruptamente
Topic: damiot/heartbeat/7C:9E:BD:F1:DA:E4
Message: "offline"

// Backend actualiza estado inmediatamente
device.setStatus("offline");
```

---

## 🐛 Troubleshooting

### Backend no arranca

```
Error: Cannot connect to database
Solución:
1. Verificar MySQL corriendo
2. Verificar credenciales en application.properties
3. Verificar BD 'damiot_db' existe
4. Ejecutar scripts SQL
```

### No conecta a MQTT

```
Error: Failed to connect to MQTT broker
Solución:
1. Verificar Mosquitto corriendo
2. Ping a 192.168.8.136
3. Verificar puerto 1883 abierto
4. Revisar logs de Mosquitto
```

### Sensores no se guardan

```
Síntoma: ESP32 publica pero no aparece en BD
Solución:
1. Verificar topic correcto con MAC
2. Revisar logs del MqttMessageHandler
3. Verificar dispositivo existe en BD con esa MAC
4. Verificar permisos de escritura en BD
```

### Comandos no llegan al ESP32

```
Síntoma: POST funciona pero LED no cambia
Solución:
1. Verificar device_id correcto en request
2. Revisar logs de MqttService
3. Verificar ESP32 suscrito al topic correcto
4. Usar mosquitto_sub para verificar mensajes
```

---

## 📊 Métricas y Monitoreo

### Actuator Health Endpoints

Spring Boot Actuator incluido (deshabilitado por defecto):

```properties
# En application.properties (para habilitar)
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

Endpoints disponibles:
```
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

### Logs

```bash
# Logs en consola (desarrollo)
tail -f logs/spring.log

# Cambiar nivel de logging
logging.level.com.damiot.backend=DEBUG
```

---

## 🔐 Seguridad

**Estado Actual:** Sin autenticación (demo académica)

**Para Producción:**

```java
// Agregar Spring Security
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .build();
    }
}
```

Recomendaciones:
- ✅ Implementar JWT authentication
- ✅ HTTPS con certificado TLS
- ✅ MQTTS en lugar de MQTT
- ✅ Rate limiting
- ✅ Input validation (@Valid)

---

## 📝 Registro de Cambios

### v2.1 (Diciembre 2025)
- ✅ Corregido device ID hardcodeado
- ✅ Extracción de MAC desde topics MQTT
- ✅ Enrutamiento correcto multi-dispositivo
- ✅ Wildcards MQTT actualizados

### v2.0 (Diciembre 2025)
- ✅ Soporte multi-dispositivo
- ✅ Topics dinámicos con MAC
- ✅ Monitoreo de heartbeat

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
- [Documentación Android](../android/README.md)
