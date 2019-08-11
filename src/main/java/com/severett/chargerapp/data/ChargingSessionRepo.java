package com.severett.chargerapp.data;

import com.severett.chargerapp.model.ChargingSession;

import java.util.Optional;
import java.util.UUID;

public interface ChargingSessionRepo {

    ChargingSession save(ChargingSession chargingSession);

    Optional<ChargingSession> findById(UUID id);

    Iterable<ChargingSession> findAll();

}
