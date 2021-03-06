package it.polito.thermostat.tester.serviceTest;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;



@Service
public class TestLauncher {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MQTTServiceTest mqttServiceTest;

    @Value("${main.room.name}")
    String mainRoomName;
    @Value("${main.room.sensor}")
    String mainRoomSensor;
    @Value("${main.room.actuator.cooler}")
    String mainRoomCooler;
    @Value("${main.room.actuator.heater}")
    String mainRoomHeater;

    public void launchAll() {
        try {
            mqttServiceTest.createMainRoom();
//            Map<String, List<String>> mapRoomEsp = mqttServiceTest.createSecondaryEspAndRoom();
//            mqttServiceTest.createNotAssociatedEsp();
//
//            TimeUnit.SECONDS.sleep(10);
//            mapRoomEsp.forEach((idRoom, listEsp) -> mqttServiceTest.createRoom(idRoom, listEsp));
        } catch (MqttException | InterruptedException e) {
            logger.error("newEspTest - publish exception");
        }
    }
}
