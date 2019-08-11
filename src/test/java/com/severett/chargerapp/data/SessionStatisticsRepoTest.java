package com.severett.chargerapp.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionStatisticsRepoTest {

    private Instant timestampOne = Instant.ofEpochSecond(1565516601L);
    private Instant timestampTwo = Instant.ofEpochSecond(1565516602L);
    private Instant timestampThree = Instant.ofEpochSecond(1565516603L);
    private Instant timestampFour = Instant.ofEpochSecond(1565516604L);
    private Instant timestampFive = Instant.ofEpochSecond(1565516605L);
    private Instant timestampSix = Instant.ofEpochSecond(1565516606L);
    private Instant timestampSeven = Instant.ofEpochSecond(1565516607L);
    private Instant timestampEight = Instant.ofEpochSecond(1565516608L);
    private Instant timestampNine = Instant.ofEpochSecond(1565516609L);
    private Instant timestampTen = Instant.ofEpochSecond(1565516610L);
    private List<Instant> timestampList = Arrays.asList(
            timestampOne, timestampTwo, timestampThree, timestampFour,
            timestampFive, timestampSix, timestampSeven, timestampEight,
            timestampNine, timestampTen
    );

    private SessionStatisticsRepo sessionStatisticsRepo;

    @BeforeEach
    void setup() {
        sessionStatisticsRepo = new InMemorySessionStatisticsRepo();
    }

    @Test
    void syncAddSessionStart() {
        testSync(sessionStatisticsRepo::addSessionStart, sessionStatisticsRepo::getSessionStartCount);
    }

    @Test
    void syncAddSessionStop() {
        testSync(sessionStatisticsRepo::addSessionStop, sessionStatisticsRepo::getSessionStopCount);
    }

    @Test
    void asyncAddSessionStart() throws InterruptedException {
        testAsync(sessionStatisticsRepo::addSessionStart, sessionStatisticsRepo::getSessionStartCount);
    }

    @Test
    void asyncAddSessionStop() throws InterruptedException {
        testAsync(sessionStatisticsRepo::addSessionStop, sessionStatisticsRepo::getSessionStopCount);
    }

    private void testSync(Consumer<Instant> timestampConsumer,
                          BiFunction<Instant, Instant, Long> countProducer) {
        int sessionStatsAdded = 0;
        for (int i = 0; i < timestampList.size(); i++) {
            Instant timestamp = timestampList.get(i);
            for (int j = 0; j <= i; j++) {
                timestampConsumer.accept(timestamp);
                sessionStatsAdded++;
            }
        }
        long sessionStatCount = countProducer.apply(
                timestampOne.minusSeconds(10), timestampOne.plusSeconds(49)
        );
        assertEquals(sessionStatsAdded, sessionStatCount);
    }

    private void testAsync(Consumer<Instant> timestampConsumer,
                           BiFunction<Instant, Instant, Long> countProducer) throws InterruptedException {
        List<Thread> runThreadsList = new ArrayList<>();
        for (int i = 0; i < timestampList.size(); i++) {
            Instant timestamp = timestampList.get(i);
            for (int j = 0; j <= i; j++) {
                Thread addThread = new Thread(() -> timestampConsumer.accept(timestamp));
                addThread.start();
                runThreadsList.add(addThread);
            }
        }
        for (Thread addThread : runThreadsList) {
            addThread.join();
        }
        long sessionStatCount = countProducer.apply(
                timestampOne.minusSeconds(10), timestampOne.plusSeconds(49)
        );
        assertEquals(runThreadsList.size(), sessionStatCount);
    }

}
