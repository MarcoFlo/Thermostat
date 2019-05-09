package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.object.ESP8266;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class MQTTservice {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String esp8266Topic = "/esp8266/#";
    private InetAddress id = InetAddress.getLocalHost();
    private String hostname = "192.168.1.143";
    String localBroker = "tcp://" + hostname + ":1883";
    String internetBroker = "tcp://test.mosquitto.org:1883";
    private IMqttClient mqttClient;



    public MQTTservice() throws UnknownHostException, MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(1000);
        mqttClient = new MqttClient(localBroker, id.toString());
        mqttClient.connect(options);
    }

}
