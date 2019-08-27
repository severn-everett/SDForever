package com.severett.chargerapp.controller

import com.severett.chargerapp.model.ChargerRequest
import com.severett.chargerapp.model.ChargingSession
import com.severett.chargerapp.model.Summary
import com.severett.chargerapp.service.SessionService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/chargingSessions", produces = [MediaType.APPLICATION_JSON_VALUE])
class AppController(
    private val sessionService: SessionService
) {

    private val logger = LoggerFactory.getLogger(AppController::class.java)

    @RequestMapping(method = [RequestMethod.POST])
    fun submitChargingSession(@RequestBody chargerRequest: ChargerRequest): ResponseEntity<ChargingSession> {
        return try {
            ResponseEntity.ok(sessionService.createSession(UUID.randomUUID(), chargerRequest.stationId, Instant.now()))
        } catch (iae: IllegalArgumentException) {
            logger.warn("Bad request received for submitChargingSession():", iae)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            logger.error("Exception encountered in submitChargingSession():", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PUT])
    fun stopChargingSession(@PathVariable id: String): ResponseEntity<ChargingSession> {
        return try {
            sessionService.stopSession(UUID.fromString(id), Instant.now())
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (iae: IllegalArgumentException) {
            logger.warn("Bad request received for stopChargingSession():", iae)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            logger.error("Exception encountered in stopChargingSession():", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @RequestMapping(method = [RequestMethod.GET])
    fun getChargingSessions(): ResponseEntity<List<ChargingSession>> {
        return try {
            ResponseEntity.ok(sessionService.getSessions())
        } catch (e: Exception) {
            logger.error("Exception encountered in getChargingSessions():", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @RequestMapping(value = ["/summary"], method = [RequestMethod.GET])
    fun getSummary(): ResponseEntity<Summary> {
        return try {
            val toTimestamp = Instant.now()
            val fromTimestamp = toTimestamp.minusSeconds(59L)
            ResponseEntity.ok(sessionService.getSummary(fromTimestamp, toTimestamp))
        } catch (e: Exception) {
            logger.error("Exception encountered in getSummary():", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}
