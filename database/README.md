# Scripts de Base de Datos - DAMIOT

Conjunto de scripts SQL para el sistema DAMIOT IoT.

**Autor:** Emilio José Salmerón Arjona  
**IES Azarquiel - Toledo**  
**CFGS Desarrollo de Aplicaciones Multiplataforma**  
**Curso 2025/2026**

---

## 📋 Scripts Disponibles

| Archivo | Propósito | Cuándo usar |
|---------|-----------|-------------|
| `01_esquema.sql` | Crea la estructura completa de la BD | Primera instalación o recrear BD |
| `02_dispositivos.sql` | Inserta los 3 dispositivos con datos de prueba | Después del esquema o para repoblar |
| `03_reset_datos.sql` | Borra todos los datos (mantiene esquema) | Limpiar datos de prueba |

---

## 🚀 Instalación Completa

### Primera Vez (Instalación desde Cero)

```sql
-- Paso 1: Crear estructura de la base de datos
source D:/DAMIOT/database/01_esquema.sql

-- Paso 2: Insertar dispositivos y datos de prueba
source D:/DAMIOT/database/02_dispositivos.sql
```

### Resultado

Tendrás 3 dispositivos configurados:
- **ESP32-Salón** (REAL, online)
- **ESP32-Jardín** (ficticio, offline)
- **ESP32-Garaje** (ficticio, online)

---

## 🔄 Resetear Datos

Si quieres limpiar todos los datos y volver a empezar sin borrar el esquema:

```sql
-- Paso 1: Borrar todos los datos
source D:/DAMIOT/database/03_reset_datos.sql

-- Paso 2: Reinsertar dispositivos
source D:/DAMIOT/database/02_dispositivos.sql
```

**Útil para:**
- Limpiar datos de prueba antes de una demo
- Volver a un estado conocido
- Eliminar registros acumulados

---

## 📊 Estructura de la Base de Datos

### Tablas Principales

- **`device`** - Dispositivos ESP32 registrados (3 dispositivos)
- **`sensor_data`** - Historial de lecturas de sensores
- **`actuator_state`** - Estado actual de los actuadores (6 actuadores total)
- **`actuator_events`** - Historial de comandos enviados

### Dispositivos Incluidos

#### 1. ESP32-Salón (Dispositivo REAL)
- **MAC:** `7C:9E:BD:F1:DA:E4`
- **IP:** `192.168.8.130`
- **Estado:** Online
- **Sensores:** temperatura, humedad
- **Actuadores:** led_azul

#### 2. ESP32-Jardín (FICTICIO - Deshabilitado)
- **MAC:** `AA:BB:CC:DD:EE:FF`
- **IP:** `192.168.8.131`
- **Estado:** Offline
- **Sensores:** temperatura, humedad, higrómetro_suelo
- **Actuadores:** led_verde, bomba_riego

#### 3. ESP32-Garaje (FICTICIO)
- **MAC:** `B8:27:EB:AA:BB:CC`
- **IP:** `192.168.8.133`
- **Estado:** Online
- **Sensores:** temperatura, distancia, co2
- **Actuadores:** puerta_garaje, luz_garaje, ventilador

---

## 🔧 Configuración de MySQL

**Base de datos:** `damiot_db`  
**Motor:** MySQL 8.4.3 en Laragon  
**Charset:** utf8mb4 (soporta emojis y caracteres especiales)  
**Collation:** utf8mb4_unicode_ci

---

## 📝 Consultas Útiles

