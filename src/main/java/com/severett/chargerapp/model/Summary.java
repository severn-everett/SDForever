package com.severett.chargerapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(value={ "totalCount" }, allowGetters=true)
public class Summary {

    private long startedCount;
    private long stoppedCount;

    public Summary() {
    }

    public Summary(long startedCount, long stoppedCount) {
        this.startedCount = startedCount;
        this.stoppedCount = stoppedCount;
    }

    public long getStartedCount() {
        return startedCount;
    }

    public void setStartedCount(long startedCount) {
        this.startedCount = startedCount;
    }

    public long getStoppedCount() {
        return stoppedCount;
    }

    public void setStoppedCount(long stoppedCount) {
        this.stoppedCount = stoppedCount;
    }

    public long getTotalCount() {
        return startedCount + stoppedCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Summary summary = (Summary) o;
        return startedCount == summary.startedCount &&
                stoppedCount == summary.stoppedCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startedCount, stoppedCount);
    }
}
