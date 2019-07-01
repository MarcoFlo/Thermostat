package it.polito.thermostat.controllermd.services.logic;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.thermostat.controllermd.configuration.HostAddressGetter;
import it.polito.thermostat.controllermd.entity.CommandActuator;
import it.polito.thermostat.controllermd.entity.ESP8266;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.SensorData;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.SensorDataRepository;
import it.polito.thermostat.controllermd.resources.ThermostatClientResource;
import it.polito.thermostat.controllermd.services.server.Esp8266ManagementService;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class MQTTservice {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    ManagerService managerService;

    @Autowired
    SensorDataRepository sensorDataRepository;

    @Autowired
    MQTTAWService mqttawService;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    Environment environment;

    @Value("${mqtt.online}")
    Boolean isMQTTOnline;
    @Value("${mosquitto.host}")
    String mosquittoBroker;

    private IMqttClient mqttClient;
    private String esp8266Topic = "/esp8266/#";

    @PostConstruct
    public void init() throws MqttException {
        String localBroker = "tcp://" + HostAddressGetter.getIp() + ":1883";
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
//      options.setCleanSession(true);
        options.setConnectionTimeout(10000);
        options.setKeepAliveInterval(10000);

        if (isMQTTOnline) {
            logger.info("MQTT cloud Broker");
            mqttClient = new MqttClient(mosquittoBroker, "controllerMD");
        } else {
            logger.info("MQTT local Broker");
            mqttClient = new MqttClient(localBroker, "controllerMD");

        }
        mqttClient.connect(options);
        mqttClient.subscribe(esp8266Topic, this::esp8266Connection);
        logger.info("MQTTService connection done");

        ((List<ESP8266>)esp8266Repository.findAll()).stream().filter(ESP8266::getIsSensor).forEach(esp -> {
            try {
                logger.info("Subscribed to " + esp.getIdEsp());
                mqttClient.subscribe("/" + esp.getIdEsp() + "/sensor", this::sensorDataReceived);
            } catch (MqttException e) {
                logger.error("Mqtt service/manageKnowEsp error \n" + e.toString());
            }
        });
    }


    /**
     * @param commandActuator
     */
    public void manageActuator(CommandActuator commandActuator) {

        String command = commandActuator.getCommandBoolean() ? "on" : "off";
        MqttMessage msg = new MqttMessage(command.getBytes());
        msg.setQos(2);
        //msg.setRetained(true);
        try {
            mqttClient.publish("/" + commandActuator.getIdEsp() + "/actuator", msg);
            mqttawService.sendEvent(commandActuator, 11);
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
                subscribeSensor(esp8266.getIdEsp());
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

        mqttawService.sendEvent(esp8266, 10);

        logger.info("New esp8266 idEsp ->" + esp8266.getIdEsp());
        logger.info("\tisSensor ->" + esp8266.getIsSensor());
        logger.info("\tisCooler ->" + esp8266.getIsCooler());
    }


    public void subscribeSensor(String esp) {
        try {
            mqttClient.subscribe("/" + esp + "/sensor", this::sensorDataReceived);
        } catch (MqttException e) {
            logger.error("Mqtt service/subscribeSensor error \n" + e.toString());
        }
    }

    /**
     * Handle new sensor data
     *
     * @param topic
     * @param message
     */
    private void sensorDataReceived(String topic, MqttMessage message) throws MqttException, JsonProcessingException {
        String[] data = message.toString().split("_");
        String idEsp = topic.split("/")[1];

        //write into the db the sensor data
        SensorData sensorData = new SensorData(idEsp, Double.valueOf(data[0]), Double.valueOf(data[1]));
        sensorDataRepository.save(sensorData);
//        logger.info("New sensor data -> " + data[0] + "\t" + data[1]);

        updateClientData(idEsp, sensorData);
        mqttawService.sendEvent(sensorData, 12);
    }

    private void updateClientData(String idEsp, SensorData sensorData) throws MqttException, JsonProcessingException {
        Optional<Room> checkRoom;
        if (environment.getActiveProfiles()[0].equals("dev"))
            checkRoom = ((List<Room>) roomRepository.findAll()).stream().filter(r -> r.getEsp8266List().contains(idEsp)).findFirst();
        else
            checkRoom = ((List<Room>) roomRepository.findAll()).stream().filter(r -> r.getEsp8266List().contains(idEsp)).findFirst();

        if (checkRoom.isPresent()) {
            Room room = checkRoom.get();
            ThermostatClientResource thermostatClientResource = new ThermostatClientResource(room.getIsManual() ? -1 : room.getDesiredTemperature(), sensorData.getApparentTemperature());
            MqttMessage msg = new MqttMessage(objectMapper.writeValueAsString(thermostatClientResource).getBytes());
            msg.setQos(2);
            msg.setRetained(true);
            mqttClient.publish("/temperature/" + room.getIdRoom(), msg);

        }
    }
}
