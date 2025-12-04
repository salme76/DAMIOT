package com.damiot.backend.mqtt;

import com.damiot.backend.model.ActuatorState;
import com.damiot.backend.model.Device;
import com.damiot.backend.service.ActuatorService;
import com.damiot.backend.service.DeviceService;
import com.damiot.backend.service.MqttService;
import com.damiot.backend.service.SensorService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Handler para procesar mensajes MQTT entrantes del ESP32
 * 
 * Topics procesados (en español con MAC del dispositivo):
 * - damiot/sensores/{MAC}/temperatura - Lecturas de temperatura
 * - damiot/sensores/{MAC}/humedad - Lecturas de humedad
 * - damiot/actuadores/{MAC}/led_azul/estado - Confirmación de estado LED
 * - damiot/heartbeat/{MAC} - Heartbeat de dispositivos (IP o "offline" para LWT)
 * - damiot/dispositivo/estado - Estado del dispositivo (ONLINE/OFFLINE)
 * 
 * Soporte multi-dispositivo: Extrae la MAC del topic para identificar
 * el dispositivo y obtener su device_id de la base de datos.
 * 
 * Resiliencia: Maneja errores sin crashear
 * 
 * @author Emilio José Salmerón Arjona
 * IES Azarquiel - Toledo
 * CFGS Desarrollo de Aplicaciones Multiplataforma
 * Curso 2025/2026
 */
@Component
@Slf4j
public class MqttMessageHandler implements MqttCallback {

    private final SensorService sensorService;
    private final ActuatorService actuatorService;
    private final DeviceService deviceService;
    private final MqttService mqttService;

    // Constructor con @Lazy para MqttService (evita dependencia circular)
    public MqttMessageHandler(
            SensorService sensorService,
            ActuatorService actuatorService,
            DeviceService deviceService,
            @Lazy MqttService mqttService) {
        this.sensorService = sensorService;
        this.actuatorService = actuatorService;
        this.deviceService = deviceService;
        this.mqttService = mqttService;
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.error("❌ Conexión MQTT perdida: {}", cause.getMessage());
        // La reconexión se maneja automáticamente en MqttConfig
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload()).trim();
        log.debug("📨 MQTT recibido - Topic: {} | Payload: {}", topic, payload);

