/*
 * Prueba DHT11 - Lectura de Sensor
 * Proyecto IoT - IES Azarquiel
 * 
 * Lee temperatura y humedad del DHT11
 * Muestra los datos en el Monitor Serie
 */

#include <DHT.h>

// Definición de pines
#define DHTPIN 4        // Pin de datos del DHT11
#define DHTTYPE DHT11   // Tipo de sensor

// Crear objeto DHT
DHT dht(DHTPIN, DHTTYPE);

void setup() {
  // Inicializar comunicación serie
  Serial.begin(115200);
  delay(2000);
  
  Serial.println("=================================");
  Serial.println("  Prueba DHT11 - ESP32");
  Serial.println("  Proyecto IoT Multiplataforma");
  Serial.println("=================================");
  Serial.println();
  
  // Inicializar sensor DHT11
  dht.begin();
  
  Serial.println("Sensor DHT11 inicializado");
  Serial.println("Pin de datos: GPIO 4");
  Serial.println();
  Serial.println("Iniciando lecturas cada 2 segundos...");
  Serial.println("=================================");
  Serial.println();
  
  delay(2000); // Esperar a que el sensor se estabilice
}

void loop() {
  // Leer humedad
  float humedad = dht.readHumidity();
  
  // Leer temperatura en Celsius
  float temperatura = dht.readTemperature();
  
  // Verificar si las lecturas son válidas
  if (isnan(humedad) || isnan(temperatura)) {
    Serial.println("❌ Error al leer del sensor DHT11");
    Serial.println("   Verifica las conexiones:");
    Serial.println("   - DHT11 S   --> GPIO 4");
    Serial.println("   - DHT11 +   --> 3.3V");
    Serial.println("   - DHT11 -   --> GND");
    Serial.println();
    delay(2000);
    return;
  }
  
  // Mostrar lecturas
  Serial.println("--- Lectura del sensor ---");
  Serial.print("🌡️  Temperatura: ");
  Serial.print(temperatura);
  Serial.println(" °C");
  
  Serial.print("💧 Humedad: ");
  Serial.print(humedad);
  Serial.println(" %");
  
  Serial.println();
  
  // Esperar 2 segundos antes de la siguiente lectura
  delay(2000);
}