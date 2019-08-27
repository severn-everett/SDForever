package com.severett.chargerapp.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.severett.chargerapp.util.NoArgs

@NoArgs
@JsonIgnoreProperties(value = ["totalCount"], allowGetters = true)
class Summary(var startedCount: Long, var stoppedCount: Long) {

    val totalCount: Long
        get() = startedCount + stoppedCount

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Summary

        if (startedCount != other.startedCount) return false
        if (stoppedCount != other.stoppedCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startedCount.hashCode()
        result = 31 * result + stoppedCount.hashCode()
        return result
    }

}
