package it.polito.thermostat.controllermd.services;


import it.polito.thermostat.controllermd.entity.ESP8266;
import it.polito.thermostat.controllermd.object.SensorData;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MQTTservice {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");


    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    TemperatureService temperatureService;

    //The key is idESP
    @Autowired
    ConcurrentHashMap<String, SensorData> mapSensorData;

    private String ipAddr;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String esp8266Topic = "/esp8266/#";

    String internetBroker = "tcp://test.mosquitto.org:1883";
    private IMqttClient mqttClient;

    @PostConstruct
    public void init() throws UnknownHostException, MqttException{
        InetAddress id = InetAddress.getLocalHost();
        ipAddr = calculateIp();
        String localBroker = "tcp://" + ipAddr + ":1883";
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
//        options.setCleanSession(true);
        options.setConnectionTimeout(10000);
        options.setKeepAliveInterval(10000);
        mqttClient = new MqttClient(localBroker, id.toString());
        mqttClient.connect(options);
        mqttClient.subscribe(esp8266Topic, this::esp8266Connection);

        esp8266Repository.findAll().stream().filter(esp8266 -> esp8266.getIsSensor()).forEach(esp -> {
            try {
                mqttClient.subscribe("/" + esp.getIdEsp(), this::sensorDataReceived);
            } catch (MqttException e) {
                logger.error("Mqtt service/manageKnowEsp error \n" + e.toString());
            }
        });
    }




    /**
     * ci permette di gestire gli actuator
     *
     * @param idEsp
     * @throws Exception
     */
    public void manageActuator(String idEsp, Boolean commandBoolean) {

        String command = commandBoolean ? "on" : "off";
        MqttMessage msg = new MqttMessage(command.getBytes());
        msg.setQos(2);
        //msg.setRetained(true);
        try {
            mqttClient.publish("/" + idEsp, msg);
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

        switch (message.toString()) {
            case "sensor":
                esp8266.setIsSensor(true);
                esp8266.setIsCooler(false);
                mqttClient.subscribe("/" + esp8266.getIdEsp(), this::sensorDataReceived);
                break;
            case "cooler":
                esp8266.setIsSensor(false);
                esp8266.setIsCooler(true);
                break;
            case "heater":
                esp8266.setIsSensor(false);
                esp8266.setIsCooler(false);
                break;
            default:
                logger.error("mqttService/esp8266Connection esp msg err");
        }

        esp8266Repository.save(esp8266);

        logger.info("New esp8266 idEsp ->" + esp8266.getIdEsp());
        logger.info("\tisSensor ->" + esp8266.getIsSensor());
        logger.info("\tisCooler ->" + esp8266.getIsCooler());
    }

    /**
     * Handle new sensor data
     *
     * @param topic
     * @param message
     */
    private void sensorDataReceived(String topic, MqttMessage message) {
        String[] data = message.toString().split("_");
        String idEsp = topic.split("/")[1];
        SensorData sensorData = new SensorData(idEsp, Double.valueOf(data[0]), Double.valueOf(data[1]));
        mapSensorData.put(idEsp, sensorData);
        logger.info("New sensor data -> " + data[0] + "\t" + data[1]);
    }


    private String calculateIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isUp() &&
                        !networkInterface.isLoopback() &&
                        !networkInterface.isVirtual()) {

                    String nameInterface;
                    if (isWindows) {
                        nameInterface = "3165";
                    } else {
                        nameInterface = ""; //TODO da settare
                    }
                    if (networkInterface.getDisplayName().contains(nameInterface)) {
                        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            InetAddress addr = addresses.nextElement();
                            if (addr.getHostAddress().length() < 20)
                                return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            logger.error("error MQTTService/getIP");
        }

        return "error";
    }
}
