package it.polito.thermostat.tester.serviceTest;

import it.polito.thermostat.tester.entity.ESP8266;
import it.polito.thermostat.tester.repository.ESP8266Repository;
import org.apache.commons.math3.util.Precision;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MQTTServiceTest {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ESP8266Repository esp8266Repository;

    private IMqttClient mqttClient;

    String localBroker = "tcp://" + calculateIp() + ":1883";
    String espDataProducer = "espDataProducer";

    @PostConstruct
    public void init() throws MqttException {
        mqttClient = new MqttClient(localBroker, "mqttTester");

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10000);
        options.setKeepAliveInterval(10000);
        options.setCleanSession(true);
        mqttClient.connect(options);
    }

    @Async("threadPoolTaskExecutor")
    public void newEspTest() throws MqttException, InterruptedException {
        MqttMessage msg = new MqttMessage("sensor".getBytes());
        msg.setQos(2);
        mqttClient.publish("/esp8266/idTest", msg);
        Thread.sleep(500);
        Optional<ESP8266> check = esp8266Repository.findByIdEsp("idTest");
        if (!check.isPresent() || !check.get().getIsSensor() || check.get().getIsCooler())
            logger.error("newEspTest sensor error");
        esp8266Repository.delete(check.get());

        msg = new MqttMessage("heater".getBytes());
        msg.setQos(2);
        msg.setRetained(true);
        mqttClient.publish("/esp8266/idTest", msg);
        Thread.sleep(500);
        check = esp8266Repository.findByIdEsp("idTest");
        if (!check.isPresent() || check.get().getIsSensor() || check.get().getIsCooler())
            logger.error("newEspTest heater error");
        esp8266Repository.delete(check.get());

        msg = new MqttMessage("cooler".getBytes());
        msg.setQos(2);
        msg.setRetained(true);
        mqttClient.publish("/esp8266/idTest", msg);
        Thread.sleep(500);
        check = esp8266Repository.findByIdEsp("idTest");
        if (!check.isPresent() || check.get().getIsSensor() || !check.get().getIsCooler())
            logger.error("newEspTest cooler error");

        esp8266Repository.delete(check.get());
    }


    @Scheduled(fixedRate = 1000)
    public void newSensorData() throws MqttException, InterruptedException {
        setUpProducer();
        String supp = Precision.round(ThreadLocalRandom.current().nextDouble(0, 100), 2) + "_" + Precision.round(ThreadLocalRandom.current().nextDouble(0, 100), 2);
        MqttMessage msg = new MqttMessage(supp.getBytes());
        msg.setQos(2);
        mqttClient.publish("/" + espDataProducer, msg);
    }

    private void setUpProducer() throws MqttException, InterruptedException {
        if (!esp8266Repository.findByIdEsp(espDataProducer).isPresent()) {
            MqttMessage msg = new MqttMessage("sensor".getBytes());
            msg.setQos(2);
            mqttClient.publish("/esp8266/" + espDataProducer, msg);
            Thread.sleep(500);
        }
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
                        nameInterface = "wlan0";
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