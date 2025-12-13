-- =====================================================
-- DISPOSITIVOS - PROYECTO DAMIOT
-- Archivo: 02_dispositivos.sql
-- Autor: Emilio José Salmerón Arjona
-- IES Azarquiel - Toledo
-- CFGS Desarrollo de Aplicaciones Multiplataforma
-- Curso 2025/2026
-- MySQL 8.4.3 en Laragon
-- Fecha: Diciembre 2025
-- =====================================================
-- Este script inserta los dispositivos del proyecto:
-- - ESP32-Salón (dispositivo físico real)
-- - ESP32-Jardín (dispositivo ficticio para pruebas)
-- - ESP32-Garaje (dispositivo ficticio para demostración)
-- 
-- Inserta datos en TODAS las tablas:
-- - device
-- - actuator_state
-- - sensor_data
-- - actuator_events
-- =====================================================

USE damiot_db;

-- =====================================================
-- DISPOSITIVO 1: ESP32-SALÓN (REAL)
-- =====================================================

-- Insertar ESP32-Salón
INSERT INTO device (name, mac_address, ip_address, status, is_enabled, last_connection) VALUES
('ESP32-Salón', '7C:9E:BD:F1:DA:E4', '192.168.8.130', 'offline', TRUE, NOW())
ON DUPLICATE KEY UPDATE 
    name = 'ESP32-Salón',
    ip_address = '192.168.8.130',
    is_enabled = TRUE,
    updated_at = CURRENT_TIMESTAMP;

-- Obtener el ID del dispositivo
SET @device_salon_id = (SELECT id FROM device WHERE mac_address = '7C:9E:BD:F1:DA:E4');

SELECT CONCAT('✅ ESP32-Salón registrado con ID: ', @device_salon_id) as info;

-- Configurar estado actual de actuadores del ESP32-Salón
INSERT INTO actuator_state (device_id, actuator_type, state) VALUES
(@device_salon_id, 'led_azul', 'OFF')
ON DUPLICATE KEY UPDATE 
    state = 'OFF',
    updated_at = CURRENT_TIMESTAMP;

SELECT CONCAT('✅ Actuadores de ESP32-Salón configurados') as info;

-- Datos iniciales de sensores del ESP32-Salón
INSERT INTO sensor_data (device_id, sensor_type, value, unit, timestamp) VALUES
(@device_salon_id, 'temperatura', 23.0, '°C', NOW()),
(@device_salon_id, 'humedad', 65.0, '%', NOW());

SELECT CONCAT('✅ Lecturas iniciales de ESP32-Salón insertadas') as info;

-- Historial de eventos de actuadores del ESP32-Salón (ejemplos)
INSERT INTO actuator_events (device_id, actuator_type, command, status, timestamp) VALUES
(@device_salon_id, 'led_azul', 'ON', 'CONFIRMED', NOW() - INTERVAL 2 HOUR),
(@device_salon_id, 'led_azul', 'OFF', 'CONFIRMED', NOW() - INTERVAL 1 HOUR),
(@device_salon_id, 'led_azul', 'ON', 'CONFIRMED', NOW() - INTERVAL 30 MINUTE),
(@device_salon_id, 'led_azul', 'OFF', 'CONFIRMED', NOW() - INTERVAL 10 MINUTE);

SELECT CONCAT('✅ Historial de eventos de ESP32-Salón insertado') as info;

-- =====================================================
-- DISPOSITIVO 2: ESP32-JARDÍN (FICTICIO - DESHABILITADO)
-- =====================================================

-- Insertar ESP32-Jardín
INSERT INTO device (name, mac_address, ip_address, status, is_enabled, last_connection) VALUES
('ESP32-Jardín', 'AA:BB:CC:DD:EE:FF', '192.168.8.131', 'offline', FALSE, NULL)
ON DUPLICATE KEY UPDATE 
    name = 'ESP32-Jardín',
    ip_address = '192.168.8.131',
    is_enabled = FALSE,
    updated_at = CURRENT_TIMESTAMP;

