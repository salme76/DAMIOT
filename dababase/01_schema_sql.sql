-- =====================================================
-- ESQUEMA DE BASE DE DATOS - PROYECTO IOT
-- Archivo: 01_schema.sql
-- MySQL 8.4.3 en Laragon
-- =====================================================
-- Este archivo contiene únicamente la estructura de la base de datos:
-- - Creación de base de datos
-- - Definición de tablas
-- - Índices
-- - Procedimientos almacenados
-- =====================================================

-- Crear y seleccionar base de datos
DROP DATABASE IF EXISTS iot_project;
CREATE DATABASE iot_project CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE iot_project;

-- =====================================================
-- DEFINICIÓN DE TABLAS
-- =====================================================

-- Tabla de dispositivos ESP32
CREATE TABLE device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT 'Nombre descriptivo del dispositivo',
    mac_address VARCHAR(17) UNIQUE NOT NULL COMMENT 'Dirección MAC del ESP32',
    ip_address VARCHAR(15) COMMENT 'Dirección IP actual',
    status ENUM('online', 'offline') DEFAULT 'offline' COMMENT 'Estado de conexión',
    last_connection TIMESTAMP NULL COMMENT 'Última vez que se conectó',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Dispositivos ESP32 registrados';

-- Tabla de lecturas de sensores
CREATE TABLE sensor_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT 'Referencia al dispositivo',
    sensor_type VARCHAR(50) NOT NULL COMMENT 'Tipo de sensor: temperature, humidity, light, etc.',
    value DECIMAL(10,2) NOT NULL COMMENT 'Valor medido',
    unit VARCHAR(20) COMMENT 'Unidad de medida: °C, %, lux, etc.',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Momento de la lectura',
    FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE,
    INDEX idx_device_timestamp (device_id, timestamp) COMMENT 'Índice para consultas por dispositivo y tiempo',
    INDEX idx_timestamp (timestamp) COMMENT 'Índice para consultas temporales',
    INDEX idx_sensor_type (sensor_type) COMMENT 'Índice para filtrar por tipo de sensor'
) ENGINE=InnoDB COMMENT='Lecturas históricas de sensores';

-- Tabla de estados de actuadores
CREATE TABLE actuator_state (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT 'Referencia al dispositivo',
    actuator_type VARCHAR(50) NOT NULL COMMENT 'Tipo de actuador: led_red, led_green, fan, relay, etc.',
    state VARCHAR(50) NOT NULL COMMENT 'Estado actual: ON, OFF, o valor numérico para PWM',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Última actualización',
    FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE,
    UNIQUE KEY unique_device_actuator (device_id, actuator_type) COMMENT 'Un actuador por dispositivo'
) ENGINE=InnoDB COMMENT='Estado actual de los actuadores';

-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS
-- =====================================================

-- Procedimiento para limpiar datos antiguos (> 30 días)
DELIMITER $$
CREATE PROCEDURE clean_old_sensor_data()
BEGIN
    DELETE FROM sensor_data 
    WHERE timestamp < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    SELECT CONCAT('Eliminadas ', ROW_COUNT(), ' lecturas antiguas') as result;
END$$
DELIMITER ;

-- Procedimiento para obtener últimas lecturas de cada sensor
DELIMITER $$
CREATE PROCEDURE get_latest_readings(IN p_device_id BIGINT)
BEGIN
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
        WHERE device_id = p_device_id
    ) ranked
    WHERE rn = 1;
END$$
DELIMITER ;

-- Procedimiento para obtener estadísticas de un sensor en un periodo
DELIMITER $$
CREATE PROCEDURE get_sensor_stats(
    IN p_device_id BIGINT,
    IN p_sensor_type VARCHAR(50),
    IN p_hours INT
)
BEGIN
    SELECT 
        sensor_type,
        COUNT(*) as total_readings,
        AVG(value) as avg_value,
        MIN(value) as min_value,
        MAX(value) as max_value,
        STDDEV(value) as stddev_value,
        MIN(timestamp) as first_reading,
        MAX(timestamp) as last_reading
    FROM sensor_data
    WHERE device_id = p_device_id
      AND sensor_type = p_sensor_type
      AND timestamp >= DATE_SUB(NOW(), INTERVAL p_hours HOUR)
    GROUP BY sensor_type;
END$$
DELIMITER ;

-- Procedimiento para actualizar estado de dispositivo
DELIMITER $$
CREATE PROCEDURE update_device_status(
    IN p_device_id BIGINT,
    IN p_status ENUM('online', 'offline')
)
BEGIN
    UPDATE device 
    SET status = p_status,
        last_connection = IF(p_status = 'online', NOW(), last_connection)
    WHERE id = p_device_id;
    
    SELECT 'Estado actualizado correctamente' as result;
END$$
DELIMITER ;

-- =====================================================
-- VERIFICACIÓN DE ESTRUCTURA
-- =====================================================

-- Mostrar tablas creadas
SHOW TABLES;

-- Mostrar estructura de cada tabla
DESCRIBE device;
DESCRIBE sensor_data;
DESCRIBE actuator_state;

-- Listar procedimientos almacenados
SHOW PROCEDURE STATUS WHERE Db = 'iot_project';
