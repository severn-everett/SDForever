package com.severett.chargerapp.service

import com.severett.chargerapp.data.ChargingSessionRepo
import com.severett.chargerapp.data.SessionStatisticsRepo
import com.severett.chargerapp.model.ChargingSession
import com.severett.chargerapp.model.Summary
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class SessionServiceImpl(
    private val chargingSessionRepo: ChargingSessionRepo,
    private val sessionStatisticsRepo: SessionStatisticsRepo
) : SessionService {

    override fun createSession(id: UUID, stationId: String?, timestamp: Instant): ChargingSession {
        if (stationId != null) {
            return ChargingSession(id, stationId, timestamp).also { newSession ->
                chargingSessionRepo.save(newSession)
                // Notify stats repo of session start only after session has been
                // successfully persisted
                sessionStatisticsRepo.addSessionStart(timestamp)
            }
        } else {
            throw IllegalArgumentException("stationId must not be null")
        }
    }

    override fun stopSession(id: UUID, timestamp: Instant): ChargingSession? {
        return chargingSessionRepo.findById(id).map { chargingSession ->
            chargingSession.stopCharging(timestamp)
            // Notify stats repo of session stop only after session has been
            // successfully stopped
            sessionStatisticsRepo.addSessionStop(timestamp)
            chargingSessionRepo.save(chargingSession)
        }.orElse(null)
    }

    override fun getSessions(): List<ChargingSession> = chargingSessionRepo.findAll().toList()

    override fun getSummary(fromTimestamp: Instant, toTimestamp: Instant): Summary {
        val sessionStartCount = sessionStatisticsRepo.getSessionStartCount(fromTimestamp, toTimestamp)
        val sessionStopCount = sessionStatisticsRepo.getSessionStopCount(fromTimestamp, toTimestamp)
        return Summary(sessionStartCount, sessionStopCount)
    }

}
