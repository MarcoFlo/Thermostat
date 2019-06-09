package it.polito.thermostat.controllermd.resources.MQTTaws;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AWSDateDeserializer extends StdDeserializer<LocalDateTime> {

    @Value("${timestamp.formatter}")
    private String pattern;

    public AWSDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String date = jsonParser.getText();
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
    }


}