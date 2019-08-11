package com.severett.chargerapp.model;

public class ChargerRequest {

    private final String stationId;

    public ChargerRequest(String stationId) {
        this.stationId = stationId;
    }

    public String getStationId() {
        return stationId;
    }
}
