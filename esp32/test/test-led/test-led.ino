/*
 * Prueba básica LED ESP32
 * Proyecto IoT - IES Azarquiel
 * 
 * Este código hace parpadear el LED azul externo (GPIO 5)
 * Nota: La placa no tiene LED interno soldado
 */

// Definición de pines
#define LED_AZUL 5      // LED externo azul

void setup() {
  // Inicializar comunicación serie
  Serial.begin(115200);
  delay(1000);
  
  Serial.println("=================================");
  Serial.println("  Prueba de LED Azul - ESP32");
  Serial.println("=================================");
  
  // Configurar pin como salida
  pinMode(LED_AZUL, OUTPUT);
  
  // Apagar LED al inicio
  digitalWrite(LED_AZUL, LOW);
  
  Serial.println("LED azul configurado correctamente en GPIO 5");
  Serial.println("Iniciando secuencia de parpadeo...");
}

void loop() {
  // Encender LED azul
  Serial.println("LED azul: ON");
  digitalWrite(LED_AZUL, HIGH);
  delay(1000);
  
  // Apagar LED azul
  Serial.println("LED azul: OFF");
  digitalWrite(LED_AZUL, LOW);
  delay(1000);
  
  Serial.println("--- Ciclo completado ---");
  Serial.println();
}