```sql
-- Ver todos los dispositivos
SELECT id, name, mac_address, status, is_enabled FROM device;

-- Ver últimas lecturas de sensores del ESP32-Salón
SELECT sensor_type, value, unit, timestamp 
FROM sensor_data 
WHERE device_id = 1 
ORDER BY timestamp DESC 
LIMIT 10;

-- Ver sensores agrupados por dispositivo
SELECT 
    d.name as dispositivo,
    s.sensor_type,
    COUNT(*) as num_lecturas,
    AVG(s.value) as promedio,
    s.unit
FROM sensor_data s
JOIN device d ON s.device_id = d.id
GROUP BY d.name, s.sensor_type, s.unit
ORDER BY d.id, s.sensor_type;

-- Ver estado de todos los actuadores
SELECT 
    d.name as dispositivo,
    a.actuator_type,
    a.state,
    a.updated_at
FROM actuator_state a
JOIN device d ON a.device_id = d.id
ORDER BY d.id, a.actuator_type;

-- Ver historial de eventos reciente
SELECT 
    d.name as dispositivo,
    e.actuator_type,
    e.command,
    e.status,
    e.timestamp
FROM actuator_events e
JOIN device d ON e.device_id = d.id
ORDER BY e.timestamp DESC 
LIMIT 20;

-- Habilitar/deshabilitar dispositivo
UPDATE device SET is_enabled = TRUE WHERE id = 2;
UPDATE device SET is_enabled = FALSE WHERE id = 2;

-- Usar stored procedures
CALL get_latest_readings(1);
CALL get_sensor_stats(1, 'temperatura', 24);
```

---

## 🎯 Datos de Prueba Incluidos

El script `02_dispositivos.sql` inserta datos realistas:

### ESP32-Salón
- 2 lecturas iniciales de sensores
- 1 actuador configurado
- 4 eventos históricos

### ESP32-Jardín
- 3 lecturas de sensores con timestamps pasados
- 2 actuadores configurados
- 4 eventos históricos

### ESP32-Garaje
- 18 lecturas de sensores (6 por cada tipo)
- 3 actuadores configurados
- 8 eventos históricos

**Total:** ~30 registros de sensores, 6 actuadores, 16 eventos históricos

---

## ⚠️ Notas Importantes

- Los scripts usan `ON DUPLICATE KEY UPDATE` para ser idempotentes (se pueden ejecutar múltiples veces)
- Los datos ficticios tienen timestamps relativos (`NOW() - INTERVAL ...`)
- El ESP32-Jardín está deshabilitado por defecto (`is_enabled = FALSE`)
- El ESP32-Garaje está configurado como `online` para demostraciones
- Las Foreign Keys tienen `ON DELETE CASCADE` para mantener integridad referencial

---

## 🧹 Mantenimiento

### Limpieza de Datos Antiguos

Los stored procedures `clean_old_sensor_data()` y `clean_old_actuator_events()` eliminan datos mayores a 30 días:

```sql
-- Ejecutar limpieza manual
CALL clean_old_sensor_data();
CALL clean_old_actuator_events();
```

Para automatizar en producción, crear un evento MySQL:

```sql
CREATE EVENT cleanup_old_data
ON SCHEDULE EVERY 1 DAY
DO BEGIN
    CALL clean_old_sensor_data();
    CALL clean_old_actuator_events();
END;
```

---

## 📚 Arquitectura Multi-dispositivo

El sistema soporta múltiples dispositivos ESP32 simultáneamente mediante:

1. **Identificación única:** Cada ESP32 tiene una MAC address única
2. **Topics MQTT dinámicos:** `damiot/sensores/{MAC}/temperatura`
3. **Enrutamiento automático:** El backend asocia la MAC con el `device_id`

Para agregar un nuevo dispositivo:
1. Insertar en la tabla `device` con su MAC única
2. Flashear el firmware estándar (detecta su propia MAC)
3. No requiere cambios en backend ni Android

---

## 🔐 Consideraciones de Seguridad

Este proyecto es una **demostración académica**. Para producción:

- ✅ Implementar autenticación (JWT/OAuth)
- ✅ Usar HTTPS en lugar de HTTP
- ✅ Habilitar MQTTS (TLS) en lugar de MQTT plano
- ✅ Cifrar credenciales en la configuración
- ✅ Configurar backups automáticos
- ✅ Implementar rate limiting en la API

---

## 📚 Más Información

Ver documentación completa del proyecto en `/documentacion/`
