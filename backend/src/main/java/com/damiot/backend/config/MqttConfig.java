package com.damiot.backend.config;

import com.damiot.backend.mqtt.MqttMessageHandler;
import com.damiot.backend.service.MqttService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuración del cliente MQTT con resiliencia
 * 
 * Características:
 * - Reconexión automática si falla la conexión inicial
 * - Reintento periódico si el broker no está disponible
 * - No crashea la aplicación si MQTT no está disponible
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
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

    private MqttClient mqttClient;
    private MqttService mqttService;
    private MqttMessageHandler messageHandler;
    private volatile boolean connectionAttempted = false;
    private volatile boolean lastConnectionFailed = false;

    @Bean
    public MqttClient mqttClient(MqttMessageHandler messageHandler, MqttService mqttService) {
        this.mqttService = mqttService;
        this.messageHandler = messageHandler;
        
        // Intentar conexión inicial (sin lanzar excepción si falla)
        tryConnect();
        
        return mqttClient;
    }

    /**
     * Intenta conectar al broker MQTT
     * No lanza excepciones, solo registra el error
     */
    private synchronized void tryConnect() {
        try {
            log.info("🔌 Configurando cliente MQTT...");
            log.info("   Broker URL: {}", brokerUrl);
            log.info("   Client ID: {}", clientId);

            // Crear cliente MQTT si no existe
            if (mqttClient == null) {
                mqttClient = new MqttClient(brokerUrl, clientId);
            }

            // Si ya está conectado, no hacer nada
            if (mqttClient.isConnected()) {
                return;
            }

            // Configurar opciones de conexión
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(5);
            options.setKeepAliveInterval(60);

            // Configurar usuario y contraseña si existen
            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
            }
            if (password != null && !password.isEmpty()) {
                options.setPassword(password.toCharArray());
            }

            // Establecer callback con manejo de reconexión
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (reconnect) {
                        log.info("🔄 Reconectado al broker MQTT: {}", serverURI);
                    } else {
                        log.info("✅ Conectado al broker MQTT: {}", serverURI);
                    }
                    // Re-suscribirse a los topics después de reconexión
                    subscribeToTopics();
                    mqttService.markConnectionRestored();
                    lastConnectionFailed = false;
                }

                @Override
                public void connectionLost(Throwable cause) {
                    if (!lastConnectionFailed) {
                        log.warn("⚠️ Conexión MQTT perdida: {}. Reintentando...", cause.getMessage());
                        lastConnectionFailed = true;
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Delegar al handler original
                    messageHandler.messageArrived(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Mensaje entregado
                }
            });

            // Conectar al broker
            log.info("🔗 Conectando al broker MQTT...");
            mqttClient.connect(options);
            log.info("✅ Conectado exitosamente al broker MQTT");

            // Suscribirse a los topics
            subscribeToTopics();

            // Inyectar cliente en MqttService
            mqttService.setMqttClient(mqttClient);
            connectionAttempted = true;
            lastConnectionFailed = false;

        } catch (MqttException e) {
            if (!lastConnectionFailed) {
                log.error("❌ Error al conectar al broker MQTT: {} (código: {})", 
                        e.getMessage(), e.getReasonCode());
                log.warn("⚠️ El backend continuará funcionando sin MQTT. Se reintentará periódicamente.");
                lastConnectionFailed = true;
            }
            connectionAttempted = true;
            
            // Aunque falle, inyectar el cliente (puede reconectar automáticamente)
            if (mqttClient != null) {
                mqttService.setMqttClient(mqttClient);
            }
        } catch (Exception e) {
            if (!lastConnectionFailed) {
                log.error("❌ Error inesperado al configurar MQTT: {}", e.getMessage());
                lastConnectionFailed = true;
            }
            connectionAttempted = true;
        }
    }

    /**
     * Suscribirse a los topics configurados
     */
    private void subscribeToTopics() {
        if (mqttClient == null || !mqttClient.isConnected()) {
            return;
        }

        try {
            String[] topics = subscribeTopics.split(",");
            for (String topic : topics) {
                topic = topic.trim();
                if (!topic.isEmpty()) {
                    mqttClient.subscribe(topic, 1);
                    log.info("📥 Suscrito al topic: {}", topic);
                }
            }
        } catch (MqttException e) {
            log.error("Error al suscribirse a topics: {}", e.getMessage());
        }
    }

    /**
     * Tarea programada para reintentar conexión si está desconectado
     * Se ejecuta cada 30 segundos
     */
    @Scheduled(fixedRate = 30000, initialDelay = 30000)
    public void checkAndReconnect() {
        // Si no hay cliente, intentar crear uno
        if (mqttClient == null) {
            tryConnect();
            return;
        }

        // Si está conectado, todo bien
        if (mqttClient.isConnected()) {
            if (lastConnectionFailed) {
                log.info("✅ Conexión MQTT restaurada");
                lastConnectionFailed = false;
            }
            return;
        }

        // Si no está conectado y la reconexión automática no funcionó, reintentar
        log.debug("🔄 Verificando conexión MQTT...");
        tryConnect();
    }

    /**
     * Cerrar conexión MQTT al apagar la aplicación
     */
    @PreDestroy
    public void cleanup() {
        if (mqttClient != null) {
            try {
                if (mqttClient.isConnected()) {
                    mqttClient.disconnect();
                    log.info("🔌 Desconectado del broker MQTT");
                }
                mqttClient.close();
            } catch (MqttException e) {
                log.warn("Error al cerrar cliente MQTT: {}", e.getMessage());
            }
        }
    }
}
