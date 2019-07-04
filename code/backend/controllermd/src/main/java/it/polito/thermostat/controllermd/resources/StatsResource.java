package it.polito.thermostat.controllermd.resources;

import it.polito.thermostat.controllermd.entity.Stats;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;


@Data
@AllArgsConstructor
public class StatsResource {
    private LocalDate day;
    private String idRoom;

    private Long amount;
    public StatsResource(Stats stats)
    {
        day = stats.getDay();
        idRoom = stats.getIdRoom();
        amount = stats.getAmount();
    }
}
