-- =====================================================
-- ESQUEMA DE BASE DE DATOS - PROYECTO DAMIOT
-- Archivo: 01_esquema.sql
-- Autor: Emilio José Salmerón Arjona
-- IES Azarquiel - Toledo
-- CFGS Desarrollo de Aplicaciones Multiplataforma
-- Curso 2025/2026
-- MySQL 8.4.3 en Laragon
-- Fecha: Diciembre 2025
-- =====================================================
-- Este archivo contiene únicamente la estructura de la base de datos:
-- - Creación de base de datos
-- - Definición de tablas
-- - Índices
-- - Procedimientos almacenados
-- =====================================================

-- Crear y seleccionar base de datos
DROP DATABASE IF EXISTS damiot_db;
CREATE DATABASE damiot_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE damiot_db;

-- =====================================================
-- DEFINICIÓN DE TABLAS
-- =====================================================

-- Tabla de dispositivos ESP32
CREATE TABLE device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT 'Nombre descriptivo del dispositivo',
    mac_address VARCHAR(17) UNIQUE NOT NULL COMMENT 'Dirección MAC del ESP32',
    ip_address VARCHAR(15) COMMENT 'Dirección IP actual',
    status ENUM('online', 'offline') DEFAULT 'offline' COMMENT 'Estado de conexión de red',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT 'Dispositivo habilitado administrativamente',
    last_connection TIMESTAMP NULL COMMENT 'Última vez que se conectó',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Dispositivos ESP32 registrados';

-- Tabla de lecturas de sensores
CREATE TABLE sensor_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT 'Referencia al dispositivo',
    sensor_type VARCHAR(50) NOT NULL COMMENT 'Tipo de sensor: temperatura, humedad, luz, etc.',
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
    actuator_type VARCHAR(50) NOT NULL COMMENT 'Tipo de actuador: led_azul, motor, relé, etc.',
    state VARCHAR(50) NOT NULL COMMENT 'Estado actual: ON, OFF, o valor numérico para PWM',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Última actualización',
    FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE,
    UNIQUE KEY unique_device_actuator (device_id, actuator_type) COMMENT 'Un actuador por dispositivo'
) ENGINE=InnoDB COMMENT='Estado actual de los actuadores';

-- Tabla de historial de eventos de actuadores
CREATE TABLE actuator_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actuator_type VARCHAR(50) NOT NULL COMMENT 'Tipo de actuador: led_azul, motor, etc.',
    command VARCHAR(20) NOT NULL COMMENT 'Comando enviado: ON, OFF, etc.',
    device_id BIGINT NOT NULL COMMENT 'Referencia al dispositivo',
    status VARCHAR(20) DEFAULT 'SENT' COMMENT 'Estado: SENT, CONFIRMED, FAILED',
    response TEXT COMMENT 'Respuesta o error del dispositivo',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Momento del evento',
    FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE,
    INDEX idx_device_id (device_id) COMMENT 'Índice para consultas por dispositivo',
    INDEX idx_actuator_type (actuator_type) COMMENT 'Índice para consultas por tipo de actuador',
    INDEX idx_timestamp (timestamp) COMMENT 'Índice para consultas temporales',
    INDEX idx_status (status) COMMENT 'Índice para filtrar por estado'
) ENGINE=InnoDB COMMENT='Historial de comandos enviados a actuadores';

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

-- Procedimiento para limpiar eventos antiguos (> 30 días)
DELIMITER $$
CREATE PROCEDURE clean_old_actuator_events()
BEGIN
    DELETE FROM actuator_events 
    WHERE timestamp < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    SELECT CONCAT('Eliminados ', ROW_COUNT(), ' eventos antiguos') as result;
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

-- Procedimiento para habilitar/deshabilitar dispositivo administrativamente
DELIMITER $$
CREATE PROCEDURE toggle_device_enabled(
    IN p_device_id BIGINT,
    IN p_is_enabled BOOLEAN
)
BEGIN
    UPDATE device 
    SET is_enabled = p_is_enabled,
        updated_at = NOW()
    WHERE id = p_device_id;
    
    SELECT CONCAT('Dispositivo ', 
                  IF(p_is_enabled = TRUE, 'habilitado', 'deshabilitado'), 
                  ' correctamente') as result;
END$$
DELIMITER ;

-- Procedimiento para obtener historial de comandos de un actuador
DELIMITER $$
CREATE PROCEDURE get_actuator_history(
    IN p_actuator_type VARCHAR(50),
    IN p_limit INT
)
BEGIN
    SELECT 
        id,
        actuator_type,
        command,
        device_id,
        status,
        response,
        timestamp
    FROM actuator_events
    WHERE actuator_type = p_actuator_type
    ORDER BY timestamp DESC
    LIMIT p_limit;
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
DESCRIBE actuator_events;

-- Listar procedimientos almacenados
SHOW PROCEDURE STATUS WHERE Db = 'damiot_db';
