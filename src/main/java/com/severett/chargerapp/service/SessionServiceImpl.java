package com.severett.chargerapp.service;

import com.severett.chargerapp.data.ChargingSessionRepo;
import com.severett.chargerapp.data.SessionStatisticsRepo;
import com.severett.chargerapp.model.ChargingSession;
import com.severett.chargerapp.model.Summary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    private final ChargingSessionRepo chargingSessionRepo;
    private final SessionStatisticsRepo sessionStatisticsRepo;

    public SessionServiceImpl(ChargingSessionRepo chargingSessionRepo, SessionStatisticsRepo sessionStatisticsRepo) {
        this.chargingSessionRepo = chargingSessionRepo;
        this.sessionStatisticsRepo = sessionStatisticsRepo;
    }

    @Override
    public ChargingSession createSession(UUID id, String stationId, Instant timestamp) {
        if (stationId != null) {
            ChargingSession newSession = new ChargingSession(id, stationId, timestamp);
            chargingSessionRepo.save(newSession);
            // Notify stats repo of session start only after session has been
            // successfully persisted
            sessionStatisticsRepo.addSessionStart(timestamp);
            return newSession;
        } else {
            throw new IllegalArgumentException("stationId must not be null");
        }
    }

    @Override
    public ChargingSession stopSession(UUID id, Instant timestamp) {
        return chargingSessionRepo.findById(id).map(chargingSession -> {
            chargingSession.stopCharging(timestamp);
            // Notify stats repo of session stop only after session has been
            // successfully stopped
            sessionStatisticsRepo.addSessionStop(timestamp);
            return chargingSessionRepo.save(chargingSession);
        }).orElse(null);
    }

    @Override
    public List<ChargingSession> getSessions() {
        List<ChargingSession> sessionsList = new ArrayList<>();
        chargingSessionRepo.findAll().forEach(sessionsList::add);
        return sessionsList;
    }

    @Override
    public Summary getSummary(Instant fromTimestamp, Instant toTimestamp) {
        long sessionStartCount = sessionStatisticsRepo.getSessionStartCount(fromTimestamp, toTimestamp);
        long sessionStopCount = sessionStatisticsRepo.getSessionStopCount(fromTimestamp, toTimestamp);
        return new Summary(sessionStartCount, sessionStopCount);
    }
}
