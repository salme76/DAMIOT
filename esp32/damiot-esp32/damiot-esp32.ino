/**
 * DAMIOT - Sistema IoT Multiplataforma
 * Firmware ESP32 con DHT11 y control LED via MQTT
 * 
 * Este es el firmware principal del ESP32-Salón, que forma parte
 * del proyecto DAMIOT (Desarrollo de Aplicaciones Multiplataforma IoT).
 * 
 * Funcionalidades:
 *   - Lectura de temperatura y humedad (sensor DHT11)
 *   - Control remoto de LED azul via MQTT
 *   - Heartbeat con IP dinámica para monitoreo
 *   - Last Will & Testament (LWT) para detección de desconexión
 *   - Reconexión automática WiFi y MQTT
 *   - Topics MQTT con identificador MAC (soporte multi-dispositivo)
 * 
 * Hardware:
 *   - ESP32 DevKitC WROOM-32D
 *   - DHT11 en GPIO4 (temperatura y humedad)
 *   - LED Azul en GPIO5
 *   - MAC: 7C:9E:BD:F1:DA:E4
 * 
 * Comunicación:
 *   - WiFi: Red DAMIOT (192.168.8.x)
 *   - MQTT: Broker Mosquitto en 192.168.8.136:1883
 *   - Topics en español con MAC para identificación única
 * 
 * Estructura de Topics MQTT:
 *   - Sensores: damiot/sensores/{MAC}/temperatura
 *   - Actuadores: damiot/actuadores/{MAC}/led_azul
 *   - Heartbeat: damiot/heartbeat/{MAC}
 * 
 * Tiempos (sincronizados con backend):
 *   - Lectura sensores: cada 5 segundos
 *   - Heartbeat: cada 10 segundos
 *   - Backend verifica: cada 15 segundos
 *   - Umbral offline: 30 segundos sin heartbeat
 * 
 * Optimizaciones v2.1:
 *   - Buffers estáticos para topics (sin String concatenation)
 *   - Eliminada fragmentación de heap
 *   - Memoria predecible y estable
 * 
 * @author Emilio José Salmerón Arjona
 * @version 2.1
 * 
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */

#include <WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>
#include "config.h"

// ===== CLIENTES DE COMUNICACIÓN =====
WiFiClient wifiClient;          // Cliente WiFi para conexión de red
PubSubClient mqttClient(wifiClient);  // Cliente MQTT sobre WiFi

// ===== SENSOR DHT11 =====
DHT dht(DHTPIN, DHTTYPE);       // Sensor de temperatura y humedad

// ===== VARIABLES DE TIEMPO =====
// Usamos millis() para temporización no bloqueante
unsigned long ultimaLectura = 0;      // Última lectura de sensores
unsigned long ultimoHeartbeat = 0;    // Último heartbeat enviado

// ===== VARIABLES DE SENSORES =====
float temperatura = 0.0;        // Última temperatura leída
float humedad = 0.0;            // Última humedad leída

// ===== BUFFERS ESTÁTICOS PARA TOPICS MQTT =====
// Pre-construidos en setup() para evitar fragmentación del heap
char macAddress[18];                    // "AA:BB:CC:DD:EE:FF\0"
char topicHeartbeat[50];                // "damiot/heartbeat/{MAC}\0"
char topicLED[60];                      // "damiot/actuadores/{MAC}/led_azul\0"
char topicLEDEstado[70];                // "damiot/actuadores/{MAC}/led_azul/estado\0"
char topicTemperatura[60];              // "damiot/sensores/{MAC}/temperatura\0"
char topicHumedad[60];                  // "damiot/sensores/{MAC}/humedad\0"

// ===== BUFFERS AUXILIARES =====
char ipBuffer[16];                      // Buffer para IP como string
char valueBuffer[8];                    // Buffer para valores de sensores
char messageBuffer[32];                 // Buffer para mensajes recibidos


