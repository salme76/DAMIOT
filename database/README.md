# Base de Datos - Proyecto IoT

Sistema de base de datos MySQL para el proyecto IoT multiplataforma.

---

## Información General

**Base de datos**: `iot_project`  
**Motor**: MySQL 8.4.3  
**Entorno**: Laragon  
**Charset**: utf8mb4  

---

## Instalación

### Desde HeidiSQL (Laragon)

1. Inicia Laragon y abre HeidiSQL
2. Ejecuta en orden:
   ```
   01_schema.sql       (estructura)
   04_real_device.sql  (dispositivo real)
   ```

### Desde terminal

```bash
mysql -u root < 01_schema.sql
mysql -u root < 04_real_device.sql
```

---

## Scripts SQL

### `01_schema.sql` (OBLIGATORIO)
Crea la estructura completa:
- Base de datos `iot_project`
- Tablas: `device`, `sensor_data`, `actuator_state`
- Índices optimizados
- Procedimientos almacenados

### `02_sample_data.sql` (OPCIONAL)
Datos de prueba para desarrollo (39 lecturas de ejemplo).

### `03_reset_data.sql` (OPCIONAL)
Limpia y recarga datos de prueba. Solo para desarrollo.

### `04_real_device.sql` (DISPOSITIVO REAL)
Registra el ESP32 real del proyecto:
- Hostname: esp32-F1DAE4
- MAC: 7C:9E:BD:F1:DA:E4
- IP: 192.168.8.130

---

## Modelo de Datos

### Tablas

**`device`**
- id, name, mac_address, ip_address, status, last_connection

**`sensor_data`**
- id, device_id, sensor_type, value, unit, timestamp

**`actuator_state`**
- id, device_id, actuator_type, state, updated_at

### Procedimientos Almacenados

```sql
CALL get_latest_readings(device_id);
CALL get_sensor_stats(device_id, sensor_type, hours);
CALL update_device_status(device_id, status);
CALL clean_old_sensor_data();
```

---

## Configuración Spring Boot

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/iot_project
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=validate
```

---

**Última actualización**: 19 de octubre de 2025  
**Versión**: 1.0