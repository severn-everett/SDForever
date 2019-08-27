package com.severett.chargerapp.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.severett.chargerapp.util.NoArgs
import java.time.Instant
import java.util.*

@NoArgs
class ChargingSession(
    var id: UUID,
    var stationId: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC")
    var startedAt: Instant
) {

    var status = Status.IN_PROGRESS

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC")
    var stoppedAt: Instant? = null

    fun stopCharging(stoppedAt: Instant) {
        status = Status.FINISHED
        this.stoppedAt = stoppedAt
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChargingSession

        if (id != other.id) return false
        if (stationId != other.stationId) return false
        if (startedAt != other.startedAt) return false
        if (status != other.status) return false
        if (stoppedAt != other.stoppedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + stationId.hashCode()
        result = 31 * result + startedAt.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + (stoppedAt?.hashCode() ?: 0)
        return result
    }

}
