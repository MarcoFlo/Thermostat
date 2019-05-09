package it.polito.thermostat.wifi.object;

import lombok.Data;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "programs")
public class Programm {

    @Id
    private String idProgramm;
    private Map<Integer, DailyProgramm> programs;

}
