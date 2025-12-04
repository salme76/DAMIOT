package com.damiot.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

/**
 * Servicio para comunicación MQTT con dispositivos ESP32
 * 
 * Proporciona métodos para publicar mensajes a topics MQTT
 * y enviar comandos a actuadores.
 * 
 * Topics en español con MAC del dispositivo para soporte multi-dispositivo.
 * Formato: damiot/actuadores/{MAC}/{actuator_type}
 * 
 * Resiliencia: Maneja errores de conexión sin crashear la aplicación
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@Service
@Slf4j
public class MqttService {

    private MqttClient mqttClient;
    private volatile boolean connectionLost = false;
    private String lastError = null;

    /**
     * Publicar un mensaje a un topic MQTT
     * Usa QoS 1 por defecto (al menos una entrega)
     */
    public void publish(String topic, String payload) {
        publish(topic, payload, 1, false);
    }

    /**
     * Publicar un mensaje con QoS y retain personalizados
     * 
     * @param topic Topic MQTT
     * @param payload Contenido del mensaje
     * @param qos Calidad de servicio (0, 1 o 2)
     * @param retained Si el broker debe retener el mensaje
     */
    public void publish(String topic, String payload, int qos, boolean retained) {
        if (!isConnected()) {
            log.warn("⚠️ MQTT no conectado. Mensaje no enviado a: {} (payload: {})", topic, payload);
            lastError = "Cliente MQTT no conectado";
            return;
        }

        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(qos);
            message.setRetained(retained);

            mqttClient.publish(topic, message);
            log.info("📤 MQTT publicado a {}: {}", topic, payload);
            lastError = null; // Limpiar error previo si éxito
            
        } catch (MqttException e) {
            lastError = e.getMessage();
            log.error("❌ Error al publicar MQTT a {}: {} (código: {})", 
                    topic, e.getMessage(), e.getReasonCode());
            
            // Si la conexión se perdió, marcarla
            if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_NOT_CONNECTED ||
                e.getReasonCode() == MqttException.REASON_CODE_CONNECTION_LOST) {
                connectionLost = true;
                log.warn("🔌 Conexión MQTT perdida. Se intentará reconectar automáticamente.");
            }
        } catch (Exception e) {
            lastError = e.getMessage();
            log.error("❌ Error inesperado al publicar MQTT: {}", e.getMessage());
        }
    }

    /**
     * Enviar comando a un actuador con identificación por MAC
     * 
     * Topic resultante: damiot/actuadores/{macAddress}/{actuatorType}
     * Ejemplo: damiot/actuadores/7C:9E:BD:F1:DA:E4/led_azul
     * 
     * @param macAddress MAC del dispositivo ESP32
     * @param actuatorType Tipo de actuador (led_azul, bomba_riego, etc.)
     * @param command Comando a enviar (ON, OFF, etc.)
     */
    public void sendActuatorCommand(String macAddress, String actuatorType, String command) {
        // Construir topic con MAC: damiot/actuadores/{MAC}/{actuator_type}
        String topic = "damiot/actuadores/" + macAddress + "/" + actuatorType;
        
        publish(topic, command, 1, false);
        log.info("🎮 Comando enviado al actuador {} del dispositivo {}: {} (topic: {})", 
                actuatorType, macAddress, command, topic);
    }

    /**
     * Enviar comando LED con MAC (método de conveniencia)
     * El ESP32 escucha en: damiot/actuadores/{MAC}/led_azul
     * 
     * @param macAddress MAC del dispositivo
     * @param command ON o OFF
     */
    public void sendLedCommand(String macAddress, String command) {
        sendActuatorCommand(macAddress, "led_azul", command);
        log.info("💡 Comando LED enviado al dispositivo {}: {}", macAddress, command);
    }

    /**
     * Enviar comando MOTOR con MAC (método de conveniencia)
     * 
     * @param macAddress MAC del dispositivo
     * @param command ON o OFF
     */
    public void sendMotorCommand(String macAddress, String command) {
        sendActuatorCommand(macAddress, "motor", command);
        log.info("⚙️ Comando motor enviado al dispositivo {}: {}", macAddress, command);
    }

    /**
     * Solicitar estado del dispositivo via MQTT
     */
    public void requestDeviceStatus(String deviceId) {
        String topic = "damiot/dispositivo/solicitud";
        publish(topic, deviceId, 1, false);
        log.info("📡 Solicitando estado del dispositivo: {}", deviceId);
    }

    /**
     * Establecer el cliente MQTT (llamado desde MqttConfig)
     */
    public void setMqttClient(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
        this.connectionLost = false;
        this.lastError = null;
        log.info("✅ Cliente MQTT configurado en MqttService");
    }

    /**
     * Verificar si el cliente está conectado
     */
    public boolean isConnected() {
        try {
            return mqttClient != null && mqttClient.isConnected();
        } catch (Exception e) {
            log.warn("Error al verificar conexión MQTT: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtener información del cliente para diagnóstico
     */
    public String getClientInfo() {
        try {
            if (mqttClient != null) {
                StringBuilder info = new StringBuilder();
                info.append("Cliente: ").append(mqttClient.getClientId());
                info.append(" - Conectado: ").append(mqttClient.isConnected());
                if (lastError != null) {
                    info.append(" - Último error: ").append(lastError);
                }
                return info.toString();
            }
            return "Cliente MQTT no inicializado";
        } catch (Exception e) {
            return "Error al obtener info: " + e.getMessage();
        }
    }

    /**
     * Marcar la conexión como recuperada
     * Llamado desde MqttConfig cuando la reconexión tiene éxito
     */
    public void markConnectionRestored() {
        this.connectionLost = false;
        this.lastError = null;
        log.info("✅ Conexión MQTT restaurada");
    }

    /**
     * Verificar si hubo pérdida de conexión reciente
     */
    public boolean wasConnectionLost() {
        return connectionLost;
    }

    /**
     * Obtener el último error
     */
    public String getLastError() {
        return lastError;
    }
}
