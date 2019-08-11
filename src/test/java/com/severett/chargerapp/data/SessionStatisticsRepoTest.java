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

    // A group of timestamps that span ten seconds
    private Instant timestampOne = Instant.ofEpochSecond(1565516601L);
    private Instant timestampTwo = timestampOne.plusSeconds(1);
    private Instant timestampThree = timestampTwo.plusSeconds(1);
    private Instant timestampFour = timestampThree.plusSeconds(1);
    private Instant timestampFive = timestampFour.plusSeconds(1);
    private Instant timestampSix = timestampFive.plusSeconds(1);
    private Instant timestampSeven = timestampSix.plusSeconds(1);
    private Instant timestampEight = timestampSeven.plusSeconds(1);
    private Instant timestampNine = timestampEight.plusSeconds(1);
    private Instant timestampTen = timestampNine.plusSeconds(1);
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

        checkCounts(countProducer, sessionStatsAdded);
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
        checkCounts(countProducer, runThreadsList.size());
    }

    private void checkCounts(
            BiFunction<Instant, Instant, Long> countProducer,
            int expectedFullCount
    ) {
        // Simulate a query that ranges a minute that begins with timestampOne
        long sessionStatCount = countProducer.apply(
                timestampOne, timestampOne.plusSeconds(59)
        );
        assertEquals(expectedFullCount, sessionStatCount);
        // Simulate a query that ranges a minute that ends with timestampTwo
        long pastSessionsStatCount = countProducer.apply(
                timestampTwo.minusSeconds(59), timestampTwo
        );
        assertEquals(3, pastSessionsStatCount);
    }

}
