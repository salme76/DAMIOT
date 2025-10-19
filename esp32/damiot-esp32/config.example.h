/*
 * Plantilla de configuración - DAMIOT ESP32
 * IES Azarquiel - Toledo
 * 
 * INSTRUCCIONES:
 * 1. Copia este archivo y renómbralo a "config.h"
 * 2. Completa los valores con tus credenciales
 * 3. NO subas config.h a Git (está en .gitignore)
 */

#ifndef CONFIG_H
#define CONFIG_H

// ===== CONFIGURACIÓN WIFI =====
const char* WIFI_SSID = "TU_WIFI_SSID";
const char* WIFI_PASSWORD = "TU_WIFI_PASSWORD";

// ===== CONFIGURACIÓN MQTT =====
// Broker público para pruebas
const char* MQTT_SERVER = "test.mosquitto.org";
const int MQTT_PORT = 1883;

// Broker local (cuando instales Mosquitto en tu PC)
// Descomenta y pon tu IP en la red local
// const char* MQTT_SERVER = "192.168.X.XXX";
// const int MQTT_PORT = 1883;

// ID único del dispositivo
const char* MQTT_CLIENT_ID = "ESP32_DAMIOT_001";

// ===== ESTRATEGIA QoS =====
// QoS 0: Telemetría (temperatura/humedad) - alta frecuencia, no crítico
// QoS 1: Comandos (LED) - crítico, idempotente, garantiza entrega

// ===== TOPICS MQTT =====
const char* TOPIC_TEMPERATURA = "damiot/temperatura";
const char* TOPIC_HUMEDAD = "damiot/humedad";
const char* TOPIC_LED = "damiot/led";
const char* TOPIC_STATUS = "damiot/status";

// ===== CONFIGURACIÓN SENSORES =====
#define DHTPIN 4
#define DHTTYPE DHT11

// ===== CONFIGURACIÓN ACTUADORES =====
#define LED_AZUL 5

// ===== INTERVALOS DE TIEMPO (milisegundos) =====
const unsigned long INTERVALO_LECTURA = 5000;  // Leer sensor cada 5 segundos
const unsigned long INTERVALO_RECONEXION = 5000;  // Reintentar conexión cada 5s

#endif
