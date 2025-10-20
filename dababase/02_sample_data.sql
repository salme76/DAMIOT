-- =====================================================
-- DATOS DE PRUEBA - PROYECTO IOT
-- Archivo: 02_sample_data.sql
-- MySQL 8.4.3 en Laragon
-- =====================================================
-- Este archivo contiene datos de ejemplo para probar el sistema:
-- - Dispositivo ESP32 de prueba
-- - Lecturas históricas de sensores (últimas 2 horas)
-- - Estados iniciales de actuadores
-- - Consultas de verificación
-- =====================================================

USE iot_project;

-- =====================================================
-- INSERCIÓN DE DATOS DE EJEMPLO
-- =====================================================

-- Limpiar datos existentes (opcional, solo para reiniciar)
-- DELETE FROM sensor_data;
-- DELETE FROM actuator_state;
-- DELETE FROM device;

-- Insertar dispositivo ESP32 de ejemplo
INSERT INTO device (name, mac_address, ip_address, status, is_enabled, last_connection) VALUES
('ESP32-Prototipo', 'AA:BB:CC:DD:EE:FF', '192.168.8.100', 'online', TRUE, NOW());

-- Obtener el ID del dispositivo insertado
SET @device_id = LAST_INSERT_ID();

-- =====================================================
-- DATOS DE SENSORES - ÚLTIMAS 2 HORAS
-- =====================================================

