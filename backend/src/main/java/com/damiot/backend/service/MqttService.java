package com.damiot.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttService {

    private MqttClient mqttClient;

    /**
     * Publicar un mensaje a un topic MQTT
     */
    public void publish(String topic, String payload) {
        publish(topic, payload, 1, false);
    }

    /**
     * Publicar un mensaje con QoS y retain personalizados
     */
    public void publish(String topic, String payload, int qos, boolean retained) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(qos);
                message.setRetained(retained);

                mqttClient.publish(topic, message);
                log.info("Mensaje publicado a {}: {}", topic, payload);
            } else {
                log.error("Cliente MQTT no conectado. No se puede publicar a: {}", topic);
            }
        } catch (MqttException e) {
            log.error("Error al publicar mensaje MQTT a {}: {}", topic, e.getMessage());
        }
    }

    /**
     * Enviar comando a un actuador
     */
    public void sendActuatorCommand(String actuatorType, String command) {
        String topic = "damiot/actuators/" + actuatorType + "/command";
        publish(topic, command, 1, false);
        log.info("Comando enviado al actuador {}: {}", actuatorType, command);
    }

    /**
     * Enviar comando LED
     */
    public void sendLedCommand(String command) {
        sendActuatorCommand("led", command);
    }

    /**
     * Enviar comando MOTOR
     */
    public void sendMotorCommand(String command) {
        sendActuatorCommand("motor", command);
    }

    /**
     * Solicitar estado del dispositivo
     */
    public void requestDeviceStatus(String deviceId) {
        String topic = "damiot/device/status/request";
        publish(topic, deviceId, 1, false);
        log.info("Solicitando estado del dispositivo: {}", deviceId);
    }

    /**
     * Establecer el cliente MQTT (llamado desde MqttConfig)
     */
    public void setMqttClient(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
        log.info("Cliente MQTT configurado en MqttService");
    }

    /**
     * Verificar si el cliente está conectado
     */
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    /**
     * Obtener información del cliente
     */
    public String getClientInfo() {
        if (mqttClient != null) {
            return "Cliente: " + mqttClient.getClientId() +
                    " - Conectado: " + mqttClient.isConnected();
        }
        return "Cliente MQTT no inicializado";
    }
}
