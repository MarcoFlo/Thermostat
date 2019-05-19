package it.polito.thermostat.wifi.configuration;

import lombok.Data;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Data
public class MongoZonedDateTime extends Date {

    private static Date parseData(String pattern, String completeData) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        ZonedDateTime londonTime = ZonedDateTime.parse(completeData, dateTimeFormatter);
        return Date.from(londonTime.toInstant());
    }


    public static Date getNow() {
        // String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS z";
        // DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        // String completeData = LocalDateTime.now().toString() + " GMT+00:00";
        // ZonedDateTime londonTime = ZonedDateTime.parse(completeData, dateTimeFormatter);
        // return Date.from(londonTime.toInstant());
        return parseData("yyyy-MM-dd'T'HH:mm:ss.SSS z", LocalDateTime.now().toString() + " GMT+00:00");
    }

    public static Date getMongoZonedDateTimeFromDate(LocalDateTime date) {
        ZoneId zone = ZoneId.of("UTC+00:00");
        ZoneOffset zoneOffSet = zone.getRules().getOffset(date);

        return Date.from(date.toInstant(zoneOffSet));
    }

    public static LocalTime getTimeFromMongoZonedDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Europe/London")).toLocalTime();
    }

    public static LocalDateTime getDateFromMongoZonedDateTime(Date date) {

        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Europe/London"));
    }
}
