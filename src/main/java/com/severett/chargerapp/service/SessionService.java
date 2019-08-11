package com.severett.chargerapp.service;

import com.severett.chargerapp.model.ChargingSession;
import com.severett.chargerapp.model.Summary;

import java.time.Instant;
import java.util.List;

public interface SessionService {

    ChargingSession createSession(String stationId);

    ChargingSession stopSession(String id) throws IllegalArgumentException;

    List<ChargingSession> getSessions();

    Summary getSummary();

}
