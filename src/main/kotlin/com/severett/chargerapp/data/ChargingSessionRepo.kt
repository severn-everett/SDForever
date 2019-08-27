package com.severett.chargerapp.data

import com.severett.chargerapp.model.ChargingSession
import java.util.*

interface ChargingSessionRepo {

    fun save(chargingSession: ChargingSession): ChargingSession

    fun findById(id: UUID): Optional<ChargingSession>

    fun findAll(): Iterable<ChargingSession>

}
