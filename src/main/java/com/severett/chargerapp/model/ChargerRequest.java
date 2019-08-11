package com.severett.chargerapp.model;

public class ChargerRequest {

    private String stationId;

    public ChargerRequest() {
    }

    public ChargerRequest(String stationId) {
        this.stationId = stationId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }
}
