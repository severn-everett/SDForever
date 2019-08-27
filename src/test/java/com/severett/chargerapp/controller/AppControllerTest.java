package com.severett.chargerapp.controller;

import com.severett.chargerapp.model.ChargerRequest;
import com.severett.chargerapp.model.ChargingSession;
import com.severett.chargerapp.model.Summary;
import com.severett.chargerapp.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

class AppControllerTest {

    private AppController appController;
    private SessionService sessionService;

    private UUID uuidOne = UUID.fromString("1b14873e-a383-4e90-9554-8879b2a6637b");
    private UUID uuidTwo = UUID.fromString("bb946cb6-055c-4305-a82f-d86ea3261341");
    private UUID uuidThree = UUID.fromString("8f129ccc-b62a-45e3-8d83-34f3e143ff8e");

    private String stationOneId = "ABC-12345";
    private String stationTwoId = "ABC-54321";

    private Instant createdTimestamp = Instant.ofEpochSecond(1565516634L);

    private ChargingSession sessionOne = new ChargingSession(uuidOne, stationOneId, createdTimestamp);
    private ChargingSession sessionTwo = new ChargingSession(uuidTwo, stationTwoId, createdTimestamp);

    private List<ChargingSession> sessionsList = Arrays.asList(sessionOne, sessionTwo);

    private Summary summary = new Summary(2, 1);

    AppControllerTest() {
        Instant stoppedTimestamp = createdTimestamp.plusSeconds(30L);
        sessionTwo.stopCharging(stoppedTimestamp);
    }

    @BeforeEach
    void setup() {
        sessionService = Mockito.mock(SessionService.class);

        when(sessionService.createSession(any(UUID.class), eq(null), any(Instant.class)))
                .thenThrow(IllegalArgumentException.class);
        when(sessionService.createSession(any(UUID.class), eq(stationOneId), any(Instant.class))).thenReturn(sessionOne);
        when(sessionService.stopSession(eq(uuidTwo), any(Instant.class))).thenReturn(sessionTwo);
        when(sessionService.getSessions()).thenReturn(sessionsList);
        when(sessionService.getSummary(any(Instant.class), any(Instant.class))).thenReturn(summary);

        appController = new AppController(sessionService);
    }

    @Test
    void goodSubmitChargingSession() {
        ResponseEntity<ChargingSession> response =
                appController.submitChargingSession(new ChargerRequest(stationOneId));
        checkResponse(response, OK, sessionOne);
    }

    @Test
    void badSubmitChargingSession() {
        ResponseEntity<ChargingSession> response =
                appController.submitChargingSession(new ChargerRequest(null));
        checkResponse(response, BAD_REQUEST, null);
    }

    @Test
    void failedSubmitChargingSession() {
        when(sessionService.createSession(any(UUID.class), anyString(), any(Instant.class))).then(invocation -> {
            throw new Exception("Something bad happened");
        });
        ResponseEntity<ChargingSession> response =
                appController.submitChargingSession(new ChargerRequest(stationOneId));
        checkResponse(response, INTERNAL_SERVER_ERROR, null);
    }

    @Test
    void goodStopSession() {
        ResponseEntity<ChargingSession> response = appController.stopChargingSession(uuidTwo.toString());
        checkResponse(response, OK, sessionTwo);
    }

    @Test
    void badStopSession() {
        //noinspection ConstantConditions
        when(sessionService.stopSession(not(eq(uuidTwo)), any(Instant.class)))
                .thenThrow(IllegalArgumentException.class);
        ResponseEntity<ChargingSession> response = appController.stopChargingSession(uuidThree.toString());
        checkResponse(response, BAD_REQUEST, null);
    }

    @Test
    void failedStopSession() {
        when(sessionService.stopSession(any(UUID.class), any(Instant.class))).then(invocation -> {
            throw new Exception("Something bad happened");
        });
        ResponseEntity<ChargingSession> response = appController.stopChargingSession(uuidTwo.toString());
        checkResponse(response, INTERNAL_SERVER_ERROR, null);
    }

    @Test
    void getSessions() {
        ResponseEntity<List<ChargingSession>> response = appController.getChargingSessions();
        checkResponse(response, OK, sessionsList);
    }

    @Test
    void failedGetSessions() {
        when(sessionService.getSessions()).then(invocation -> {
            throw new Exception("Something bad happened");
        });
        ResponseEntity<List<ChargingSession>> response = appController.getChargingSessions();
        checkResponse(response, INTERNAL_SERVER_ERROR, null);
    }

    @Test
    void getSummary() {
        ResponseEntity<Summary> response = appController.getSummary();
        checkResponse(response, OK, summary);
    }

    @Test
    void failedGetSummary() {
        when(sessionService.getSummary(any(Instant.class), any(Instant.class))).then(invocation -> {
            throw new Exception("Something bad happened");
        });
        ResponseEntity<Summary> response = appController.getSummary();
        checkResponse(response, INTERNAL_SERVER_ERROR, null);
    }

    private <T> void checkResponse(ResponseEntity<T> response, HttpStatus expectedStatus, T expectedBody) {
        assertAll("Response Entity Verification",
                () -> assertEquals(expectedStatus, response.getStatusCode()),
                () -> assertEquals(expectedBody, response.getBody())
        );
    }

}
