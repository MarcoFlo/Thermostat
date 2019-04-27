package it.polito.thermostat.wifi.services;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class MQTTservice  {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String timeTopic = "/time";
    private InetAddress id = InetAddress.getLocalHost();
    private String hostname = "192.168.43.100";
    private IMqttClient mqttClient;

    public MQTTservice() throws UnknownHostException, MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(1000);
        mqttClient = new MqttClient("tcp://" + hostname + ":1883", id.toString());
        mqttClient.connect(options);
        mqttClient.subscribe(timeTopic, this::messageArrived);
    }


    public void publishCommand() throws Exception {

        if (!mqttClient.isConnected()) {
            logger.error("MQTT Client not connected.");
            return;
        }

        MqttMessage msg = new MqttMessage("msg prova".getBytes());
        msg.setQos(2);
        msg.setRetained(true);
        mqttClient.publish(timeTopic, msg);
    }


    /**
     * messageArrived
     * This callback is invoked when a message is received on a subscribed topic.
     */
    private void messageArrived(String topic, MqttMessage message) {
        logger.info("Topic:" + topic);
        logger.info("Message: " + message.toString());
    }

}
