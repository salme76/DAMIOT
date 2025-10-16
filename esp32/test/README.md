# Tests de Hardware ESP32

Códigos de prueba para validar cada componente del proyecto IoT de forma individual.

------

## 🔵 Test LED Azul

### Requisitos previos

- Circuito montado en breadboard según esquema de conexiones
- Arduino IDE configurado con soporte ESP32
- Driver CP2102 instalado

### Pasos

1. Abre `test-led/test-led.ino` en Arduino IDE
2. Selecciona **Placa:** ESP32 Dev Module
3. Selecciona **Puerto:** COM3 (o el que corresponda)
4. Haz clic en **Subir** (→)
5. Abre el **Monitor Serie** (115200 baudios)

### Resultado esperado

El LED azul debe parpadear cada segundo (1s encendido, 1s apagado).

En el Monitor Serie verás:

```
LED azul: ON
LED azul: OFF
--- Ciclo completado ---
```

------

## 🌡️ Test Sensor DHT11

### Requisitos previos

- Circuito montado en breadboard según esquema de conexiones
- Librerías instaladas: `DHT sensor library` y `Adafruit Unified Sensor`

### Pasos

1. Abre `test-dht11/test-dht11.ino` en Arduino IDE
2. Selecciona **Placa:** ESP32 Dev Module
3. Selecciona **Puerto:** COM3 (o el que corresponda)
4. Haz clic en **Subir** (→)
5. Abre el **Monitor Serie** (115200 baudios)

### Resultado esperado

Cada 2 segundos verás lecturas de temperatura y humedad:

```
--- Lectura del sensor ---
🌡️  Temperatura: 23.5 °C
💧 Humedad: 45 %
```

**Prueba:** Acerca tu dedo al sensor, la temperatura debe subir.