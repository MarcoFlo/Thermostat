package it.polito.thermostat.controllermd.services.logic;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.nio.file.Files;

import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;
import org.springframework.util.ResourceUtils;


/**
 * https://gist.github.com/jimrok/d25cb45b840f5a4ad700
 */

@Service
@PropertySource("classpath:aws-iot-sdk-samples.properties")
public class MQTTdb {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${clientEndpoint}")
    String clientEndpoint;

    @Value("${clientId}")
    String clientId;

    @Value("${certificateFile}")
    String certificateFile;
    //String certificateURL;

    @Value("${privateKeyFile}")
    String privateKeyFile;
    //String privateKeyURL;


    private AWSIotMqttClient mqttClient;

    @PostConstruct
    public void init() {

//      logger.info(Objects.requireNonNull(ResourceUtils.getFile("./certificate//").list())[0]);

        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        mqttClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        mqttClient.setPort(8883);
        mqttClient.setConnectionTimeout(100);
        mqttClient.setKeepAliveInterval(100);
        try {
            logger.info("Trying to connect to aws");

            mqttClient.connect();
            logger.info("done connect aws");
        } catch (Exception e) {
            logger.error("connect exception" + e.toString());
        }

        try {
            mqttClient.subscribe(new MyTopic("pl19/notification", AWSIotQos.QOS1));
        } catch (Exception e) {
            logger.error("subscribe exception" + e.toString());
        }
        logger.info("mqttdb initialization done");

    }


    public static class MyTopic extends AWSIotTopic {
        private Logger logger = LoggerFactory.getLogger(this.getClass());

        public MyTopic(String topic, AWSIotQos qos) {
            super(topic, qos);
        }

        @Override
        public void onMessage(AWSIotMessage message) {
            logger.info(message.toString());

        }
    }

}


