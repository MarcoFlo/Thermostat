package it.polito.thermostat.controllermd.services;


import it.polito.thermostat.controllermd.object.ESP8266;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class MQTTservice {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String esp8266Topic = "/esp8266/#";
    private InetAddress id = InetAddress.getLocalHost();
    //private String hostname = "192.168.1.143";
    private String hostname  = "192.168.1.206";
    String localBroker = "tcp://" + hostname + ":1883";
    String internetBroker = "tcp://test.mosquitto.org:1883";
    private IMqttClient mqttClient;



    @Autowired
    ESP8266Repository esp8266Repository;

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
    public void manageActuator(ESP8266 esp8266, String command) {

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
        try {
            mqttClient.publish("/" + esp8266.getIdEsp(), msg);
        } catch (MqttException e) {
            logger.error("MqttService/manageActuator - publish -> " + e.toString());
        }
    }


    /**
     * This callback is invoked when a new esp is connected
     */
    private void esp8266Connection(String topic, MqttMessage message) throws MqttException {
        ESP8266 esp8266 = new ESP8266();
        esp8266.setIdEsp(topic.split("/")[2]);
        if (message.equals("sensor")) {
            esp8266.setIsSensor(true);
            mqttClient.subscribe("/" + esp8266.getIdEsp(), this::sensorDataReceived);
        } else {
            esp8266.setIsSensor(false);
            if (message.equals("heater"))
                esp8266.setIsHeater(true);
            else
                esp8266.setIsHeater(false);
        }
        esp8266Repository.save(esp8266);

        logger.info("New esp8266 connected");
        logger.info("\tesp8266 idEsp ->" + esp8266.getIdEsp());
        logger.info("\tisActuator ->" + !esp8266.getIsSensor());

    }

    /**
     * Handle new sensor data
     *
     * @param topic
     * @param message
     */
    private void sensorDataReceived(String topic, MqttMessage message) {
        ESP8266 esp8266 = esp8266Repository.findByIdEsp(topic.split("/")[1]).get();
        String[] data = message.toString().split("_");
        esp8266.setTemperature(Double.valueOf(data[0]));
        esp8266.setHumidity(Double.valueOf(data[1]));
    }
}
