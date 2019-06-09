package it.polito.thermostat.controllermd.resources.MQTTaws;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.polito.thermostat.controllermd.configuration.HostAddressGetter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class PingAWS {
    private Integer event_id;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING)
    //    @JsonDeserialize(using = AWSDateDeserializer.class)
    //    @JsonSerialize(using = AWSDateSerializer.class)
    private String timestamp;
    private String device_mac;
    private Event event;

    public PingAWS(PingAWS pingAWS) {
        event_id = 1;
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        device_mac = HostAddressGetter.getMAC();
        event.setSequence(pingAWS.getEvent().getSequence() + 1);
        event.setMessage("Ping response");
    }

    @Data
    @NoArgsConstructor
    public static class Event {
        private String message;
        private Integer sequence;

    }
}
