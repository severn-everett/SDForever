package com.severett.chargerapp.model;

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

}
