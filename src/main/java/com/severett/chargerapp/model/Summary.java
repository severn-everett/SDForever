package com.severett.chargerapp.model;

import java.util.Objects;

public final class Summary {

    private final long startedCount;
    private final long stoppedCount;

    public Summary(long startedCount, long stoppedCount) {
        this.startedCount = startedCount;
        this.stoppedCount = stoppedCount;
    }

    public long getStartedCount() {
        return startedCount;
    }

    public long getStoppedCount() {
        return stoppedCount;
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
