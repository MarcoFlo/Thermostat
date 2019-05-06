package it.polito.thermostat.controllermd.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CustomDateDeserializer extends StdDeserializer<Date> {
    private String pattern = "yyyy-MM-dd HH:mm z";
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);

    public CustomDateDeserializer() {
        this(null);
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String time = jsonParser.getText();
        String completeData = "2019-01-01 "+ time + " GMT+00:00";

        ZonedDateTime londonTime = ZonedDateTime.parse(completeData, dateTimeFormatter);
        return Date.from(londonTime.toInstant());

    }

    public CustomDateDeserializer(Class<?> vc) {
        super(vc);
    }

}