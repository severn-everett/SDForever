package com.severett.chargerapp.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ChargingSession {

    private final UUID id;
    private final String stationId;
    private Instant startedAt;
    private Instant stoppedAt;
    private Status status;

    public ChargingSession(UUID id, String stationId, Instant startedAt) {
        this.id = id;
        this.stationId = stationId;
        this.startedAt = startedAt;
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

    public void stopCharging(Instant stoppedAt) {
        status = Status.FINISHED;
        this.stoppedAt = stoppedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargingSession that = (ChargingSession) o;
        return id.equals(that.id) &&
                stationId.equals(that.stationId) &&
                startedAt.equals(that.startedAt) &&
                Objects.equals(stoppedAt, that.stoppedAt) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stationId, startedAt, stoppedAt, status);
    }
}