-- Obtener el ID del dispositivo ficticio
SET @device_jardin_id = (SELECT id FROM device WHERE mac_address = 'AA:BB:CC:DD:EE:FF');

SELECT CONCAT('✅ ESP32-Jardín registrado con ID: ', @device_jardin_id, ' (DESHABILITADO)') as info;

-- Configurar estado actual de actuadores del ESP32-Jardín (ficticio)
INSERT INTO actuator_state (device_id, actuator_type, state) VALUES
(@device_jardin_id, 'led_verde', 'OFF'),
(@device_jardin_id, 'bomba_riego', 'OFF')
ON DUPLICATE KEY UPDATE 
    state = 'OFF',
    updated_at = CURRENT_TIMESTAMP;

SELECT CONCAT('✅ Actuadores de ESP32-Jardín configurados') as info;

-- Datos ficticios de sensores del ESP32-Jardín (para demostración)
INSERT INTO sensor_data (device_id, sensor_type, value, unit, timestamp) VALUES
(@device_jardin_id, 'temperatura', 18.5, '°C', NOW() - INTERVAL 1 HOUR),
(@device_jardin_id, 'humedad', 72.0, '%', NOW() - INTERVAL 1 HOUR),
-- Sensor tipo YL-69. Valores de 0 (muy húmedo) a 1023 (totalmente seco o desconectado)
(@device_jardin_id, 'higrómetro_suelo', 414, 'adc', NOW() - INTERVAL 1 HOUR);

SELECT CONCAT('✅ Lecturas de ESP32-Jardín insertadas') as info;

-- Historial de eventos de actuadores del ESP32-Jardín (ejemplos ficticios)
INSERT INTO actuator_events (device_id, actuator_type, command, status, timestamp) VALUES
(@device_jardin_id, 'led_verde', 'ON', 'CONFIRMED', NOW() - INTERVAL 3 HOUR),
(@device_jardin_id, 'led_verde', 'OFF', 'CONFIRMED', NOW() - INTERVAL 2 HOUR),
(@device_jardin_id, 'bomba_riego', 'ON', 'CONFIRMED', NOW() - INTERVAL 90 MINUTE),
(@device_jardin_id, 'bomba_riego', 'OFF', 'CONFIRMED', NOW() - INTERVAL 85 MINUTE);

SELECT CONCAT('✅ Historial de eventos de ESP32-Jardín insertado') as info;

-- =====================================================
-- DISPOSITIVO 3: ESP32-GARAJE (FICTICIO)
-- =====================================================

-- Insertar ESP32-Garaje
INSERT INTO device (name, mac_address, ip_address, status, is_enabled, last_connection) VALUES
('ESP32-Garaje', 'B8:27:EB:AA:BB:CC', '192.168.8.133', 'online', TRUE, NOW())
ON DUPLICATE KEY UPDATE 
    name = 'ESP32-Garaje',
    ip_address = '192.168.8.133',
    status = 'online',
    is_enabled = TRUE,
    last_connection = NOW(),
    updated_at = CURRENT_TIMESTAMP;

-- Obtener el ID del dispositivo
SET @device_garaje_id = (SELECT id FROM device WHERE mac_address = 'B8:27:EB:AA:BB:CC');

SELECT CONCAT('✅ ESP32-Garaje registrado con ID: ', @device_garaje_id) as info;

-- Configurar estado actual de actuadores del ESP32-Garaje
INSERT INTO actuator_state (device_id, actuator_type, state) VALUES
(@device_garaje_id, 'puerta_garaje', 'CLOSED'),
(@device_garaje_id, 'luz_garaje', 'OFF'),
(@device_garaje_id, 'ventilador', 'OFF')
ON DUPLICATE KEY UPDATE 
    state = VALUES(state),
    updated_at = CURRENT_TIMESTAMP;

SELECT CONCAT('✅ Actuadores de ESP32-Garaje configurados') as info;

