# Base de Datos - Proyecto IoT

Sistema de base de datos para el proyecto IoT multiplataforma con ESP32, backend Spring Boot y app Android.

---

## 📋 Tabla de Contenidos

- [Información General](#información-general)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Scripts SQL](#scripts-sql)
- [Modelo de Datos](#modelo-de-datos)
- [Procedimientos Almacenados](#procedimientos-almacenados)
- [Dispositivo Real](#dispositivo-real)
- [Configuración Spring Boot](#configuración-spring-boot)
- [Consultas Útiles](#consultas-útiles)
- [Mantenimiento](#mantenimiento)

---

## 📊 Información General

**Base de datos**: `iot_project`  
**Motor**: MySQL 8.4.3  
**Entorno**: Laragon  
**Charset**: utf8mb4  
**Collation**: utf8mb4_unicode_ci

### Características principales:
- Modelo simplificado orientado a prototipo funcional
- Soporte para múltiples sensores y actuadores
- Histórico de lecturas con timestamps
- Procedimientos almacenados para consultas frecuentes
- Índices optimizados para consultas temporales

---

## 🔧 Requisitos

- **MySQL**: 8.4.3 o superior
- **Laragon**: Para entorno de desarrollo local
- **HeidiSQL**: Cliente de base de datos (incluido en Laragon)

---

## 🚀 Instalación

### Método 1: HeidiSQL (Recomendado para Windows/Laragon)

1. Inicia **Laragon**
2. Abre **HeidiSQL** (click derecho en icono Laragon → Database → HeidiSQL)
3. Ejecuta los scripts en orden:
   ```sql
   -- 1. Crear estructura
   File → Open → 01_schema.sql → Execute (F9)
   
   -- 2. (Opcional) Cargar datos de prueba
   File → Open → 02_sample_data.sql → Execute (F9)
   
   -- O directamente el dispositivo real
   File → Open → 04_real_device.sql → Execute (F9)
   ```

### Método 2: Terminal MySQL

```bash
# Desde la carpeta database/
mysql -u root < 01_schema.sql
mysql -u root < 04_real_device.sql
```

### Método 3: Script completo (bash)

```bash
#!/bin/bash
# install_database.sh
cd database
mysql -u root < 01_schema.sql
mysql -u root < 04_real_device.sql
echo "Base de datos instalada correctamente"
```

---

## 📁 Scripts SQL

### `01_schema.sql` ⭐ (OBLIGATORIO)
**Propósito**: Crear la estructura completa de la base de datos.

**Contiene**:
- Creación de base de datos `iot_project`
- 3 tablas: `device`, `sensor_data`, `actuator_state`
- Índices optimizados
- 4 procedimientos almacenados

**Cuándo ejecutar**: Una vez al iniciar el proyecto.

---

### `02_sample_data.sql` (OPCIONAL)
**Propósito**: Datos de prueba para desarrollo.

**Contiene**:
- 1 dispositivo ESP32 de ejemplo (MAC: AA:BB:CC:DD:EE:FF)
- 39 lecturas de sensores (últimas 2 horas):
  - 13 lecturas de temperatura (22.5°C → 25.1°C)
  - 13 lecturas de humedad (68% → 63.5%)
  - 13 lecturas de luminosidad (450 lux → 580 lux)
- 5 actuadores con estados iniciales

**Cuándo ejecutar**: Para probar el sistema con datos ficticios.

---

### `03_reset_data.sql` (OPCIONAL)
**Propósito**: Limpiar y recargar datos de prueba.

**Contiene**:
- Limpieza de todas las tablas (TRUNCATE)
- Recarga de datos de ejemplo
- Verificación automática

**Cuándo ejecutar**: Durante desarrollo, cuando necesites resetear los datos.

⚠️ **ADVERTENCIA**: Elimina TODOS los datos. Solo usar en desarrollo.

---

### `04_real_device.sql` ⭐ (DISPOSITIVO REAL)
**Propósito**: Registrar el dispositivo ESP32 real del proyecto.

**Datos del dispositivo**:
```
Hostname:    esp32-F1DAE4
MAC:         7C:9E:BD:F1:DA:E4
IP:          192.168.8.130
Red:         DAMIOT (192.168.8.1/24)
Password:    12345678
```

**Hardware configurado**:
- **DHT11** (pin 4): Sensor de temperatura y humedad
- **LED Azul** (pin 5): Actuador digital

**Cuándo ejecutar**: Después de `01_schema.sql` para registrar el dispositivo real.

---

## 🗄️ Modelo de Datos

### Diagrama de Entidad-Relación

```
┌─────────────────┐
│     DEVICE      │
├─────────────────┤
│ id (PK)         │
│ name            │
│ mac_address     │◄────┐
│ ip_address      │     │
│ status          │     │
│ last_connection │     │
└─────────────────┘     │
                        │
        ┌───────────────┼───────────────┐
        │               │               │
        │               │               │
┌───────▼────────┐  ┌───▼──────────┐  │
│  SENSOR_DATA   │  │ ACTUATOR_    │  │
│                │  │ STATE        │  │
├────────────────┤  ├──────────────┤  │
│ id (PK)        │  │ id (PK)      │  │
│ device_id (FK) │──┘ device_id    │──┘
│ sensor_type    │    actuator_type│
│ value          │    state        │
│ unit           │    updated_at   │
│ timestamp      │                 │
└────────────────┘  └──────────────┘
```

### Tablas

#### `device`
Almacena información de los dispositivos ESP32 conectados.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | BIGINT | Identificador único (PK) |
| `name` | VARCHAR(100) | Nombre descriptivo (ej: "esp32-F1DAE4") |
| `mac_address` | VARCHAR(17) | Dirección MAC (UNIQUE) |
| `ip_address` | VARCHAR(15) | IP actual del dispositivo |
| `status` | ENUM | 'online' o 'offline' |
| `last_connection` | TIMESTAMP | Última conexión |
| `created_at` | TIMESTAMP | Fecha de registro |
| `updated_at` | TIMESTAMP | Última actualización |

#### `sensor_data`
Almacena el histórico de lecturas de sensores.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | BIGINT | Identificador único (PK) |
| `device_id` | BIGINT | Referencia al dispositivo (FK) |
| `sensor_type` | VARCHAR(50) | Tipo: 'temperature', 'humidity', 'light', etc. |
| `value` | DECIMAL(10,2) | Valor medido |
| `unit` | VARCHAR(20) | Unidad: '°C', '%', 'lux', etc. |
| `timestamp` | TIMESTAMP | Momento de la lectura |

**Índices**:
- `idx_device_timestamp`: Consultas por dispositivo y tiempo
- `idx_timestamp`: Consultas temporales
- `idx_sensor_type`: Filtrado por tipo de sensor

#### `actuator_state`
Almacena el estado actual de los actuadores.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | BIGINT | Identificador único (PK) |
| `device_id` | BIGINT | Referencia al dispositivo (FK) |
| `actuator_type` | VARCHAR(50) | Tipo: 'led_red', 'fan', 'relay', etc. |
| `state` | VARCHAR(50) | Estado: 'ON', 'OFF', o valor PWM |
| `updated_at` | TIMESTAMP | Última actualización |

**Restricción**: Solo un registro por combinación device_id + actuator_type.

---

## ⚙️ Procedimientos Almacenados

### `clean_old_sensor_data()`
Elimina lecturas de sensores más antiguas de 30 días.

```sql
CALL clean_old_sensor_data();
```

**Retorna**: Número de registros eliminados.

---

### `get_latest_readings(device_id)`
Obtiene la última lectura de cada tipo de sensor para un dispositivo.

```sql
CALL get_latest_readings(1);
```

**Parámetros**:
- `device_id`: ID del dispositivo

**Retorna**: Última lectura de temperatura, humedad, luz, etc.

---

### `get_sensor_stats(device_id, sensor_type, hours)`
Calcula estadísticas de un sensor en un periodo de tiempo.

```sql
CALL get_sensor_stats(1, 'temperature', 24);
```

**Parámetros**:
- `device_id`: ID del dispositivo
- `sensor_type`: Tipo de sensor ('temperature', 'humidity', etc.)
- `hours`: Número de horas hacia atrás

**Retorna**: Promedio, mínimo, máximo, desviación estándar, etc.

---

### `update_device_status(device_id, status)`
Actualiza el estado de conexión de un dispositivo.

```sql
CALL update_device_status(1, 'online');
```

**Parámetros**:
- `device_id`: ID del dispositivo
- `status`: 'online' o 'offline'

---

## 🔷 Dispositivo Real

### Especificaciones Hardware

| Componente | Descripción | Pin | Mediciones |
|------------|-------------|-----|------------|
| **ESP32** | Microcontrolador | - | - |
| **DHT11** | Sensor Temp/Humedad | 4 | temperature, humidity |
| **LED Azul** | Actuador Digital | 5 | ON/OFF |

### Configuración de Red

```
Router:   GLi.Net Mango
IP:       192.168.8.1/24
SSID:     DAMIOT
Password: 12345678
ESP32 IP: 192.168.8.130
```

### Identificación del Dispositivo

```cpp
// Para código ESP32
const char* hostname = "esp32-F1DAE4";
const char* mac = "7C:9E:BD:F1:DA:E4";
IPAddress ip(192, 168, 8, 130);
```

### Consultar ID del Dispositivo

```sql
SELECT id 
FROM device 
WHERE mac_address = '7C:9E:BD:F1:DA:E4';
```

---

## 🔌 Configuración Spring Boot

### `application.properties`

```properties
# Configuración de base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/iot_project?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### `application.yml` (Alternativa)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/iot_project?useSSL=false&serverTimezone=UTC
    username: root
    password: 
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### Dependencias Maven (`pom.xml`)

```xml
<dependencies>
    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
</dependencies>
```

---

## 🔍 Consultas Útiles

### Ver todos los dispositivos

```sql
SELECT * FROM device;
```

### Últimas 10 lecturas de temperatura

```sql
SELECT value, unit, timestamp 
FROM sensor_data 
WHERE sensor_type = 'temperature' 
ORDER BY timestamp DESC 
LIMIT 10;
```

### Estado de todos los actuadores

```sql
SELECT d.name, a.actuator_type, a.state, a.updated_at
FROM actuator_state a
JOIN device d ON a.device_id = d.id;
```

### Promedios de sensores (última hora)

```sql
SELECT 
    sensor_type,
    COUNT(*) as lecturas,
    AVG(value) as promedio,
    MIN(value) as minimo,
    MAX(value) as maximo,
    unit
FROM sensor_data
WHERE timestamp >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
GROUP BY sensor_type, unit;
```

### Histórico de temperatura del DHT11

```sql
SELECT 
    value as temperatura,
    timestamp
FROM sensor_data
WHERE device_id = 1
  AND sensor_type = 'temperature'
  AND timestamp >= DATE_SUB(NOW(), INTERVAL 24 HOUR)
ORDER BY timestamp DESC;
```

### Verificar conectividad del dispositivo

```sql
SELECT 
    name,
    status,
    last_connection,
    TIMESTAMPDIFF(SECOND, last_connection, NOW()) as segundos_desconectado
FROM device 
WHERE mac_address = '7C:9E:BD:F1:DA:E4';
```

### Cambiar estado del LED

```sql
UPDATE actuator_state 
SET state = 'ON' 
WHERE device_id = 1 
  AND actuator_type = 'led_blue';
```

---

## 🛠️ Mantenimiento

### Limpieza de datos antiguos

```sql
-- Eliminar lecturas > 30 días
CALL clean_old_sensor_data();

-- O manualmente
DELETE FROM sensor_data 
WHERE timestamp < DATE_SUB(NOW(), INTERVAL 30 DAY);
```

### Backup de la base de datos

```bash
# Backup completo
mysqldump -u root iot_project > backup_iot_$(date +%Y%m%d).sql

# Solo estructura
mysqldump -u root --no-data iot_project > backup_schema.sql

# Solo datos
mysqldump -u root --no-create-info iot_project > backup_data.sql
```

### Restaurar backup

```bash
mysql -u root iot_project < backup_iot_20241019.sql
```

### Verificar integridad

```sql
-- Contar registros
SELECT 
    'Dispositivos' as tabla, COUNT(*) as registros FROM device
UNION ALL
SELECT 'Sensores', COUNT(*) FROM sensor_data
UNION ALL
SELECT 'Actuadores', COUNT(*) FROM actuator_state;

-- Verificar índices
SHOW INDEX FROM sensor_data;

-- Verificar procedimientos
SHOW PROCEDURE STATUS WHERE Db = 'iot_project';
```

---

## 📈 Escalabilidad Futura

Este es un modelo simplificado para prototipo. En la documentación del proyecto se contempla:

### Mejoras arquitectónicas:
- Separación de tablas Sensor y Actuator independientes
- Sistema de usuarios con autenticación
- Tabla de comandos con trazabilidad
- Sistema de alertas configurables
- Agregación de datos históricos

### Multi-dispositivo:
- Gestión de múltiples ESP32
- Clusterización del backend
- Cache distribuido (Redis)

### Seguridad:
- Autenticación JWT
- Certificados TLS para MQTT
- Encriptación de comunicaciones

---

## 📝 Notas Adicionales

- **Zona horaria**: El servidor MySQL debe estar configurado en UTC
- **Formato de fecha**: ISO 8601 (YYYY-MM-DD HH:MM:SS)
- **Charset**: Usar siempre UTF-8 para compatibilidad
- **Foreign Keys**: Configuradas con `ON DELETE CASCADE`
- **Timestamps**: Actualizados automáticamente con triggers

---

## 🆘 Solución de Problemas

### Error: "Access denied for user 'root'@'localhost'"
```sql
-- En MySQL como administrador
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '';
FLUSH PRIVILEGES;
```

### Error: "Table doesn't exist"
```bash
# Volver a ejecutar el schema
mysql -u root < 01_schema.sql
```

### Error: "Duplicate entry" al insertar dispositivo
```sql
-- El dispositivo ya existe, usar el script que incluye ON DUPLICATE KEY UPDATE
mysql -u root < 04_real_device.sql
```

---

## 📚 Referencias

- [MySQL 8.4 Documentation](https://dev.mysql.com/doc/refman/8.4/en/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Laragon Documentation](https://laragon.org/docs/)

---

**Última actualización**: 19 de octubre de 2024  
**Versión**: 1.0  
**Autor**: IES Azarquiel - Proyecto CFGS DAM
