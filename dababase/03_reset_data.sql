-- =====================================================
-- RESET DE DATOS - PROYECTO IOT
-- Archivo: 03_reset_data.sql
-- MySQL 8.4.3 en Laragon
-- =====================================================
-- Este script limpia todos los datos y recarga los datos de ejemplo.
-- ADVERTENCIA: Elimina TODOS los datos existentes.
-- Úsalo solo en desarrollo/pruebas, NUNCA en producción.
-- =====================================================

USE iot_project;

-- =====================================================
-- LIMPIEZA DE DATOS
-- =====================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Limpiar todas las tablas (preserva la estructura)
TRUNCATE TABLE sensor_data;
TRUNCATE TABLE actuator_state;
TRUNCATE TABLE device;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Datos eliminados correctamente' as status;

-- =====================================================
-- RECARGA DE DATOS DE EJEMPLO
-- =====================================================

-- Insertar dispositivo ESP32 de ejemplo
INSERT INTO device (name, mac_address, ip_address, status, is_enabled, last_connection) VALUES
('ESP32-Prototipo', 'AA:BB:CC:DD:EE:FF', '192.168.8.100', 'online', TRUE, NOW());

-- Obtener el ID del dispositivo insertado
SET @device_id = LAST_INSERT_ID();

-- Lecturas de TEMPERATURA
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

-- Lecturas de HUMEDAD
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

-- Lecturas de LUMINOSIDAD
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

-- Estados de ACTUADORES
INSERT INTO actuator_state (device_id, actuator_type, state) VALUES
(@device_id, 'led_red', 'OFF'),
(@device_id, 'led_green', 'ON'),
(@device_id, 'led_blue', 'OFF'),
(@device_id, 'fan', 'OFF'),
(@device_id, 'relay1', 'OFF');

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

-- Resumen
SELECT 'Datos recargados correctamente' as status;

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

-- Mostrar datos insertados
SELECT 'DISPOSITIVO:' as info;
SELECT * FROM device;

SELECT 'ÚLTIMAS LECTURAS POR SENSOR:' as info;
CALL get_latest_readings(@device_id);

SELECT 'ESTADO DE ACTUADORES:' as info;
SELECT actuator_type, state, updated_at 
FROM actuator_state 
WHERE device_id = @device_id;