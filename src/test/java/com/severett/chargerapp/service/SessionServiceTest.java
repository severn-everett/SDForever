package com.severett.chargerapp.service;

import com.severett.chargerapp.data.ChargingSessionRepo;
import com.severett.chargerapp.data.SessionStatisticsRepo;
import com.severett.chargerapp.model.ChargingSession;
import com.severett.chargerapp.model.Summary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.severett.chargerapp.model.Status.FINISHED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SessionServiceTest {

    private SessionService sessionService;

    private ChargingSessionRepo chargingSessionRepo;
    private SessionStatisticsRepo sessionStatisticsRepo;

    private UUID uuid = UUID.fromString("1b14873e-a383-4e90-9554-8879b2a6637b");
    private String stationIdOne = "ABC-12345";
    private Instant createdTimestamp = Instant.ofEpochSecond(1565516634L);
    private Instant stoppedTimestamp = createdTimestamp.plusSeconds(30L);

    @BeforeEach
    void setup() {
        chargingSessionRepo = mock(ChargingSessionRepo.class);
        sessionStatisticsRepo = mock(SessionStatisticsRepo.class);

        sessionService = new SessionServiceImpl(chargingSessionRepo, sessionStatisticsRepo);
    }

    @Test
    void goodCreateSession() {
        ChargingSession expectedSession = new ChargingSession(uuid, stationIdOne, createdTimestamp);
        ChargingSession createdSession = sessionService.createSession(uuid, stationIdOne, createdTimestamp);
        assertEquals(expectedSession, createdSession);
        verify(chargingSessionRepo, times(1)).save(createdSession);
        verify(sessionStatisticsRepo, times(1)).addSessionStart(createdTimestamp);
    }

    @Test
    void badCreateSession() {
        assertThrows(IllegalArgumentException.class, () ->
                sessionService.createSession(uuid, null, createdTimestamp)
        );
        verify(chargingSessionRepo, times(0)).save(any(ChargingSession.class));
        verify(sessionStatisticsRepo, times(0)).addSessionStart(createdTimestamp);
    }

    @Test
    void goodStopSession() {
        ChargingSession existingSession = new ChargingSession(uuid, stationIdOne, createdTimestamp);
        when(chargingSessionRepo.findById(not(eq(uuid)))).thenReturn(Optional.empty());
        when(chargingSessionRepo.findById(uuid)).thenReturn(Optional.of(existingSession));
        when(chargingSessionRepo.save(existingSession)).thenReturn(existingSession);

        ChargingSession returnedSession = sessionService.stopSession(uuid, stoppedTimestamp);
        assertAll("Checking Returned Session",
                () -> assertEquals(uuid, returnedSession.getId()),
                () -> assertEquals(stationIdOne, returnedSession.getStationId()),
                () -> assertEquals(createdTimestamp, returnedSession.getStartedAt()),
                () -> assertEquals(FINISHED, returnedSession.getStatus()),
                () -> assertEquals(stoppedTimestamp, returnedSession.getStoppedAt())
        );
        verify(sessionStatisticsRepo, times(1)).addSessionStop(stoppedTimestamp);
    }

    @Test
    void badStopSession() {
        when(chargingSessionRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

        ChargingSession returnedSession = sessionService.stopSession(uuid, stoppedTimestamp);
        assertNull(returnedSession);
        verify(sessionStatisticsRepo, times(0)).addSessionStop(stoppedTimestamp);
        verify(chargingSessionRepo, times(0)).save(any(ChargingSession.class));
    }

    // This test is a bit of a truism due to the tested function currently
    // only just turning around to a (mocked) repo but has been included
    // for the sake of completeness in testing all endpoints of this service.
    @Test
    void getSessions() {
        ChargingSession sessionOne = new ChargingSession(uuid, stationIdOne, createdTimestamp);
        String stationIdTwo = "ABC-54321";
        ChargingSession sessionTwo = new ChargingSession(uuid, stationIdTwo, createdTimestamp);

        when(chargingSessionRepo.findAll()).thenReturn(Arrays.asList(sessionOne, sessionTwo));

        List<ChargingSession> sessionsList = sessionService.getSessions();
        assertEquals(2, sessionsList.size());
        assertEquals(sessionOne, sessionsList.get(0));
        assertEquals(sessionTwo, sessionsList.get(1));
    }

    @Test
    void getSummary() {
        long sessionStartedCount = 2L;
        long sessionEndedCount = 1L;
        when(sessionStatisticsRepo.getSessionStartCount(any(Instant.class), any(Instant.class)))
                .thenReturn(0L);
        when(sessionStatisticsRepo.getSessionStartCount(createdTimestamp, stoppedTimestamp))
                .thenReturn(sessionStartedCount);
        when(sessionStatisticsRepo.getSessionStopCount(any(Instant.class), any(Instant.class)))
                .thenReturn(0L);
        when(sessionStatisticsRepo.getSessionStopCount(createdTimestamp, stoppedTimestamp))
                .thenReturn(sessionEndedCount);

        Summary summary = sessionService.getSummary(createdTimestamp, stoppedTimestamp);
        assertEquals(new Summary(sessionStartedCount, sessionEndedCount), summary);
    }

}
