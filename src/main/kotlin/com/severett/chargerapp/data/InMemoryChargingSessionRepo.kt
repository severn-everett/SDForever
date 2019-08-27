package com.severett.chargerapp.data

import com.severett.chargerapp.model.ChargingSession
import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryChargingSessionRepo : ChargingSessionRepo {

    private val sessionMap = ConcurrentHashMap<UUID, ChargingSession>()

    override fun save(chargingSession: ChargingSession): ChargingSession {
        sessionMap[chargingSession.id] = chargingSession
        return chargingSession
    }

    override fun findById(id: UUID): Optional<ChargingSession> = Optional.ofNullable(sessionMap[id])

    override fun findAll(): Iterable<ChargingSession> = sessionMap.values

}
