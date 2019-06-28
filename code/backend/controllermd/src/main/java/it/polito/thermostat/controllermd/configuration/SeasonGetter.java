package it.polito.thermostat.controllermd.configuration;

import java.time.LocalDateTime;

public class SeasonGetter {

    public static boolean isSummer() {
        switch (LocalDateTime.now().getMonth()) {
            case APRIL:
            case MAY:
            case JUNE:
            case JULY:
            case AUGUST:
            case SEPTEMBER:
            case OCTOBER:
                return true;
            case NOVEMBER:
            case DECEMBER:
            case JANUARY:
            case FEBRUARY:
            case MARCH:
                return false;
        }
        return true;
    }
}
