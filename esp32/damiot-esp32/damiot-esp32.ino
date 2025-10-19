/*
 * DAMIOT - Sistema IoT Multiplataforma
 * ESP32 con DHT11 y control LED via MQTT
 * 
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */

#include <WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>
#include "config.h"

// Clientes WiFi y MQTT
WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

// Sensor DHT11
DHT dht(DHTPIN, DHTTYPE);

// Variables de tiempo
unsigned long ultimaLectura = 0;

// Variables para almacenar lecturas
float temperatura = 0.0;
float humedad = 0.0;

// ===== SETUP =====
void setup() {
  // Inicializar comunicación serie
  Serial.begin(115200);
  delay(1000);
  
  Serial.println("\n\n=================================");
  Serial.println("    DAMIOT - Sistema IoT");
  Serial.println("    IES Azarquiel - Toledo");
  Serial.println("=================================\n");
  
  // Configurar LED
  pinMode(LED_AZUL, OUTPUT);
  digitalWrite(LED_AZUL, LOW);
  Serial.println("✓ LED configurado");
  
  // Inicializar sensor DHT11
  dht.begin();
  Serial.println("✓ Sensor DHT11 inicializado");
  
  // Conectar a WiFi
  conectarWiFi();
  
  // Configurar servidor MQTT
  mqttClient.setServer(MQTT_SERVER, MQTT_PORT);
  mqttClient.setCallback(callbackMQTT);
  
  Serial.println("\n=================================");
  Serial.println("Sistema iniciado correctamente");
  Serial.println("=================================\n");
}

// ===== LOOP PRINCIPAL =====
void loop() {
  // Verificar conexión WiFi
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("⚠️  WiFi desconectado. Reconectando...");
    conectarWiFi();
  }
  
  // Verificar conexión MQTT
  if (!mqttClient.connected()) {
    reconectarMQTT();
  }
  
  // Procesar mensajes MQTT
  mqttClient.loop();
  
  // Leer sensor y publicar datos
  if (millis() - ultimaLectura >= INTERVALO_LECTURA) {
    leerYPublicarSensor();
    ultimaLectura = millis();
  }
}

// ===== FUNCIÓN: CONECTAR WIFI =====
void conectarWiFi() {
  Serial.print("Conectando a WiFi: ");
  Serial.print(WIFI_SSID);
  Serial.print(" ");
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  
  int intentos = 0;
  while (WiFi.status() != WL_CONNECTED && intentos < 20) {
    delay(500);
    Serial.print(".");
    intentos++;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n✓ WiFi conectado!");
    Serial.print("  IP asignada: ");
    Serial.println(WiFi.localIP());
  } else {
    Serial.println("\n✗ Error al conectar WiFi");
    Serial.println("  Reiniciando en 5 segundos...");
    delay(5000);
    ESP.restart();
  }
}

// ===== FUNCIÓN: RECONECTAR MQTT =====
void reconectarMQTT() {
  while (!mqttClient.connected()) {
    Serial.print("Conectando a broker MQTT: ");
    Serial.print(MQTT_SERVER);
    Serial.print(":");
    Serial.print(MQTT_PORT);
    Serial.print(" ");
    
    if (mqttClient.connect(MQTT_CLIENT_ID)) {
      Serial.println("\n✓ MQTT conectado!");
      
      // Suscribirse al topic del LED con QoS 1
      // QoS 1 garantiza entrega de comandos críticos
      // Los duplicados no son problema (operación idempotente)
      mqttClient.subscribe(TOPIC_LED, 1);
      Serial.print("  Suscrito a: ");
      Serial.print(TOPIC_LED);
      Serial.println(" (QoS 1)");
      
      // Publicar mensaje de estado
      mqttClient.publish(TOPIC_STATUS, "online");
      Serial.println("  Estado publicado: online");
      
    } else {
      Serial.print("\n✗ Error de conexión MQTT. Código: ");
      Serial.println(mqttClient.state());
      Serial.println("  Reintentando en 5 segundos...");
      delay(5000);
    }
  }
}

// ===== FUNCIÓN: CALLBACK MQTT (recibir mensajes) =====
void callbackMQTT(char* topic, byte* payload, unsigned int length) {
  Serial.print("\n📩 Mensaje recibido en topic: ");
  Serial.println(topic);
  
  // Convertir payload a String
  String mensaje = "";
  for (int i = 0; i < length; i++) {
    mensaje += (char)payload[i];
  }
  
  Serial.print("   Contenido: ");
  Serial.println(mensaje);
  
  // Control del LED (operación idempotente)
  // Encender un LED ya encendido no tiene efectos secundarios
  // Por eso QoS 1 es perfecto: garantiza entrega sin problemas de duplicados
  if (String(topic) == TOPIC_LED) {
    if (mensaje == "ON" || mensaje == "1") {
      digitalWrite(LED_AZUL, HIGH);
      Serial.println("   🔵 LED encendido");
    } 
    else if (mensaje == "OFF" || mensaje == "0") {
      digitalWrite(LED_AZUL, LOW);
      Serial.println("   ⚫ LED apagado");
    }
  }
}

// ===== FUNCIÓN: LEER SENSOR Y PUBLICAR =====
void leerYPublicarSensor() {
  // Leer sensor DHT11
  humedad = dht.readHumidity();
  temperatura = dht.readTemperature();
  
  // Verificar si las lecturas son válidas
  if (isnan(humedad) || isnan(temperatura)) {
    Serial.println("❌ Error al leer sensor DHT11");
    return;
  }
  
  // Mostrar lecturas
  Serial.println("\n--- Lectura del sensor ---");
  Serial.print("🌡️  Temperatura: ");
  Serial.print(temperatura);
  Serial.println(" °C");
  
  Serial.print("💧 Humedad: ");
  Serial.print(humedad);
  Serial.println(" %");
  
  // Publicar temperatura (QoS 0: alta frecuencia, no crítico)
  char tempString[8];
  dtostrf(temperatura, 1, 2, tempString);
  if (mqttClient.publish(TOPIC_TEMPERATURA, tempString)) {
    Serial.print("   ✓ Temperatura publicada en: ");
    Serial.print(TOPIC_TEMPERATURA);
    Serial.println(" (QoS 0)");
  }
  
  // Publicar humedad (QoS 0: alta frecuencia, no crítico)
  char humString[8];
  dtostrf(humedad, 1, 2, humString);
  if (mqttClient.publish(TOPIC_HUMEDAD, humString)) {
    Serial.print("   ✓ Humedad publicada en: ");
    Serial.print(TOPIC_HUMEDAD);
    Serial.println(" (QoS 0)");
  }
  
  Serial.println();
}
