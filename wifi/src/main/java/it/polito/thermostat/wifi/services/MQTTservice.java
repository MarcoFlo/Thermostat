package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.object.ESP8266;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MQTTservice {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String esp8266Topic = "/esp8266/#";
    private InetAddress id = InetAddress.getLocalHost();
    private String hostname = "192.168.1.143";
    String localBroker = "tcp://" + hostname + ":1883";
    String internetBroker = "tcp://test.mosquitto.org:1883";
    private IMqttClient mqttClient;

    @Autowired
    private ConcurrentHashMap<String, ESP8266> esp8266Map;

    public MQTTservice() throws UnknownHostException, MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(1000);
        mqttClient = new MqttClient(localBroker, id.toString());
        mqttClient.connect(options);
        mqttClient.subscribe(esp8266Topic, this::esp8266Connection);
    }


    public void publishESP8266Debug() throws Exception {

        if (!mqttClient.isConnected()) {
            logger.error("MQTT Client not connected.");
            return;
        }

        MqttMessage msg = new MqttMessage("sensor".getBytes());
        msg.setQos(2);
        //msg.setRetained(true);
        mqttClient.publish("/esp8266/id1", msg);
    }

    /**
     * ci permette di gestire gli actuator
     *
     * @param esp8266
     * @throws Exception
     */
    public void manageActuator(ESP8266 esp8266, String command) throws Exception {

        if (esp8266.getIsSensor()) {
            logger.error("manageActuator error, this esp8266 is not related to an actuator");
            return;
        }
        if (!mqttClient.isConnected()) {
            logger.error("MQTT Client not connected.");
            return;
        }

        MqttMessage msg = new MqttMessage(command.getBytes());
        msg.setQos(2);
        //msg.setRetained(true);
        mqttClient.publish("/" + esp8266.getId(), msg);
    }


    /**
     * This callback is invoked when a new esp is connected
     */
    private void esp8266Connection(String topic, MqttMessage message) throws MqttException {
        ESP8266 esp8266 = new ESP8266();
        esp8266.setId(topic.split("/")[2]);
        if (message.equals("sensor")) {
            esp8266.setIsSensor(true);
            mqttClient.subscribe("/" + esp8266.getId(), this::sensorDataReceived);
        } else {
            esp8266.setIsSensor(false);
            if (message.equals("heater"))
                esp8266.setIsHeater(true);
            else
                esp8266.setIsHeater(false);
        }
        esp8266Map.put(esp8266.getId(), esp8266); //TODO DB


        logger.info("New esp8266 connected");
        logger.info("\tesp8266 id ->" + esp8266.getId());
        logger.info("\tisActuator ->" + !esp8266.getIsSensor());

    }

    /**
     * Handle new sensor data
     *
     * @param topic
     * @param message
     */
    private void sensorDataReceived(String topic, MqttMessage message) {
        ESP8266 esp8266 = esp8266Map.get(topic.split("/")[1]);
        String[] data = message.toString().split("_");
        esp8266.setTemperature(Double.valueOf(data[0]));
        esp8266.setHumidity(Double.valueOf(data[1]));
    }
}
