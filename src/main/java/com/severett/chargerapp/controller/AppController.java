package com.severett.chargerapp.controller;

import com.severett.chargerapp.model.ChargerRequest;
import com.severett.chargerapp.model.ChargingSession;
import com.severett.chargerapp.model.Summary;
import com.severett.chargerapp.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/chargingSessions")
public class AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    private final SessionService sessionService;

    public AppController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @RequestMapping(method = {POST}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<ChargingSession> submitChargingSession(
            @RequestBody ChargerRequest chargerRequest
    ) {
        try {
            return ResponseEntity.ok(sessionService.createSession(chargerRequest.getStationId()));
        } catch (IllegalArgumentException iae) {
            logger.warn("Bad request received for submitChargingSession:", iae);
            return new ResponseEntity<>(BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Exception encountered in submitChargingSession():", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/{id}", method = {PUT}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<ChargingSession> stopChargingSession(@PathVariable String id) {
        try {
            ChargingSession stoppedSession = sessionService.stopSession(id);
            return stoppedSession != null ?
                    ResponseEntity.ok(stoppedSession) :
                    new ResponseEntity<>(BAD_REQUEST);
        } catch (IllegalArgumentException iae) {
            logger.warn("Bad request received for stopChargingSession:", iae);
            return new ResponseEntity<>(BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Exception encountered in stopChargingSession():", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = {GET}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ChargingSession>> getChargingSessions() {
        try {
            return ResponseEntity.ok(sessionService.getSessions());
        } catch (Exception e) {
            logger.error("Exception encountered in getChargingSessions():", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/summary", method = {GET}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<Summary> getSummary() {
        try {
            return ResponseEntity.ok(sessionService.getSummary());
        } catch (Exception e) {
            logger.error("Exception encountered in getSummary():", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

}
