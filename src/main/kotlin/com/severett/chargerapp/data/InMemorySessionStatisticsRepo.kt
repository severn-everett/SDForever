package com.severett.chargerapp.data

import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemorySessionStatisticsRepo : SessionStatisticsRepo {

    private val startSessionCounter = ConcurrentHashMap<Long, Long>()

    private val stopSessionCounter = ConcurrentHashMap<Long, Long>()

    override fun addSessionStart(timestamp: Instant) {
        startSessionCounter.compute(timestamp.epochSecond) { _, oldValue ->
            oldValue?.let { it + 1L } ?: 1L
        }
    }

    override fun addSessionStop(timestamp: Instant) {
        stopSessionCounter.compute(timestamp.epochSecond) { _, oldValue ->
            oldValue?.let { it + 1L } ?: 1L
        }
    }

    override fun getSessionStartCount(fromTimestamp: Instant, toTimestamp: Instant): Long =
        getCountAggregate(startSessionCounter, fromTimestamp.epochSecond, toTimestamp.epochSecond)

    override fun getSessionStopCount(fromTimestamp: Instant, toTimestamp: Instant): Long =
        getCountAggregate(stopSessionCounter, fromTimestamp.epochSecond, toTimestamp.epochSecond)

    fun getCountAggregate(counter: Map<Long, Long>, fromEpoch: Long, toEpoch: Long): Long {
        return (fromEpoch..toEpoch).fold(0L) { retVal, currentEpoch ->
            retVal + counter.getOrDefault(currentEpoch, 0L)
        }
    }

}