-- Datos ficticios de sensores del ESP32-Garaje (para demostración)
INSERT INTO sensor_data (device_id, sensor_type, value, unit, timestamp) VALUES
-- Temperatura del garaje (más fresco que el salón)
(@device_garaje_id, 'temperatura', 18.5, '°C', NOW() - INTERVAL 5 MINUTE),
(@device_garaje_id, 'temperatura', 18.8, '°C', NOW() - INTERVAL 4 MINUTE),
(@device_garaje_id, 'temperatura', 19.0, '°C', NOW() - INTERVAL 3 MINUTE),
(@device_garaje_id, 'temperatura', 19.2, '°C', NOW() - INTERVAL 2 MINUTE),
(@device_garaje_id, 'temperatura', 19.5, '°C', NOW() - INTERVAL 1 MINUTE),
(@device_garaje_id, 'temperatura', 19.8, '°C', NOW()),

-- Distancia (sensor ultrasónico en cm)
-- 15 cm = coche presente, 200 cm = sin coche
(@device_garaje_id, 'distancia', 15.0, 'cm', NOW() - INTERVAL 5 MINUTE),
(@device_garaje_id, 'distancia', 15.0, 'cm', NOW() - INTERVAL 4 MINUTE),
(@device_garaje_id, 'distancia', 15.0, 'cm', NOW() - INTERVAL 3 MINUTE),
(@device_garaje_id, 'distancia', 15.0, 'cm', NOW() - INTERVAL 2 MINUTE),
(@device_garaje_id, 'distancia', 15.0, 'cm', NOW() - INTERVAL 1 MINUTE),
(@device_garaje_id, 'distancia', 15.0, 'cm', NOW()),

-- CO2 (ppm: parts per million)
-- Valores normales: 400-800 ppm (aire limpio)
-- Precaución: 800-1500 ppm (activar ventilador)
-- Peligroso: >1500 ppm (motor encendido o mala ventilación)
(@device_garaje_id, 'co2', 920, 'ppm', NOW() - INTERVAL 5 MINUTE),
(@device_garaje_id, 'co2', 850, 'ppm', NOW() - INTERVAL 4 MINUTE),
(@device_garaje_id, 'co2', 720, 'ppm', NOW() - INTERVAL 3 MINUTE),
(@device_garaje_id, 'co2', 620, 'ppm', NOW() - INTERVAL 2 MINUTE),
(@device_garaje_id, 'co2', 550, 'ppm', NOW() - INTERVAL 1 MINUTE),
(@device_garaje_id, 'co2', 480, 'ppm', NOW());

SELECT CONCAT('✅ Lecturas de ESP32-Garaje insertadas') as info;

-- Historial de eventos de actuadores del ESP32-Garaje (ejemplos ficticios)
INSERT INTO actuator_events (device_id, actuator_type, command, status, timestamp) VALUES
-- Eventos de puerta
(@device_garaje_id, 'puerta_garaje', 'OPEN', 'CONFIRMED', NOW() - INTERVAL 3 HOUR),
(@device_garaje_id, 'puerta_garaje', 'CLOSED', 'CONFIRMED', NOW() - INTERVAL 2 HOUR),
(@device_garaje_id, 'puerta_garaje', 'OPEN', 'CONFIRMED', NOW() - INTERVAL 1 HOUR),
(@device_garaje_id, 'puerta_garaje', 'CLOSED', 'CONFIRMED', NOW() - INTERVAL 30 MINUTE),

-- Eventos de luz
(@device_garaje_id, 'luz_garaje', 'ON', 'CONFIRMED', NOW() - INTERVAL 25 MINUTE),
(@device_garaje_id, 'luz_garaje', 'OFF', 'CONFIRMED', NOW() - INTERVAL 20 MINUTE),

-- Eventos de ventilador
(@device_garaje_id, 'ventilador', 'ON', 'CONFIRMED', NOW() - INTERVAL 15 MINUTE),
(@device_garaje_id, 'ventilador', 'OFF', 'CONFIRMED', NOW() - INTERVAL 10 MINUTE);

