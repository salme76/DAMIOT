-- =====================================================
-- DISPOSITIVO NUEVO - PROYECTO DAMIOT
-- Archivo: 04_nuevo_device.sql
-- Autor: Emilio José Salmerón Arjona
-- IES Azarquiel - Toledo
-- CFGS Desarrollo de Aplicaciones Multiplataforma
-- Curso 2024/2025
-- MySQL 8.4.3 en Laragon
-- Fecha: Diciembre 2025
-- =====================================================
-- Este script inserta un nuevo dispositivo de prueba:
-- - ESP32-Nuevo con LED Rojo y sensor DHT11
-- =====================================================

USE damiot_db;

-- =====================================================
-- DISPOSITIVO: ESP32-NUEVO
-- =====================================================

-- Insertar ESP32-Nuevo
INSERT INTO device (name, mac_address, ip_address, status, is_enabled, last_connection) VALUES
('ESP32-Nuevo', '11:22:33:44:55:66', '192.168.8.140', 'offline', TRUE, NOW())
ON DUPLICATE KEY UPDATE 
    name = 'ESP32-Nuevo',
    ip_address = '192.168.8.140',
    is_enabled = TRUE,
    updated_at = CURRENT_TIMESTAMP;

-- Obtener el ID del dispositivo
SET @device_nuevo_id = (SELECT id FROM device WHERE mac_address = '11:22:33:44:55:66');

SELECT CONCAT('✅ ESP32-Nuevo registrado con ID: ', @device_nuevo_id) as info;

-- Configurar estado actual del actuador LED Rojo
INSERT INTO actuator_state (device_id, actuator_type, state) VALUES
(@device_nuevo_id, 'led_rojo', 'OFF')
ON DUPLICATE KEY UPDATE 
    state = 'OFF',
    updated_at = CURRENT_TIMESTAMP;

SELECT CONCAT('✅ Actuador LED Rojo configurado') as info;

-- Datos iniciales de sensores DHT11 (temperatura y humedad)
INSERT INTO sensor_data (device_id, sensor_type, value, unit, timestamp) VALUES
(@device_nuevo_id, 'temperatura', 22.5, '°C', NOW()),
(@device_nuevo_id, 'humedad', 58.0, '%', NOW());

SELECT CONCAT('✅ Lecturas iniciales DHT11 insertadas') as info;

-- Historial de eventos del LED Rojo (ejemplos)
INSERT INTO actuator_events (device_id, actuator_type, command, status, timestamp) VALUES
(@device_nuevo_id, 'led_rojo', 'ON', 'CONFIRMED', NOW() - INTERVAL 1 HOUR),
(@device_nuevo_id, 'led_rojo', 'OFF', 'CONFIRMED', NOW() - INTERVAL 30 MINUTE);

SELECT CONCAT('✅ Historial de eventos insertado') as info;

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

SELECT '=====================================' as '';
SELECT '     ESP32-NUEVO INSERTADO          ' as '';
SELECT '=====================================' as '';

SELECT 
    id,
    name,
    mac_address,
    ip_address,
    status,
    is_enabled
FROM device
WHERE mac_address = '11:22:33:44:55:66';

SELECT '--- Actuadores ---' as '';
SELECT actuator_type, state, updated_at
FROM actuator_state
WHERE device_id = @device_nuevo_id;

SELECT '--- Sensores (últimas lecturas) ---' as '';
SELECT sensor_type, value, unit, timestamp
FROM sensor_data
WHERE device_id = @device_nuevo_id
ORDER BY timestamp DESC;