        try {
            // Procesar según el topic (en español)
            if (topic.startsWith("damiot/sensores/")) {
                handleSensorMessage(topic, payload);
            } else if (topic.startsWith("damiot/actuadores/") && topic.endsWith("/estado")) {
                handleActuatorStatusMessage(topic, payload);
            } else if (topic.startsWith("damiot/heartbeat/")) {
                handleHeartbeat(topic, payload);
            } else if (topic.equals("damiot/dispositivo/estado")) {
                handleDeviceStatus(topic, payload);
            } else {
                log.debug("Topic no manejado específicamente: {}", topic);
            }
        } catch (Exception e) {
            log.error("Error procesando mensaje MQTT [{}]: {}", topic, e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("📤 Entrega MQTT completada: {}", token.getMessageId());
    }

    /**
     * Procesar mensajes de sensores del ESP32
     * 
     * Topics esperados (en español con MAC):
     * - damiot/sensores/7C:9E:BD:F1:DA:E4/temperatura con payload: "25.50"
     * - damiot/sensores/7C:9E:BD:F1:DA:E4/humedad con payload: "65.00"
     * 
     * El ESP32 envía valores como strings numéricos simples.
     * Formato del topic: damiot/sensores/{MAC}/{sensor_type}
     */
    private void handleSensorMessage(String topic, String payload) {
        try {
            // Extraer MAC y tipo de sensor del topic
            // Formato: damiot/sensores/7C:9E:BD:F1:DA:E4/temperatura
            String[] parts = topic.split("/");
            if (parts.length < 4) {
                log.warn("Formato de topic de sensor inválido: {}", topic);
                return;
            }
            
            String macAddress = parts[2];  // La MAC está en la posición 2
            String sensorType = parts[3];  // El tipo de sensor en la posición 3
            
            // Validar formato de MAC
            if (!isValidMacAddress(macAddress)) {
                log.warn("MAC address inválida en topic de sensor: {}", macAddress);
                return;
            }
            
            // Buscar dispositivo por MAC
            Optional<Device> deviceOpt = deviceService.getDeviceByMacAddress(macAddress);
            if (deviceOpt.isEmpty()) {
                log.warn("Dispositivo no encontrado con MAC: {}", macAddress);
                return;
            }
            
            Device device = deviceOpt.get();
            Long deviceId = device.getId();

            // El ESP32 envía solo el valor numérico
            Double value = Double.parseDouble(payload);

            // Asignar unidad según tipo de sensor (en español)
            String unit = switch (sensorType.toLowerCase()) {
                case "temperatura" -> "°C";
                case "humedad" -> "%";
                case "humedad_suelo", "higrómetro_suelo" -> "%";
                default -> "";
            };

            // Guardar en base de datos
            sensorService.saveSensorReading(sensorType, value, unit, deviceId);

            log.info("📊 Sensor guardado: {} = {} {} (Device: {}, MAC: {})", 
                    sensorType, value, unit, deviceId, macAddress);

        } catch (NumberFormatException e) {
            log.warn("Valor de sensor no numérico: {} (topic: {})", payload, topic);
        } catch (Exception e) {
            log.error("Error procesando sensor [{}]: {}", topic, e.getMessage());
        }
    }

    /**
     * Procesar confirmaciones de estado de actuadores
     * 
     * Topic esperado: damiot/actuadores/7C:9E:BD:F1:DA:E4/led_azul/estado
     * Payload: "ON" o "OFF"
     * 
     * El ESP32 confirma el cambio de estado enviando este mensaje.
     * Formato: damiot/actuadores/{MAC}/{actuator_type}/estado
     */
    private void handleActuatorStatusMessage(String topic, String payload) {
        try {
            // Extraer MAC y tipo de actuador del topic
            // Formato: damiot/actuadores/7C:9E:BD:F1:DA:E4/led_azul/estado
            String[] parts = topic.split("/");
            if (parts.length < 5) {
                log.warn("Formato de topic de actuador inválido: {}", topic);
                return;
            }
            
            String macAddress = parts[2];     // MAC en posición 2
            String actuatorType = parts[3];   // Tipo en posición 3
            // parts[4] es "estado"
            
            // Validar MAC
            if (!isValidMacAddress(macAddress)) {
                log.warn("MAC address inválida en topic de actuador: {}", macAddress);
                return;
            }
            
            // Buscar dispositivo por MAC
            Optional<Device> deviceOpt = deviceService.getDeviceByMacAddress(macAddress);
            if (deviceOpt.isEmpty()) {
                log.warn("Dispositivo no encontrado con MAC: {}", macAddress);
                return;
            }
            
            Device device = deviceOpt.get();
            Long deviceId = device.getId();
            String state = payload.toUpperCase(); // "ON" o "OFF"

            // Actualizar estado en BD
            actuatorService.updateActuatorState(deviceId, actuatorType, state);

            log.info("💡 Actuador confirmado: {} -> {} (Device: {}, MAC: {})", 
                    actuatorType, state, deviceId, macAddress);
                    
        } catch (Exception e) {
            log.error("Error procesando estado de actuador [{}]: {}", topic, e.getMessage());
        }
    }

    /**
     * Procesar heartbeat de dispositivos
     * 
     * Topic esperado: damiot/heartbeat/7C:9E:BD:F1:DA:E4
     * Payload: 
     *   - IP (ej: "192.168.8.130") para heartbeat normal
     *   - "offline" para LWT (Last Will & Testament) cuando el ESP32 se desconecta
     * 
     * Cuando el ESP32 vuelve online, se sincronizan los estados de actuadores.
     */
    private void handleHeartbeat(String topic, String payload) {
        try {
            // Extraer MAC del topic: damiot/heartbeat/7C:9E:BD:F1:DA:E4
            String macAddress = topic.replace("damiot/heartbeat/", "");
            
            // Validar formato MAC
            if (!isValidMacAddress(macAddress)) {
                log.warn("MAC address inválida en heartbeat: {}", macAddress);
                return;
            }
            
            // Verificar si es mensaje LWT (offline) o heartbeat normal (IP)
            if (payload.equalsIgnoreCase("offline")) {
                // LWT recibido: el ESP32 se desconectó abruptamente
                log.warn("📴 LWT recibido - Dispositivo desconectado: {}", macAddress);
                deviceService.markDeviceOfflineByMac(macAddress);
            } else {
                // Heartbeat normal con IP
                // Verificar si el dispositivo estaba offline (para sincronizar estados)
                boolean wasOffline = deviceService.isDeviceOfflineByMac(macAddress);
                
                // Actualizar estado a online
                deviceService.updateDeviceHeartbeat(macAddress, payload);
                log.debug("💓 Heartbeat: {} -> {}", macAddress, payload);
                
                // Si el dispositivo estaba offline y ahora está online, sincronizar actuadores
                if (wasOffline) {
                    log.info("🔄 Dispositivo {} volvió online, sincronizando actuadores...", macAddress);
                    syncActuatorStatesForDevice(macAddress);
                }
            }
        } catch (Exception e) {
            log.error("Error procesando heartbeat [{}]: {}", topic, e.getMessage());
        }
    }

    /**
     * Sincroniza los estados de actuadores con un dispositivo ESP32 específico
     * Envía el estado actual de cada actuador según la BD
     * 
     * @param macAddress MAC del dispositivo a sincronizar
     */
    private void syncActuatorStatesForDevice(String macAddress) {
        try {
            // Buscar dispositivo por MAC
            Optional<Device> deviceOpt = deviceService.getDeviceByMacAddress(macAddress);
            if (deviceOpt.isEmpty()) {
                log.warn("No se puede sincronizar: dispositivo no encontrado con MAC {}", macAddress);
                return;
            }
            
            Device device = deviceOpt.get();
            Long deviceId = device.getId();
            
            // Obtener estados de actuadores del dispositivo
            List<ActuatorState> states = actuatorService.getActuatorStatesByDevice(deviceId);
            
            for (ActuatorState state : states) {
                // Construir topic con MAC: damiot/actuadores/{MAC}/{actuator_type}
                String topic = "damiot/actuadores/" + macAddress + "/" + state.getActuatorType();
                String command = state.getState(); // "ON" u "OFF"
                
                mqttService.publish(topic, command, 1, false);
                log.info("🔄 Sincronizado: {} -> {} (Device: {}, MAC: {})", 
                        state.getActuatorType(), command, deviceId, macAddress);
            }
        } catch (Exception e) {
            log.error("Error sincronizando actuadores del dispositivo {}: {}", macAddress, e.getMessage());
        }
    }

    /**
     * Procesar estado general del dispositivo
     * 
     * Topic: damiot/dispositivo/estado
     * Payload: "ONLINE" o "OFFLINE"
     */
    private void handleDeviceStatus(String topic, String payload) {
        try {
            log.info("📱 Estado de dispositivo: {}", payload);
            
            // Nota: Este topic NO incluye MAC, es un mensaje genérico
            // La sincronización real se hace cuando llega el heartbeat con MAC
            
            if (payload.equalsIgnoreCase("ONLINE")) {
                log.debug("Dispositivo reportó ONLINE (la sincronización se hará por heartbeat)");
            }
        } catch (Exception e) {
            log.error("Error procesando estado de dispositivo: {}", e.getMessage());
        }
    }

    /**
     * Valida que una MAC address tenga el formato correcto
     * Formato esperado: XX:XX:XX:XX:XX:XX (donde X es un dígito hexadecimal)
     * 
     * @param macAddress MAC a validar
     * @return true si el formato es válido
     */
    private boolean isValidMacAddress(String macAddress) {
        if (macAddress == null || macAddress.isEmpty()) {
            return false;
        }
        // Formato: XX:XX:XX:XX:XX:XX donde X es 0-9, A-F, a-f
        return macAddress.matches("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}");
    }
}