SELECT CONCAT('✅ Historial de eventos de ESP32-Garaje insertado') as info;

-- =====================================================
-- VERIFICACIÓN FINAL
-- =====================================================

SELECT '=====================================' as '';
SELECT '     RESUMEN DE DATOS INSERTADOS    ' as '';
SELECT '=====================================' as '';

-- Contar registros en cada tabla
SELECT 
    'device' as tabla,
    COUNT(*) as registros
FROM device

UNION ALL

SELECT 
    'sensor_data' as tabla,
    COUNT(*) as registros
FROM sensor_data

UNION ALL

SELECT 
    'actuator_state' as tabla,
    COUNT(*) as registros
FROM actuator_state

UNION ALL

SELECT 
    'actuator_events' as tabla,
    COUNT(*) as registros
FROM actuator_events;

-- Mostrar dispositivos registrados
SELECT '=====================================' as '';
SELECT '       DISPOSITIVOS REGISTRADOS      ' as '';
SELECT '=====================================' as '';

SELECT 
    id,
    name,
    mac_address,
    ip_address,
    status,
    is_enabled,
    last_connection
FROM device
ORDER BY id;

-- Mostrar resumen de sensores por dispositivo
SELECT '=====================================' as '';
SELECT '    SENSORES POR DISPOSITIVO         ' as '';
SELECT '=====================================' as '';

SELECT 
    d.name as dispositivo,
    s.sensor_type,
    COUNT(*) as num_lecturas,
    MIN(s.value) as min_valor,
    MAX(s.value) as max_valor,
    AVG(s.value) as promedio,
    s.unit
FROM sensor_data s
JOIN device d ON s.device_id = d.id
GROUP BY d.id, d.name, s.sensor_type, s.unit
ORDER BY d.id, s.sensor_type;

-- =====================================================
-- CONSULTAS ÚTILES (COMENTADAS)
-- =====================================================

-- Obtener ID de ESP32-Salón
-- SELECT id FROM device WHERE mac_address = '7C:9E:BD:F1:DA:E4';

-- Obtener ID de ESP32-Jardín
-- SELECT id FROM device WHERE mac_address = 'AA:BB:CC:DD:EE:FF';

-- Obtener ID de ESP32-Garaje
-- SELECT id FROM device WHERE mac_address = 'B8:27:EB:AA:BB:CC';

-- Ver estado de todos los dispositivos
-- SELECT name, mac_address, status, is_enabled FROM device;

-- Ver últimas lecturas del ESP32-Salón
-- CALL get_latest_readings((SELECT id FROM device WHERE mac_address = '7C:9E:BD:F1:DA:E4'));

-- Ver últimas lecturas del ESP32-Jardín
-- CALL get_latest_readings((SELECT id FROM device WHERE mac_address = 'AA:BB:CC:DD:EE:FF'));

-- Ver últimas lecturas del ESP32-Garaje
-- CALL get_latest_readings((SELECT id FROM device WHERE mac_address = 'B8:27:EB:AA:BB:CC'));

-- Ver historial de eventos de un actuador
-- SELECT * FROM actuator_events WHERE actuator_type = 'led_azul' ORDER BY timestamp DESC;

-- Ver todos los eventos por dispositivo
-- SELECT * FROM actuator_events WHERE device_id = 1 ORDER BY timestamp DESC;

-- Habilitar ESP32-Jardín (si quieres activarlo)
-- UPDATE device SET is_enabled = TRUE WHERE mac_address = 'AA:BB:CC:DD:EE:FF';

-- Deshabilitar ESP32-Jardín
-- UPDATE device SET is_enabled = FALSE WHERE mac_address = 'AA:BB:CC:DD:EE:FF';

-- Ver sensores del garaje
-- SELECT * FROM sensor_data WHERE device_id = (SELECT id FROM device WHERE name = 'ESP32-Garaje') ORDER BY timestamp DESC;
