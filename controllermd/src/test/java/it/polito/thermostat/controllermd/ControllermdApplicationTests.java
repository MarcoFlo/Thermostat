package it.polito.thermostat.controllermd;

import it.polito.thermostat.controllermd.entity.ESP8266;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.services.MQTTservice;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllermdApplicationTests {

    private Logger logger = LoggerFactory.getLogger(this.getClass());




//    @Test
//    public void newEsp() throws MqttException {
//
//        if (!mqttClient.isConnected()) {
//            logger.info("accidenti");
//        }
//
//        MqttMessage msg = new MqttMessage("sensor".getBytes());
//        msg.setQos(2);
//        msg.setRetained(true);
//        mqttClient.publish("/esp8266/idTest", msg);
//        Optional<ESP8266> check = esp8266Repository.findByIdEsp("idTest");
//        assert check.isPresent();
//        assert check.get().getIsSensor();
//        assert !check.get().getIsCooler();
//        esp8266Repository.delete(check.get());
//
//        msg = new MqttMessage("heater".getBytes());
//        msg.setQos(2);
//        msg.setRetained(true);
//        mqttClient.publish("/esp8266/idTest", msg);
//        check = esp8266Repository.findByIdEsp("idTest");
//        assert check.isPresent();
//        assert !check.get().getIsSensor();
//        assert !check.get().getIsCooler();
//        esp8266Repository.delete(check.get());
//
//        msg = new MqttMessage("cooler".getBytes());
//        msg.setQos(2);
//        msg.setRetained(true);
//        mqttClient.publish("/esp8266/idTest", msg);
//        check = esp8266Repository.findByIdEsp("idTest");
//        assert check.isPresent();
//        assert !check.get().getIsSensor();
//        assert check.get().getIsCooler();
//        esp8266Repository.delete(check.get());
//
//    }

}
