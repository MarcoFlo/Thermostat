package it.polito.thermostat.controllermd.services.logic;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;


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

    @Value("${privateKeyFile}")
    String privateKeyFile;


    private AWSIotMqttClient mqttClient;

    @PostConstruct
    public void init() throws AWSIotException {
        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        mqttClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        mqttClient.connect();
        mqttClient.subscribe(new NotificationTopic("pl19/notification", AWSIotQos.QOS1));
    }



    public static class NotificationTopic extends AWSIotTopic {
        private Logger logger = LoggerFactory.getLogger(this.getClass());

        public NotificationTopic(String topic, AWSIotQos qos) {
            super(topic, qos);
        }

        @Override
        public void onMessage(AWSIotMessage message) {
            //logger.info(message.getStringPayload());

        }
    }
}


