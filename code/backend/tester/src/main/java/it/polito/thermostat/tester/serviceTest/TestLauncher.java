package it.polito.thermostat.tester.serviceTest;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestLauncher {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MQTTServiceTest mqttServiceTest;


    public void launchAll() {
        try {
            mqttServiceTest.createEspMainRoom();
            mqttServiceTest.createSecondaryEspAndRoom();

        } catch (MqttException | InterruptedException e) {
            logger.error("newEspTest - publish exception");
        }
    }
}
