package it.polito.thermostat.controllermd.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;


@Data
@AllArgsConstructor
public class StatsResource {
    private List<String> dayList;
    private List<List<Long>> dataList;
}
