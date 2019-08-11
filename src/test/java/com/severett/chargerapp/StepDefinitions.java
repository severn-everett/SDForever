package com.severett.chargerapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.severett.chargerapp.model.ChargingSession;
import com.severett.chargerapp.model.Summary;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ContextConfiguration(classes = Main.class)
@WebAppConfiguration
@AutoConfigureMockMvc
@DirtiesContext
public class StepDefinitions implements En {

    private final Logger logger = LoggerFactory.getLogger(StepDefinitions.class);

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Integer, Integer> responseCounter = new HashMap<>();

    private final Map<String, List<ChargingSession>> createdSessionsMap = new ConcurrentHashMap<>();
    private final Map<String, List<ChargingSession>> stoppedSessionsMap = new ConcurrentHashMap<>();

    private final List<ChargingSession> totalSessionsList = new ArrayList<>();
    private Summary summary;

    public StepDefinitions() {
        // This is necessary to deserialize instances of java.time.Instant
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Before(() -> {
            responseCounter.clear();
            createdSessionsMap.clear();
            totalSessionsList.clear();
            summary = null;
        });

        When("I create charging sessions with the following parameters:", (DataTable rows) -> {
            URI uri = URI.create("/chargingSessions");
            List<Map<String, String>> paramsList = rows.asMaps();
            paramsList.forEach(requestParams -> {
                ObjectNode bodyParams = objectMapper.createObjectNode();
                bodyParams.put("stationId", requestParams.get("stationId"));
                logger.debug("Request params: {}", requestParams.toString());
                try {
                    logger.debug("Body Params: {}", bodyParams.toString());
                    mvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON)
                            .content(bodyParams.toString()))
                            .andDo(result -> {
                                MockHttpServletResponse response = result.getResponse();
                                logger.debug(
                                        "Received result {} for request to {}",
                                        response.getStatus(),
                                        uri
                                );
                                responseCounter.compute(
                                        response.getStatus(), (key, value) -> value != null ? value + 1 : 1
                                );
                                ChargingSession session = objectMapper.readValue(
                                        response.getContentAsString(), ChargingSession.class
                                );
                                createdSessionsMap.computeIfAbsent(
                                        session.getStationId(),
                                        (stationId) -> new ArrayList<>()
                                ).add(session);
                            });
                } catch (Exception e) {
                    fail(e);
                }
            });
        });

        When("I stop the charging sessions with the following station ids:", (DataTable rows) -> {
            List<Map<String, String>> paramsList = rows.asMaps();
            paramsList.forEach(requestParams -> {
                String stationId = requestParams.get("stationId");
                if (!createdSessionsMap.containsKey(stationId)) {
                    fail("No created sessions for station '" + stationId + "'");
                }
                createdSessionsMap.get(stationId).forEach(chargingSession -> {
                    URI uri = URI.create("/chargingSessions/" + chargingSession.getId().toString());
                    try {
                        mvc.perform(put(uri)).andDo(result -> {
                            MockHttpServletResponse response = result.getResponse();
                            logger.debug(
                                    "Received result {} for request to {}",
                                    response.getStatus(),
                                    uri
                            );
                            responseCounter.compute(
                                    response.getStatus(), (key, value) -> value != null ? value + 1 : 1
                            );
                            ChargingSession session = objectMapper.readValue(
                                    response.getContentAsString(), ChargingSession.class
                            );
                            stoppedSessionsMap.computeIfAbsent(
                                    session.getStationId(),
                                    (givenId) -> new ArrayList<>()
                            ).add(session);
                        });
                    } catch (Exception e) {
                        fail(e);
                    }
                });
            });
        });

        When("I request the list of all sessions", () -> {
            MockHttpServletResponse getListResponse =
                    mvc.perform(get(URI.create("/chargingSessions"))).andReturn().getResponse();
            assertEquals(HttpStatus.OK.value(), getListResponse.getStatus());
            JsonNode responseContent = objectMapper.readTree(getListResponse.getContentAsString());
            responseContent.forEach(entry -> {
                try {
                    totalSessionsList.add(objectMapper.treeToValue(entry, ChargingSession.class));
                } catch (JsonProcessingException e) {
                    fail(e);
                }
            });
        });

        When("I request the summary of the sessions", () -> {
            MockHttpServletResponse getSummaryResponse =
                    mvc.perform(get(URI.create("/chargingSessions/summary"))).andReturn().getResponse();
            assertEquals(HttpStatus.OK.value(), getSummaryResponse.getStatus());
            summary = objectMapper.readValue(getSummaryResponse.getContentAsString(), Summary.class);
        });

        And("I wait {int} second(s)", (Integer amt) ->
            Thread.sleep(amt.longValue() * 1000L)
        );

        Then("there should be {int} responses of status code {int}", (Integer expectedCount, Integer codeNumber) -> {
            int actualCount = responseCounter.getOrDefault(codeNumber, 0);
            assertEquals(
                    expectedCount,
                    actualCount,
                    () -> String.format(
                            "Expected %d occurrences of status code %d, instead found %d",
                            expectedCount,
                            codeNumber,
                            actualCount)
            );
        });

        Then("there should be the following amount of created sessions:", (DataTable dataTable) ->
            checkSessionsMap(dataTable.asMaps(), createdSessionsMap)
        );

        Then("there should be the following amount of stopped sessions:", (DataTable dataTable) ->
                checkSessionsMap(dataTable.asMaps(), stoppedSessionsMap)
        );

        Then("there should be a list of {int} sessions", (Integer sessionCount) ->
                assertEquals(sessionCount, totalSessionsList.size())
        );

        Then("I should get a summary with the following statistics:", (DataTable dataTable) -> {
            assertNotNull(summary);
            Map<String, String> statsMap = dataTable.asMaps().get(0);
            int startedCount = Integer.parseInt(statsMap.get("startedCount"));
            int stoppedCount = Integer.parseInt(statsMap.get("stoppedCount"));
            int totalCount = Integer.parseInt(statsMap.get("totalCount"));
            assertAll("Check Summary contents",
                    () -> assertEquals(startedCount, summary.getStartedCount()),
                    () -> assertEquals(stoppedCount, summary.getStoppedCount()),
                    () -> assertEquals(totalCount, summary.getTotalCount())
            );
        });
    }

    private void checkSessionsMap(List<Map<String, String>> sessionRows, Map<String,
            List<ChargingSession>> targetSessionMap) {
        sessionRows.forEach(row -> {
            String stationId = row.get("stationId");
            int expectedCount = Integer.parseInt(row.get("count"));
            int actualCount = targetSessionMap.containsKey(stationId) ? targetSessionMap.get(stationId).size() : 0;
            assertEquals(expectedCount, actualCount, () -> String.format(
                    "Expected %d instances of '%s' only found %d",
                    expectedCount, stationId, actualCount
            ));
        });
    }

}
