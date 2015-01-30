package com.example.rossi.meteo;

/**
 * Forecast for a given time interval.
 */
public class Forecast {
    // String describing the key . Example: 22h30 � 22h45
    public final String key;
    // String describing the forecast value. Example: Pas de pluie
    public final String value;

    public Forecast(String key, String forecast) {
        this.key = key;
        this.value = forecast;
    }

    @Override
    public String toString() {
        return key + ": " + value;
    }
}
