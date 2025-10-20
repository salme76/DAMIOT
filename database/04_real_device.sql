-- =====================================================
-- DISPOSITIVOS REALES - PROYECTO IOT
-- Archivo: 04_real_device.sql
-- MySQL 8.4.3 en Laragon
-- =====================================================
-- Este script inserta los dispositivos ESP32 del proyecto:
-- 1. ESP32-Salón (dispositivo real, operativo)
-- 2. ESP32-Jardín (dispositivo ficticio, deshabilitado)
-- =====================================================

USE iot_project;

-- =====================================================
-- DISPOSITIVO 1: ESP32-SALÓN (REAL)
-- =====================================================

-- Insertar ESP32-Salón
INSERT INTO device (name, mac_address, ip_address, status, is_enabled, last_connection) VALUES
('ESP32-Salón', '7C:9E:BD:F1:DA:E4', '192.168.8.130', 'offline', TRUE, NULL)
ON DUPLICATE KEY UPDATE 
    name = 'ESP32-Salón',
    ip_address = '192.168.8.130',
    is_enabled = TRUE,
    updated_at = CURRENT_TIMESTAMP;

-- Obtener el ID del dispositivo Salón
SET @salon_id = (SELECT id FROM device WHERE mac_address = '7C:9E:BD:F1:DA:E4');

SELECT CONCAT('ESP32-Salón registrado con ID: ', @salon_id) as info;

-- Configurar actuadores del Salón
INSERT INTO actuator_state (device_id, actuator_type, state) VALUES
(@salon_id, 'led_blue', 'OFF')
ON DUPLICATE KEY UPDATE 
    state = 'OFF',
    updated_at = CURRENT_TIMESTAMP;

-- Datos iniciales de sensores del Salón (opcional)
INSERT INTO sensor_data (device_id, sensor_type, value, unit, timestamp) VALUES
(@salon_id, 'temperature', 23.0, '°C', NOW()),
(@salon_id, 'humidity', 65.0, '%', NOW());

-- =====================================================
-- DISPOSITIVO 2: ESP32-JARDÍN (FICTICIO, DESHABILITADO)
-- =====================================================

-- Insertar ESP32-Jardín
INSERT INTO device (name, mac_address, ip_address, status, is_enabled, last_connection) VALUES
('ESP32-Jardín', 'AA:BB:CC:DD:EE:11', NULL, 'offline', FALSE, NULL)
ON DUPLICATE KEY UPDATE 
    name = 'ESP32-Jardín',
    status = 'offline',
    is_enabled = FALSE,
    updated_at = CURRENT_TIMESTAMP;

-- Obtener el ID del dispositivo Jardín
SET @jardin_id = (SELECT id FROM device WHERE mac_address = 'AA:BB:CC:DD:EE:11');

SELECT CONCAT('ESP32-Jardín registrado con ID: ', @jardin_id) as info;

-- Configurar actuadores del Jardín
INSERT INTO actuator_state (device_id, actuator_type, state) VALUES
(@jardin_id, 'motor_riego', 'OFF'),
(@jardin_id, 'luz_servicio', 'OFF')
ON DUPLICATE KEY UPDATE 
    updated_at = CURRENT_TIMESTAMP;

-- NO insertamos lecturas de sensores porque está deshabilitado

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

SELECT '========================================' as '';
SELECT 'DISPOSITIVOS REGISTRADOS CORRECTAMENTE' as status;
SELECT '========================================' as '';

-- Mostrar información de ambos dispositivos
SELECT 
    id,
    name,
    mac_address as MAC,
    ip_address as IP,
    status,
    is_enabled as habilitado,
    last_connection,
    created_at as registrado_el
FROM device 
WHERE mac_address IN ('7C:9E:BD:F1:DA:E4', 'AA:BB:CC:DD:EE:11')
ORDER BY id;

-- Mostrar actuadores configurados
SELECT 
    d.name as dispositivo,
    a.actuator_type as tipo,
    a.state as estado,
    a.updated_at as actualizado
FROM actuator_state a
JOIN device d ON a.device_id = d.id
WHERE d.mac_address IN ('7C:9E:BD:F1:DA:E4', 'AA:BB:CC:DD:EE:11')
ORDER BY d.name, a.actuator_type;

-- Mostrar lecturas de sensores
SELECT 
    d.name as dispositivo,
    s.sensor_type as sensor,
    s.value as valor,
    s.unit as unidad,
    s.timestamp as fecha_lectura
FROM sensor_data s
JOIN device d ON s.device_id = d.id
WHERE d.mac_address IN ('7C:9E:BD:F1:DA:E4', 'AA:BB:CC:DD:EE:11')
ORDER BY d.name, s.timestamp DESC;

-- =====================================================
-- INFORMACIÓN TÉCNICA DE LOS DISPOSITIVOS
-- =====================================================

SELECT '========================================' as '';
SELECT 'CONFIGURACIÓN HARDWARE - ESP32-SALÓN' as info;
SELECT '========================================' as '';

SELECT 
    'DHT11' as componente,
    'Sensor Temperatura/Humedad' as descripcion,
    '4' as pin,
    'temperature, humidity' as mediciones
UNION ALL
SELECT 
    'LED Azul' as componente,
    'Actuador Digital' as descripcion,
    '5' as pin,
    'ON/OFF' as estados;

SELECT '========================================' as '';
SELECT 'CONFIGURACIÓN HARDWARE - ESP32-JARDÍN' as info;
SELECT '========================================' as '';

SELECT 
    'DHT11' as componente,
    'Sensor Temperatura/Humedad' as descripcion,
    '-' as pin,
    'temperature, humidity' as mediciones
UNION ALL
SELECT 
    'LDR/BH1750' as componente,
    'Sensor Luminosidad' as descripcion,
    '-' as pin,
    'light' as mediciones
UNION ALL
SELECT 
    'Motor Riego' as componente,
    'Actuador Bomba' as descripcion,
    '-' as pin,
    'ON/OFF' as estados
UNION ALL
SELECT 
    'Luz Servicio' as componente,
    'Actuador Digital' as descripcion,
    '-' as pin,
    'ON/OFF' as estados;

SELECT '========================================' as '';
SELECT 'CONFIGURACIÓN DE RED' as info;
SELECT '========================================' as '';

SELECT 
    'Router GLi.Net Mango' as componente,
    '192.168.8.1/24' as red,
    'DAMIOT' as SSID,
    '12345678' as password;

-- =====================================================
-- CONSULTAS ÚTILES
-- =====================================================

-- Obtener ID de ESP32-Salón
SELECT id FROM device WHERE mac_address = '7C:9E:BD:F1:DA:E4';

-- Obtener ID de ESP32-Jardín
SELECT id FROM device WHERE mac_address = 'AA:BB:CC:DD:EE:11';

-- Ver solo dispositivos habilitados
SELECT name, status FROM device WHERE is_enabled = TRUE;

-- Ver solo dispositivos deshabilitados
SELECT name, status FROM device WHERE is_enabled = FALSE;