package it.polito.thermostat.tester.serviceTest;

import it.polito.thermostat.tester.entity.ESP8266;
import it.polito.thermostat.tester.entity.Room;
import it.polito.thermostat.tester.repository.ESP8266Repository;
import it.polito.thermostat.tester.repository.RoomRepository;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.util.Precision;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MQTTServiceTest {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${mqtt.online}")
    Boolean isMQTTOnline;
    @Value("${cloudmqtt.broker}")
    String cloudmqttBroker;
    @Value("${cloudmqtt.user}")
    String cloudmqttUser;
    @Value("${cloudmqtt.pass}")
    String cloudmqttPass;


    @Value("${mosquitto.broker}")
    String mosquittoBroker;

    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    RoomRepository roomRepository;

    private IMqttClient mqttClient;

    String[] sensorType = {"sensor", "heater", "cooler"};
    String[] roomName = {"Kitchen", "Bathroom", "Living"};

    List<String> savedEsp;


    @PostConstruct
    public void init() throws MqttException {
        savedEsp = new LinkedList<>();

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
//      options.setCleanSession(true);
        options.setConnectionTimeout(10000);
        options.setKeepAliveInterval(10000);

        if (isMQTTOnline) {
            logger.info("MQTT cloud Broker");
            mqttClient = new MqttClient(mosquittoBroker, "tester");
        } else {
            logger.info("MQTT local Broker");
            mqttClient = new MqttClient("tcp://" + HostAddressGetter.getIp() + ":1883", "tester");

        }
        mqttClient.connect(options);

    }

    public void newEspTest() throws MqttException, InterruptedException {
        MqttMessage msg = new MqttMessage("sensor".getBytes());
        msg.setQos(2);
        mqttClient.publish("/esp8266/idTest", msg);
        Thread.sleep(500);
        Optional<ESP8266> check = esp8266Repository.findById("idTest");
        if (!check.isPresent() || !check.get().getIsSensor() || check.get().getIsCooler())
            logger.error("newEspTest sensor error");
        esp8266Repository.delete(check.get());

        msg = new MqttMessage("heater".getBytes());
        msg.setQos(2);
        mqttClient.publish("/esp8266/idTest", msg);
        Thread.sleep(500);
        check = esp8266Repository.findById("idTest");
        if (!check.isPresent() || check.get().getIsSensor() || check.get().getIsCooler())
            logger.error("newEspTest heater error");
        esp8266Repository.delete(check.get());

        msg = new MqttMessage("cooler".getBytes());
        msg.setQos(2);
        mqttClient.publish("/esp8266/idTest", msg);
        Thread.sleep(500);
        check = esp8266Repository.findById("idTest");
        if (!check.isPresent() || check.get().getIsSensor() || !check.get().getIsCooler())
            logger.error("newEspTest cooler error");
        esp8266Repository.delete(check.get());
    }


    public void createEspAndRoom() throws MqttException {
        MqttMessage msg;
        int id = -1;
        esp8266Repository.deleteAll();
        roomRepository.deleteAll();


        for (int j = 0; j < roomName.length; j++) {
            for (String s : sensorType) {
                msg = new MqttMessage(s.getBytes());
                msg.setQos(2);
                do {
                    id = ThreadLocalRandom.current().nextInt(0, 100 + 1);
                }
                while (savedEsp.contains("idTest" + id));

                mqttClient.publish("/esp8266/idTest" + id, msg);
                if (s.equals("sensor")) {
                    roomRepository.save(new Room(roomName[j], Collections.singletonList("idTest" + id), true, 18.0));
                    logger.info("Room " + roomName[j] + " created " + id);
                }
                savedEsp.add("idTest" + id);

                logger.info("esp with id: " + "idTest" + id + " created");
            }
        }

    }


    @Scheduled(fixedRate = 10000)
    public void newSensorData() throws MqttException {
        for (String idEsp : savedEsp) {
            Optional<ESP8266> checkEsp = esp8266Repository.findById(idEsp);
            if (checkEsp.isPresent()) {
                ESP8266 esp8266 = checkEsp.get();
                if (esp8266.getIsSensor()) {
                    String supp = Precision.round(ThreadLocalRandom.current().nextDouble(10, 30), 2) + "_" + Precision.round(ThreadLocalRandom.current().nextDouble(10, 30), 2);
                    MqttMessage msg = new MqttMessage(supp.getBytes());
                    msg.setQos(2);
                    mqttClient.publish("/" + idEsp + "/sensor", msg);
                    logger.info("new Sensor data from " + idEsp);
                }
            }
        }
    }

}