/**
 * Configuración inicial del sistema
 * Se ejecuta una sola vez al encender o reiniciar el ESP32
 */
void setup() {
  // Inicializar comunicación serie para debugging
  Serial.begin(115200);
  delay(1000);
  
  // Mostrar banner de inicio
  Serial.println("\n\n=================================");
  Serial.println("    DAMIOT - Sistema IoT v2.1");
  Serial.println("    ESP32-Salón");
  Serial.println("    IES Azarquiel - Toledo");
  Serial.println("=================================\n");
  
  // Configurar LED como salida y apagarlo
  pinMode(LED_AZUL, OUTPUT);
  digitalWrite(LED_AZUL, LOW);
  Serial.println("[OK] LED azul configurado (GPIO5)");
  
  // Inicializar sensor DHT11
  dht.begin();
  Serial.println("[OK] Sensor DHT11 inicializado (GPIO4)");
  
  // Conectar a red WiFi
  conectarWiFi();
  
  // Obtener y guardar dirección MAC en buffer estático
  String macStr = WiFi.macAddress();
  macStr.toCharArray(macAddress, sizeof(macAddress));
  Serial.print("[INFO] MAC del dispositivo: ");
  Serial.println(macAddress);
  
  // Construir topics MQTT una sola vez usando la MAC
  // Esto evita crear Strings en el heap durante la ejecución
  snprintf(topicHeartbeat, sizeof(topicHeartbeat), "damiot/heartbeat/%s", macAddress);
  snprintf(topicLED, sizeof(topicLED), "damiot/actuadores/%s/%s", macAddress, ACTUADOR_LED_AZUL);
  snprintf(topicLEDEstado, sizeof(topicLEDEstado), "damiot/actuadores/%s/%s/estado", macAddress, ACTUADOR_LED_AZUL);
  snprintf(topicTemperatura, sizeof(topicTemperatura), "damiot/sensores/%s/%s", macAddress, SENSOR_TEMPERATURA);
  snprintf(topicHumedad, sizeof(topicHumedad), "damiot/sensores/%s/%s", macAddress, SENSOR_HUMEDAD);
  
  Serial.println("[INFO] Topics MQTT construidos:");
  Serial.print("  Heartbeat: ");
  Serial.println(topicHeartbeat);
  Serial.print("  LED: ");
  Serial.println(topicLED);
  Serial.print("  Temperatura: ");
  Serial.println(topicTemperatura);
  Serial.print("  Humedad: ");
  Serial.println(topicHumedad);
  
  // Configurar cliente MQTT
  mqttClient.setServer(MQTT_SERVER, MQTT_PORT);
  mqttClient.setKeepAlive(MQTT_KEEP_ALIVE);  // Keep-alive de 10 segundos
  mqttClient.setCallback(callbackMQTT);
  
  Serial.println("\n=================================");
  Serial.println("    Sistema iniciado");
  Serial.println("=================================\n");
}


/**
 * Bucle principal del programa
 * Se ejecuta continuamente mientras el ESP32 esté encendido
 */
void loop() {
  // Verificar conexión WiFi
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("[!] WiFi desconectado. Reconectando...");
    conectarWiFi();
  }
  
  // Verificar conexión MQTT
  if (!mqttClient.connected()) {
    reconectarMQTT();
  }
  
  // Procesar mensajes MQTT entrantes
  mqttClient.loop();
  
  // Leer sensores cada INTERVALO_LECTURA (5 segundos)
  unsigned long tiempoActual = millis();
  if (tiempoActual - ultimaLectura >= INTERVALO_LECTURA) {
    leerYPublicarSensor();
    ultimaLectura = tiempoActual;
  }
  
  // Enviar heartbeat cada INTERVALO_HEARTBEAT (10 segundos)
  if (tiempoActual - ultimoHeartbeat >= INTERVALO_HEARTBEAT) {
    enviarHeartbeat();
    ultimoHeartbeat = tiempoActual;
  }
}


