package com.damiot.backend.mqtt;

import com.damiot.backend.service.ActuatorService;
import com.damiot.backend.service.DeviceService;
import com.damiot.backend.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttMessageHandler implements MqttCallback {

    private final SensorService sensorService;
    private final ActuatorService actuatorService;
    private final DeviceService deviceService;

    @Override
    public void connectionLost(Throwable cause) {
        log.error("❌ Conexión MQTT perdida: {}", cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        log.info("📨 Mensaje MQTT recibido - Topic: {} | Payload: {}", topic, payload);

        try {
            // Procesar según el topic
            if (topic.startsWith("damiot/sensors/")) {
                handleSensorMessage(topic, payload);
            } else if (topic.startsWith("damiot/actuators/")) {
                handleActuatorMessage(topic, payload);
            } else if (topic.startsWith("damiot/device/status")) {
                handleDeviceStatusMessage(topic, payload);
            } else {
                log.warn("Topic no reconocido: {}", topic);
            }
        } catch (Exception e) {
            log.error("Error procesando mensaje MQTT: {}", e.getMessage(), e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("Entrega MQTT completada: {}", token.getMessageId());
    }

    /**
     * Procesar mensajes de sensores
     * Formato esperado: {"value": 25.5, "unit": "°C"}
     * O simplemente: 25.5
     */
    private void handleSensorMessage(String topic, String payload) {
        try {
            // Extraer tipo de sensor del topic: damiot/sensors/temperature -> temperature
            String sensorType = topic.replace("damiot/sensors/", "");

            // Parsear valor
            Double value;
            String unit = "";

            // Si el payload es JSON
            if (payload.trim().startsWith("{")) {
                // Parseo simple de JSON (puedes mejorar con Jackson si lo necesitas)
                value = parseJsonValue(payload);
                unit = parseJsonUnit(payload);
            } else {
                // Si es solo un número
                value = Double.parseDouble(payload.trim());

                // Asignar unidad según tipo de sensor
                unit = switch (sensorType.toLowerCase()) {
                    case "temperature" -> "°C";
                    case "humidity" -> "%";
                    default -> "";
                };
            }

            // Guardar en base de datos
            String deviceId = "ESP32-001"; // Puedes extraerlo del topic o payload si lo envías
            sensorService.saveSensorReading(sensorType, value, unit, deviceId);

            log.info("✅ Lectura de sensor guardada: {} = {} {}", sensorType, value, unit);

        } catch (Exception e) {
            log.error("Error procesando mensaje de sensor: {}", e.getMessage());
        }
    }

    /**
     * Procesar mensajes de actuadores (respuestas del ESP32)
     * Formato: "OK" o "EXECUTED" o "ERROR"
     */
    private void handleActuatorMessage(String topic, String payload) {
        try {
            // Extraer tipo de actuador: damiot/actuators/led/status -> led
            String[] parts = topic.split("/");
            if (parts.length >= 3) {
                String actuatorType = parts[2];

                log.info("✅ Estado de actuador recibido: {} -> {}", actuatorType, payload);

                // Puedes actualizar el estado en la BD si guardaste el eventId
                // actuatorService.updateEventStatus(eventId, "EXECUTED", payload);
            }
        } catch (Exception e) {
            log.error("Error procesando mensaje de actuador: {}", e.getMessage());
        }
    }

    /**
     * Procesar mensajes de estado del dispositivo
     * Formato: "ONLINE" o "OFFLINE" o JSON con info del dispositivo
     */
    private void handleDeviceStatusMessage(String topic, String payload) {
        try {
            String deviceId = "ESP32-001"; // Extraer del payload si lo envías

            if (payload.equalsIgnoreCase("ONLINE")) {
                deviceService.markDeviceOnline(deviceId, "192.168.8.x");
                log.info("✅ Dispositivo {} marcado como ONLINE", deviceId);
            } else if (payload.equalsIgnoreCase("OFFLINE")) {
                deviceService.markDeviceOffline(deviceId);
                log.info("⚠️ Dispositivo {} marcado como OFFLINE", deviceId);
            } else {
                // Heartbeat o actualización de estado
                deviceService.updateHeartbeat(deviceId);
                log.debug("💓 Heartbeat recibido de {}", deviceId);
            }
        } catch (Exception e) {
            log.error("Error procesando estado de dispositivo: {}", e.getMessage());
        }
    }

    /**
     * Parseo simple de JSON para extraer "value"
     */
    private Double parseJsonValue(String json) {
        try {
            String valueStr = json.replaceAll(".*\"value\"\\s*:\\s*([0-9.]+).*", "$1");
            return Double.parseDouble(valueStr);
        } catch (Exception e) {
            log.error("Error parseando valor JSON: {}", json);
            return 0.0;
        }
    }

    /**
     * Parseo simple de JSON para extraer "unit"
     */
    private String parseJsonUnit(String json) {
        try {
            if (json.contains("\"unit\"")) {
                return json.replaceAll(".*\"unit\"\\s*:\\s*\"([^\"]+)\".*", "$1");
            }
        } catch (Exception e) {
            log.error("Error parseando unidad JSON: {}", json);
        }
        return "";
    }
}
