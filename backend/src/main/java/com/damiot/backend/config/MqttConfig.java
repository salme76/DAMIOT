package com.damiot.backend.config;

import com.damiot.backend.mqtt.MqttMessageHandler;
import com.damiot.backend.service.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MqttConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.username:}")
    private String username;

    @Value("${mqtt.password:}")
    private String password;

    @Value("${mqtt.topics.subscribe}")
    private String subscribeTopics;

    @Bean
    public MqttClient mqttClient(MqttMessageHandler messageHandler, MqttService mqttService) {
        try {
            log.info("Configurando cliente MQTT...");
            log.info("Broker URL: {}", brokerUrl);
            log.info("Client ID: {}", clientId);

            // Crear cliente MQTT
            MqttClient client = new MqttClient(brokerUrl, clientId);

            // Configurar opciones de conexión
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(60);

            // Configurar usuario y contraseña si existen
            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
                log.info("Usuario MQTT configurado: {}", username);
            }
            if (password != null && !password.isEmpty()) {
                options.setPassword(password.toCharArray());
                log.info("Contraseña MQTT configurada");
            }

            // Establecer callback para mensajes
            client.setCallback(messageHandler);

            // Conectar al broker
            log.info("Conectando al broker MQTT...");
            client.connect(options);
            log.info("✅ Conectado exitosamente al broker MQTT");

            // Suscribirse a los topics configurados
            String[] topics = subscribeTopics.split(",");
            for (String topic : topics) {
                topic = topic.trim();
                client.subscribe(topic, 1);
                log.info("✅ Suscrito al topic: {}", topic);
            }

            // Inyectar cliente en MqttService
            mqttService.setMqttClient(client);

            return client;

        } catch (MqttException e) {
            log.error("❌ Error al configurar cliente MQTT: {}", e.getMessage());
            log.error("Razón: {}", e.getReasonCode());
            throw new RuntimeException("No se pudo conectar al broker MQTT", e);
        }
    }
}
