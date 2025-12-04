/**
 * Archivo de configuración - DAMIOT ESP32
 * 
 * Centraliza todas las constantes y configuraciones del ESP32.
 * 
 * @author Emilio José Salmerón Arjona
 * @version 2.0
 * 
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */

#ifndef CONFIG_H
#define CONFIG_H

// ===== CONFIGURACIÓN WIFI =====
// Red IoT dedicada al proyecto (Router GLi.Net Mango)
const char* WIFI_SSID = "DAMIOT";
const char* WIFI_PASSWORD = "12345678";

// ===== CONFIGURACIÓN MQTT =====
// Broker Mosquitto ejecutándose en el PC del backend
const char* MQTT_SERVER = "192.168.8.136";
const int MQTT_PORT = 1883;
const char* MQTT_CLIENT_ID = "ESP32-Salon";

// ===== TOPICS MQTT BASE (en español) =====
// NOTA: Los topics reales incluirán la MAC del dispositivo dinámicamente
// Formato: damiot/{categoria}/{MAC}/{sensor/actuador}
// Ejemplo: damiot/sensores/7C:9E:BD:F1:DA:E4/temperatura

// Bases de topics para sensores
const char* TOPIC_BASE_SENSORES = "damiot/sensores/";
const char* SENSOR_TEMPERATURA = "temperatura";
const char* SENSOR_HUMEDAD = "humedad";

// Bases de topics para actuadores
const char* TOPIC_BASE_ACTUADORES = "damiot/actuadores/";
const char* ACTUADOR_LED_AZUL = "led_azul";

// Sufijo para confirmación de estado
const char* SUFIJO_ESTADO = "/estado";

// Topic de estado general del dispositivo
const char* TOPIC_STATUS = "damiot/dispositivo/estado";

// ===== CONFIGURACIÓN SENSORES =====
// DHT11: Sensor de temperatura y humedad
#define DHTPIN 4        // GPIO4 - Conexión al pin DATA del DHT11
#define DHTTYPE DHT11   // Tipo de sensor (DHT11, no DHT22)

// ===== CONFIGURACIÓN ACTUADORES =====
// LED azul para pruebas de control remoto
#define LED_AZUL 5      // GPIO5 - LED azul

// ===== INTERVALOS DE TIEMPO (milisegundos) =====
// Valores optimizados para demo (detección rápida de desconexión)
// - Keep-alive MQTT: 10 segundos
// - Timeout broker: ~15 segundos (1.5x keep-alive)
// - Backend verifica inactividad cada 15 segundos
// - Backend marca offline después de 30 segundos sin heartbeat
const unsigned long INTERVALO_LECTURA = 5000;      // Leer sensor cada 5 segundos
const unsigned long INTERVALO_HEARTBEAT = 10000;   // Heartbeat cada 10 segundos
const unsigned long INTERVALO_RECONEXION = 5000;   // Reintentar conexión cada 5 segundos

// ===== CONFIGURACIÓN MQTT AVANZADA =====
// Keep-alive en segundos (PubSubClient)
// El broker declara al cliente muerto tras 1.5x este valor sin respuesta
const int MQTT_KEEP_ALIVE = 10;  // 10 segundos -> timeout ~15s

#endif
