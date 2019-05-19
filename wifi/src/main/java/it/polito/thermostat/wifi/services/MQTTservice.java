package it.polito.thermostat.wifi.services;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

@Service
public class MQTTservice {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IMqttClient mqttClient;

    @PostConstruct
    public void init() throws UnknownHostException, MqttException {
        String localBroker = "tcp://" + calculateIp() + ":1883";
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
//      options.setCleanSession(true);
        options.setConnectionTimeout(10000);
        options.setKeepAliveInterval(10000);
        mqttClient = new MqttClient(localBroker, "wifiSpring");
        mqttClient.connect(options);
    }

    public void sendWifiCredentials(String essid, String pw) {
        String credentials = essid + "_" + pw;
        MqttMessage msg = new MqttMessage(credentials.getBytes());
        msg.setQos(2);
        msg.setRetained(true); //se arriva un esp nuovo e non sa a chi connettersi
        try {
            mqttClient.publish("/wifi", msg);
        } catch (MqttException e) {
            logger.error("MqttService/sendWifiCredentials - publish -> " + e.toString());
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
