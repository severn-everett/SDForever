package com.severett.chargerapp.data;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySessionStatisticsRepo implements SessionStatisticsRepo {

    private final Map<Long, Long> startSessionCounter;

    private final Map<Long, Long> stopSessionCounter;

    public InMemorySessionStatisticsRepo() {
        startSessionCounter = new ConcurrentHashMap<>();
        stopSessionCounter = new ConcurrentHashMap<>();
    }

    @Override
    public void addSessionStart(Instant timestamp) {
        startSessionCounter.compute(
                timestamp.getEpochSecond(),
                (key, oldValue) -> oldValue != null ? oldValue + 1L : 1L
        );
    }

    @Override
    public void addSessionStop(Instant timestamp) {
        stopSessionCounter.compute(
                timestamp.getEpochSecond(),
                (key, oldValue) -> oldValue != null ? oldValue + 1L : 1L
        );
    }

    @Override
    public long getSessionStartCount(Instant fromTimestamp, Instant toTimestamp) {
        return getCountAggregate(
                startSessionCounter,
                fromTimestamp.getEpochSecond(),
                toTimestamp.getEpochSecond()
        );
    }

    @Override
    public long getSessionStopCount(Instant fromTimestamp, Instant toTimestamp) {
        return getCountAggregate(
                stopSessionCounter,
                fromTimestamp.getEpochSecond(),
                toTimestamp.getEpochSecond()
        );
    }

    private long getCountAggregate(Map<Long, Long> counter, long fromEpoch, long toEpoch) {
        long retValue = 0L;
        long currentEpoch = fromEpoch;
        do {
            retValue += counter.getOrDefault(currentEpoch, 0L);
            currentEpoch++;
        } while (currentEpoch <= toEpoch);
        return retValue;
    }
}
