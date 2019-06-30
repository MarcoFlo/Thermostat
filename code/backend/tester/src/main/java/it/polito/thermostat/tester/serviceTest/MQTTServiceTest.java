package it.polito.thermostat.tester.serviceTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.polito.thermostat.tester.resource.RoomResource;
import it.polito.thermostat.tester.entity.ESP8266;
import it.polito.thermostat.tester.entity.Program;
import it.polito.thermostat.tester.repository.ESP8266Repository;
import it.polito.thermostat.tester.repository.RoomRepository;
import org.apache.commons.math3.util.Precision;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


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

    @Value("${main.room.name}")
    String mainRoomName;
    @Value("${main.room.sensor}")
    String mainRoomSensor;
    @Value("${main.room.actuator.cooler}")
    String mainRoomCooler;
    @Value("${main.room.actuator.heater}")
    String mainRoomHeater;


    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ObjectMapper objectMapper;


    private IMqttClient mqttClient;

    String[] sensorType = {"sensor", "heater", "cooler"};
    String[] roomName = {"Kitchen", "Bathroom", "Living"};
    List<String> rpiEsp;

    List<String> savedEsp;


    @PostConstruct
    public void init() throws MqttException {
        esp8266Repository.deleteAll();
        roomRepository.deleteAll();
        rpiEsp = Arrays.asList(mainRoomSensor, mainRoomHeater, mainRoomCooler);
        savedEsp = new LinkedList<>();
        objectMapper.registerModule(new JavaTimeModule());

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


    public void createEspMainRoom() throws MqttException {
        MqttMessage msg;

        //create rpi esp
        for (int i = 0; i < rpiEsp.size(); i++) {
            msg = new MqttMessage(sensorType[i].getBytes());
            msg.setQos(2);
            mqttClient.publish("/esp8266/" + rpiEsp.get(i), msg);
            logger.info("esp with id: " + rpiEsp.get(i) + " created");

        }
        savedEsp.add(mainRoomSensor);
    }

    public void createSecondaryEspAndRoom() throws MqttException {
        MqttMessage msg;
        String idEsp;
        List<String> espRoomList;


        // create normal esp
        for (int j = 0; j < roomName.length; j++) {
            espRoomList = new LinkedList<>();
            for (String s : sensorType) {
                msg = new MqttMessage(s.getBytes());
                msg.setQos(2);
                do {
                    idEsp = "idTest" + ThreadLocalRandom.current().nextInt(0, 100 + 1);
                }
                while (savedEsp.contains(idEsp));

                mqttClient.publish("/esp8266/" + idEsp, msg);
                logger.info("esp with id: " + idEsp + " created");

                savedEsp.add(idEsp);
                espRoomList.add(idEsp);
            }
            createRoom(roomName[j], espRoomList);
        }

    }


    private void createRoom(String idRoom, List<String> espList) {
        URL url = null;
        Program defaultProgram = null;

        try {
            url = new URL("http://localhost:8080/setting/default_program");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoInput(true);

            if (con.getResponseCode() != 200) {
                System.err.println("The server is not working as expected -> default program");
                System.exit(-1);

            }


            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            defaultProgram = objectMapper.readValue(response.toString(), Program.class);
            defaultProgram.setIdProgram(idRoom);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        try {
            url = new URL("http://localhost:8080/setting/room/resource");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            DataOutputStream writer = new DataOutputStream(con.getOutputStream());
            RoomResource roomResource = new RoomResource(idRoom, espList, defaultProgram);
            writer.writeBytes(objectMapper.writeValueAsString(roomResource));
            writer.flush();
            writer.close();

            if (con.getResponseCode() != 200) {
                System.err.println("The server is not working as expected -> new room");
                System.exit(-1);
            }
            logger.info("Room " + idRoom + " created");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);

        }


    }


    @Scheduled(fixedRate = 10000)
    public void newSensorData() throws MqttException {
        for (String idEsp : savedEsp) {
            Optional<ESP8266> checkEsp = esp8266Repository.findById(idEsp);
            if (checkEsp.isPresent()) {
                ESP8266 esp8266 = checkEsp.get();
                if (esp8266.getIsSensor()) {
                    String supp = Precision.round(ThreadLocalRandom.current().nextDouble(10, 30), 2) + "_" + Precision.round(ThreadLocalRandom.current().nextDouble(40, 80), 2);
                    MqttMessage msg = new MqttMessage(supp.getBytes());
                    msg.setQos(2);
                    mqttClient.publish("/" + idEsp + "/sensor", msg);
                    logger.info("new Sensor data from " + idEsp);
                }
            }
        }
    }

}
