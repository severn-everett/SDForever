package com.severett.chargerapp.service

import com.severett.chargerapp.model.ChargingSession
import com.severett.chargerapp.model.Summary
import java.time.Instant
import java.util.*

interface SessionService {

    fun createSession(id: UUID, stationId: String?, timestamp: Instant): ChargingSession

    fun stopSession(id: UUID, timestamp: Instant): ChargingSession?

    fun getSessions(): List<ChargingSession>

    fun getSummary(fromTimestamp: Instant, toTimestamp: Instant): Summary

}
