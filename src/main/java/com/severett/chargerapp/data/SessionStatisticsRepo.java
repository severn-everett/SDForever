package com.severett.chargerapp.data;

import java.time.Instant;

public interface SessionStatisticsRepo {

    void addSessionStart(Instant timestamp);

    void addSessionStop(Instant timestamp);

    long getSessionStartCount(Instant beginTimestamp, Instant endTimestamp);

    long getSessionStopCount(Instant beginTimestamp, Instant endTimestamp);

}
