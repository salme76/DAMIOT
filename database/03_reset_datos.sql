-- =====================================================
-- RESET DE DATOS - PROYECTO DAMIOT
-- Archivo: 03_reset_datos.sql
-- Autor: Emilio José Salmerón Arjona
-- IES Azarquiel - Toledo
-- CFGS Desarrollo de Aplicaciones Multiplataforma
-- Curso 2025/2026
-- =====================================================
-- Este script BORRA todos los datos de las tablas
-- pero mantiene la estructura (esquema).
-- 
-- Útil para limpiar datos de prueba y volver a empezar.
-- 
-- PRECAUCIÓN: Este script borra TODOS los datos.
-- =====================================================

USE damiot_db;

-- Deshabilitar verificación de claves foráneas temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- BORRAR DATOS
-- =====================================================

SELECT '=====================================' as '';
SELECT '     BORRANDO DATOS DE TABLAS       ' as '';
SELECT '=====================================' as '';

-- Borrar eventos de actuadores (historial)
DELETE FROM actuator_events;
SELECT CONCAT('✅ Tabla actuator_events limpiada (', ROW_COUNT(), ' registros eliminados)') as resultado;

-- Borrar estados de actuadores
DELETE FROM actuator_state;
SELECT CONCAT('✅ Tabla actuator_state limpiada (', ROW_COUNT(), ' registros eliminados)') as resultado;

-- Borrar lecturas de sensores
DELETE FROM sensor_data;
SELECT CONCAT('✅ Tabla sensor_data limpiada (', ROW_COUNT(), ' registros eliminados)') as resultado;

-- Borrar dispositivos
DELETE FROM device;
SELECT CONCAT('✅ Tabla device limpiada (', ROW_COUNT(), ' registros eliminados)') as resultado;

-- Reiniciar auto_increment de las tablas
ALTER TABLE device AUTO_INCREMENT = 1;
ALTER TABLE sensor_data AUTO_INCREMENT = 1;
ALTER TABLE actuator_state AUTO_INCREMENT = 1;
ALTER TABLE actuator_events AUTO_INCREMENT = 1;

SELECT '✅ Contadores AUTO_INCREMENT reiniciados' as resultado;

-- Reactivar verificación de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

SELECT '=====================================' as '';
SELECT '          VERIFICACIÓN               ' as '';
SELECT '=====================================' as '';

SELECT 'device' as tabla, COUNT(*) as registros FROM device
UNION ALL
SELECT 'sensor_data' as tabla, COUNT(*) as registros FROM sensor_data
UNION ALL
SELECT 'actuator_state' as tabla, COUNT(*) as registros FROM actuator_state
UNION ALL
SELECT 'actuator_events' as tabla, COUNT(*) as registros FROM actuator_events;

SELECT '=====================================' as '';
SELECT '✅ Base de datos limpia y lista' as resultado;
SELECT '📝 Ejecuta 02_dispositivos.sql para insertar dispositivos' as siguiente_paso;
SELECT '=====================================' as '';
