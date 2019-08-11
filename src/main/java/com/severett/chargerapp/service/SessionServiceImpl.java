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
    public ChargingSession createSession(String stationId) {
        if (stationId != null) {
            ChargingSession newSession = chargingSessionRepo.save(
                    new ChargingSession(UUID.randomUUID(), stationId)
            );
            // Notify stats repo of session start only after session has been
            // successfully persisted
            sessionStatisticsRepo.addSessionStart(Instant.now());
            return newSession;
        } else {
            throw new IllegalArgumentException("stationId must not be null");
        }
    }

    @Override
    public ChargingSession stopSession(String id) {
        if (id != null) {
            return chargingSessionRepo.findById(UUID.fromString(id)).map(chargingSession -> {
                chargingSession.stopCharging();
                // Notify stats repo of session stop only after session has been
                // successfully stopped
                sessionStatisticsRepo.addSessionStop(Instant.now());
                return chargingSession;
            }).orElse(null);
        } else {
            throw new IllegalArgumentException("id must not be null");
        }
    }

    @Override
    public List<ChargingSession> getSessions() {
        List<ChargingSession> sessionsList = new ArrayList<>();
        chargingSessionRepo.findAll().forEach(sessionsList::add);
        return sessionsList;
    }

    @Override
    public Summary getSummary() {
        Instant toTimestamp = Instant.now();
        Instant fromTimestamp = toTimestamp.minusSeconds(60L);
        long sessionStartCount = sessionStatisticsRepo.getSessionStartCount(fromTimestamp, toTimestamp);
        long sessionStopCount = sessionStatisticsRepo.getSessionStartCount(fromTimestamp, toTimestamp);
        return new Summary(sessionStartCount, sessionStopCount);
    }
}