/**
 * Conecta el ESP32 a la red WiFi configurada
 * 
 * Usa millis() para timeout no bloqueante.
 * Si falla después de 10 segundos, reinicia el ESP32.
 */
void conectarWiFi() {
  Serial.print("Conectando a WiFi: ");
  Serial.print(WIFI_SSID);
  Serial.print(" ");
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  
  // Esperar conexión con timeout no bloqueante
  unsigned long inicio = millis();
  while (WiFi.status() != WL_CONNECTED && (millis() - inicio) < 10000) {
    delay(500);
    Serial.print(".");
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n[OK] WiFi conectado!");
    Serial.print("     IP: ");
    Serial.println(WiFi.localIP());
    Serial.print("     MAC: ");
    Serial.println(macAddress);
  } else {
    Serial.println("\n[ERROR] No se pudo conectar a WiFi");
    Serial.println("        Reiniciando en 5 segundos...");
    delay(5000);
    ESP.restart();
  }
}


/**
 * Reconecta al broker MQTT con Last Will & Testament (LWT)
 * 
 * LWT: Si el ESP32 se desconecta abruptamente (corte de energía,
 * crash, etc.), el broker publicará automáticamente "offline"
 * en el topic de heartbeat, notificando al backend inmediatamente.
 * 
 * Se suscribe al topic de actuadores con la MAC del dispositivo.
 */
void reconectarMQTT() {
  // Intentar reconectar sin bloquear indefinidamente
  static unsigned long ultimoIntento = 0;
  unsigned long tiempoActual = millis();
  
  if (tiempoActual - ultimoIntento < INTERVALO_RECONEXION) {
    return;  // No reintentar aún
  }
  
  ultimoIntento = tiempoActual;
  
  if (!mqttClient.connected()) {
    Serial.print("Conectando a MQTT: ");
    Serial.print(MQTT_SERVER);
    Serial.print(":");
    Serial.println(MQTT_PORT);
    
    // Mensaje LWT que se publicará si hay desconexión abrupta
    const char* lwtMessage = "offline";
    
    // Conectar con LWT configurado
    // Parámetros: clientId, willTopic, willQoS, willRetain, willMessage
    if (mqttClient.connect(MQTT_CLIENT_ID, 
                          topicHeartbeat,   // Usa buffer estático
                          0,                // QoS 0 para LWT
                          true,             // Retain = true
                          lwtMessage)) {
      
      Serial.println("[OK] MQTT conectado con LWT!");
      Serial.print("     Client ID: ");
      Serial.println(MQTT_CLIENT_ID);
      Serial.print("     LWT topic: ");
      Serial.println(topicHeartbeat);
      
      // Suscribirse al topic del LED con QoS 1
      // QoS 1 garantiza entrega de comandos (idempotente)
      mqttClient.subscribe(topicLED, 1);
      Serial.print("     Suscrito a: ");
      Serial.println(topicLED);
      
      // Publicar estado ONLINE
      mqttClient.publish(TOPIC_STATUS, "ONLINE");
      
      // Enviar heartbeat inicial
      enviarHeartbeat();
      
    } else {
      Serial.print("[ERROR] Falló conexión MQTT, código: ");
      Serial.println(mqttClient.state());
      Serial.println("        Reintentando en 5 segundos...");
    }
  }
}


/**
 * Callback para mensajes MQTT entrantes
 * 
 * Se ejecuta automáticamente cuando llega un mensaje
 * a un topic al que estamos suscritos.
 * 
 * Valida que el mensaje sea para este dispositivo (verificando MAC en topic).
 * 
 * @param topic Topic del mensaje recibido
 * @param payload Contenido del mensaje (bytes)
 * @param length Longitud del payload
 */
