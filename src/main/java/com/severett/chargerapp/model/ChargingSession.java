package com.severett.chargerapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class ChargingSession {

    private UUID id;
    private String stationId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC")
    private Instant startedAt;
    @JsonInclude(NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC")
    private Instant stoppedAt;
    private Status status;

    public ChargingSession() {
    }

    public ChargingSession(UUID id, String stationId, Instant startedAt) {
        this.id = id;
        this.stationId = stationId;
        this.startedAt = startedAt;
        this.status = Status.IN_PROGRESS;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt(Instant stoppedAt) {
        this.stoppedAt = stoppedAt;
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
