package com.severett.chargerapp.model;

import java.time.Instant;
import java.util.UUID;

public final class ChargingSession {

    private final UUID id;
    private final String stationId;
    private Instant startedAt;
    private Instant stoppedAt;
    private Status status;

    public ChargingSession(UUID id, String stationId) {
        this.id = id;
        this.stationId = stationId;
        this.status = Status.IN_PROGRESS;
    }

    public UUID getId() {
        return id;
    }

    public String getStationId() {
        return stationId;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getStoppedAt() {
        return stoppedAt;
    }

    public void stopCharging() {
        status = Status.FINISHED;
        stoppedAt = Instant.now();
    }

}
