package com.severett.chargerapp.data;

import com.severett.chargerapp.model.ChargingSession;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryChargingSessionRepo implements ChargingSessionRepo {

    private final Map<UUID, ChargingSession> sessionMap;

    public InMemoryChargingSessionRepo() {
        sessionMap = new ConcurrentHashMap<>();
    }

    @Override
    public ChargingSession save(ChargingSession chargingSession) {
        return sessionMap.put(chargingSession.getId(), chargingSession);
    }

    @Override
    public Optional<ChargingSession> findById(UUID id) {
        return Optional.ofNullable(sessionMap.get(id));
    }

    @Override
    public Iterable<ChargingSession> findAll() {
        return sessionMap.values();
    }

}