-- Lecturas de TEMPERATURA (cada 10 minutos)
INSERT INTO sensor_data (device_id, sensor_type, value, unit, timestamp) VALUES
(@device_id, 'temperature', 22.5, '°C', DATE_SUB(NOW(), INTERVAL 120 MINUTE)),
(@device_id, 'temperature', 22.8, '°C', DATE_SUB(NOW(), INTERVAL 110 MINUTE)),
(@device_id, 'temperature', 23.1, '°C', DATE_SUB(NOW(), INTERVAL 100 MINUTE)),
(@device_id, 'temperature', 23.5, '°C', DATE_SUB(NOW(), INTERVAL 90 MINUTE)),
(@device_id, 'temperature', 23.8, '°C', DATE_SUB(NOW(), INTERVAL 80 MINUTE)),
(@device_id, 'temperature', 24.2, '°C', DATE_SUB(NOW(), INTERVAL 70 MINUTE)),
(@device_id, 'temperature', 24.5, '°C', DATE_SUB(NOW(), INTERVAL 60 MINUTE)),
(@device_id, 'temperature', 24.8, '°C', DATE_SUB(NOW(), INTERVAL 50 MINUTE)),
(@device_id, 'temperature', 25.0, '°C', DATE_SUB(NOW(), INTERVAL 40 MINUTE)),
(@device_id, 'temperature', 25.2, '°C', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(@device_id, 'temperature', 25.5, '°C', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
(@device_id, 'temperature', 25.3, '°C', DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
(@device_id, 'temperature', 25.1, '°C', NOW());

-- Lecturas de HUMEDAD (cada 10 minutos)
INSERT INTO sensor_data (device_id, sensor_type, value, unit, timestamp) VALUES
(@device_id, 'humidity', 68.0, '%', DATE_SUB(NOW(), INTERVAL 120 MINUTE)),
(@device_id, 'humidity', 67.5, '%', DATE_SUB(NOW(), INTERVAL 110 MINUTE)),
(@device_id, 'humidity', 67.0, '%', DATE_SUB(NOW(), INTERVAL 100 MINUTE)),
(@device_id, 'humidity', 66.5, '%', DATE_SUB(NOW(), INTERVAL 90 MINUTE)),
(@device_id, 'humidity', 66.0, '%', DATE_SUB(NOW(), INTERVAL 80 MINUTE)),
(@device_id, 'humidity', 65.5, '%', DATE_SUB(NOW(), INTERVAL 70 MINUTE)),
(@device_id, 'humidity', 65.0, '%', DATE_SUB(NOW(), INTERVAL 60 MINUTE)),
(@device_id, 'humidity', 64.8, '%', DATE_SUB(NOW(), INTERVAL 50 MINUTE)),
(@device_id, 'humidity', 64.5, '%', DATE_SUB(NOW(), INTERVAL 40 MINUTE)),
(@device_id, 'humidity', 64.2, '%', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(@device_id, 'humidity', 64.0, '%', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
(@device_id, 'humidity', 63.8, '%', DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
(@device_id, 'humidity', 63.5, '%', NOW());

-- Lecturas de LUMINOSIDAD (cada 10 minutos)
INSERT INTO sensor_data (device_id, sensor_type, value, unit, timestamp) VALUES
(@device_id, 'light', 450.0, 'lux', DATE_SUB(NOW(), INTERVAL 120 MINUTE)),
(@device_id, 'light', 520.0, 'lux', DATE_SUB(NOW(), INTERVAL 110 MINUTE)),
(@device_id, 'light', 580.0, 'lux', DATE_SUB(NOW(), INTERVAL 100 MINUTE)),
(@device_id, 'light', 650.0, 'lux', DATE_SUB(NOW(), INTERVAL 90 MINUTE)),
(@device_id, 'light', 720.0, 'lux', DATE_SUB(NOW(), INTERVAL 80 MINUTE)),
(@device_id, 'light', 780.0, 'lux', DATE_SUB(NOW(), INTERVAL 70 MINUTE)),
(@device_id, 'light', 820.0, 'lux', DATE_SUB(NOW(), INTERVAL 60 MINUTE)),
(@device_id, 'light', 850.0, 'lux', DATE_SUB(NOW(), INTERVAL 50 MINUTE)),
(@device_id, 'light', 830.0, 'lux', DATE_SUB(NOW(), INTERVAL 40 MINUTE)),
(@device_id, 'light', 790.0, 'lux', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(@device_id, 'light', 720.0, 'lux', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
(@device_id, 'light', 650.0, 'lux', DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
(@device_id, 'light', 580.0, 'lux', NOW());

-- =====================================================
-- ESTADOS INICIALES DE ACTUADORES
-- =====================================================

INSERT INTO actuator_state (device_id, actuator_type, state) VALUES
(@device_id, 'led_red', 'OFF'),
(@device_id, 'led_green', 'ON'),
(@device_id, 'led_blue', 'OFF'),
(@device_id, 'fan', 'OFF'),
(@device_id, 'relay1', 'OFF');

-- =====================================================
-- CONSULTAS DE VERIFICACIÓN
-- =====================================================

-- Resumen de datos insertados
SELECT 
    'Dispositivos' as tabla,
    COUNT(*) as registros
FROM device
UNION ALL
SELECT 
    'Lecturas de sensores',
    COUNT(*)
FROM sensor_data
UNION ALL
SELECT 
    'Estados de actuadores',
    COUNT(*)
FROM actuator_state;

-- Información del dispositivo
SELECT * FROM device;

-- Últimas 5 lecturas de cada tipo de sensor
SELECT 
    sensor_type,
    value,
    unit,
    timestamp
FROM (
    SELECT 
        sensor_type,
        value,
        unit,
        timestamp,
        ROW_NUMBER() OVER (PARTITION BY sensor_type ORDER BY timestamp DESC) as rn
    FROM sensor_data
    WHERE device_id = @device_id
) ranked
WHERE rn <= 5
ORDER BY sensor_type, timestamp DESC;

-- Estado actual de todos los actuadores
SELECT 
    actuator_type,
    state,
    updated_at
FROM actuator_state
WHERE device_id = @device_id
ORDER BY actuator_type;

-- =====================================================
-- CONSULTAS ÚTILES PARA DESARROLLO
-- =====================================================

-- Promedios de sensores en la última hora
SELECT 
    sensor_type,
    COUNT(*) as num_readings,
    AVG(value) as avg_value,
    MIN(value) as min_value,
    MAX(value) as max_value,
    unit
FROM sensor_data
WHERE device_id = @device_id
  AND timestamp >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
GROUP BY sensor_type, unit;

-- Evolución de temperatura (últimas 10 lecturas)
SELECT 
    value as temperatura,
    unit,
    timestamp
FROM sensor_data
WHERE device_id = @device_id
  AND sensor_type = 'temperature'
ORDER BY timestamp DESC
LIMIT 10;

-- Prueba de procedimiento almacenado: obtener últimas lecturas
CALL get_latest_readings(@device_id);

-- Prueba de procedimiento almacenado: estadísticas de temperatura (última hora)
CALL get_sensor_stats(@device_id, 'temperature', 1);