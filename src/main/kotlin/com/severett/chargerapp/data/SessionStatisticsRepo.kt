package com.severett.chargerapp.data

import java.time.Instant

interface SessionStatisticsRepo {

    fun addSessionStart(timestamp: Instant)

    fun addSessionStop(timestamp: Instant)

    fun getSessionStartCount(fromTimestamp: Instant, toTimestamp: Instant): Long

    fun getSessionStopCount(fromTimestamp: Instant, toTimestamp: Instant): Long

}