void callbackMQTT(char* topic, byte* payload, unsigned int length) {
  Serial.print("\n>> Mensaje en: ");
  Serial.println(topic);
  
  // Copiar payload a buffer (evita String)
  if (length >= sizeof(messageBuffer)) {
    length = sizeof(messageBuffer) - 1;  // Prevenir overflow
  }
  memcpy(messageBuffer, payload, length);
  messageBuffer[length] = '\0';  // Null terminator
  
  Serial.print("   Contenido: ");
  Serial.println(messageBuffer);
  
  // Verificar que el mensaje es para este dispositivo
  // Comparamos directamente con el buffer estático
  if (strcmp(topic, topicLED) == 0) {
    
    // Procesar comandos del LED
    if (strcmp(messageBuffer, "ON") == 0 || strcmp(messageBuffer, "1") == 0) {
      digitalWrite(LED_AZUL, HIGH);
      Serial.println("   LED -> ON");
      
      // Confirmar cambio al backend
      mqttClient.publish(topicLEDEstado, "ON");
      
    } else if (strcmp(messageBuffer, "OFF") == 0 || strcmp(messageBuffer, "0") == 0) {
      digitalWrite(LED_AZUL, LOW);
      Serial.println("   LED -> OFF");
      
      // Confirmar cambio al backend
      mqttClient.publish(topicLEDEstado, "OFF");
    }
  } else {
    Serial.println("   [!] Mensaje ignorado (no es para este dispositivo)");
  }
}


/**
 * Envía heartbeat con la IP actual del dispositivo
 * 
 * El heartbeat indica al backend que el dispositivo está vivo.
 * Incluye la IP actual para que el backend la tenga actualizada
 * (útil en redes con DHCP donde la IP puede cambiar).
 * 
 * Topic: damiot/heartbeat/{MAC}
 * Payload: IP actual (ej: "192.168.8.130")
 */
void enviarHeartbeat() {
  // Convertir IP a string usando buffer estático
  IPAddress ip = WiFi.localIP();
  snprintf(ipBuffer, sizeof(ipBuffer), "%d.%d.%d.%d", ip[0], ip[1], ip[2], ip[3]);
  
  // Publicar heartbeat usando buffers estáticos (QoS 0, no crítico)
  if (mqttClient.publish(topicHeartbeat, ipBuffer)) {
    Serial.print("[HB] ");
    Serial.print(topicHeartbeat);
    Serial.print(" -> ");
    Serial.println(ipBuffer);
  } else {
    Serial.println("[ERROR] Heartbeat fallido");
  }
}


/**
 * Lee los sensores y publica los datos via MQTT
 * 
 * Lee temperatura y humedad del DHT11 y los publica
 * en sus respectivos topics MQTT con la MAC del dispositivo.
 * 
 * Topics: 
 *   - damiot/sensores/{MAC}/temperatura
 *   - damiot/sensores/{MAC}/humedad
 * 
 * QoS 0: Telemetría frecuente, no crítico si se pierde un dato
 */
void leerYPublicarSensor() {
  // Leer sensor DHT11
  humedad = dht.readHumidity();
  temperatura = dht.readTemperature();
  
  // Verificar lecturas válidas
  if (isnan(humedad) || isnan(temperatura)) {
    Serial.println("[ERROR] Lectura DHT11 fallida");
    return;
  }
  
  // Mostrar en consola
  Serial.println("\n--- Sensores ---");
  Serial.print("Temperatura: ");
  Serial.print(temperatura);
  Serial.println(" °C");
  Serial.print("Humedad: ");
  Serial.print(humedad);
  Serial.println(" %");
  
  // Publicar temperatura usando buffer estático
  dtostrf(temperatura, 1, 2, valueBuffer);
  if (mqttClient.publish(topicTemperatura, valueBuffer)) {
    Serial.print("   Publicado: ");
    Serial.println(topicTemperatura);
  }
  
  // Publicar humedad usando buffer estático
  dtostrf(humedad, 1, 2, valueBuffer);
  if (mqttClient.publish(topicHumedad, valueBuffer)) {
    Serial.print("   Publicado: ");
    Serial.println(topicHumedad);
  }
  
  Serial.println("----------------\n");
}
