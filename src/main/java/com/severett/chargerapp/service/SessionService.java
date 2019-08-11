package com.severett.chargerapp.service;

import com.severett.chargerapp.model.ChargingSession;
import com.severett.chargerapp.model.Summary;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SessionService {

    ChargingSession createSession(UUID id, String stationId, Instant timestamp);

    ChargingSession stopSession(UUID id, Instant timestamp) throws IllegalArgumentException;

    List<ChargingSession> getSessions();

    Summary getSummary(Instant fromTimestamp, Instant toTimestamp);

}
