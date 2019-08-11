# SDForever

SDForever is the working name for a web service that allows users to conduct a variety of operations related to charging sessions for electric vehicles.

## Getting Started

### Prerequisites

Gradle and Java 8+ need to be installed to build this project.

### Installing

The project can be built and run in one step using the `bootRun` task via Gradle:

```
gradle bootRun
```

Alternatively, the project can be built on its own:

```
gradle build
```

Whereupon it will can be executed as a Java program by running the resulting standalone jar.

```
java -jar build/libs/ChargerService-1.0-SNAPSHOT.jar
```

As this is a Spring Boot application, configuration options can be provided for port, logging, etc
by either specifying a configuration file or by passing in configuration options directly as system parameters.
Please read [here](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) for more details.

## API

The following API endpoints are available:

| Endpoint | Description | Request Body | Response Body |
| --- | --- | --- | --- |
| POST /chargingSessions | Submit a new charging session for the station | {<br>&nbsp;&nbsp;"stationID":<br>&nbsp;&nbsp;"abc-12345"<br>} | {<br>&nbsp;&nbsp;"id": "d9bb7458-d5d9-4de7-87f7-7f39edd51d18",<br>&nbsp;&nbsp;"stationId": "ABC-12345",<br>&nbsp;&nbsp;"startedAt": "2019-05-06T19:00:20.529",<br>&nbsp;&nbsp;"status": "IN_PROGRESS"<br>} |
| PUT /chargingSessions/{id} | Stop charging session | | {<br>&nbsp;&nbsp;"id": "d9bb7458-d5d9-4de7-87f7-7f39edd51d18",<br>&nbsp;&nbsp;"stationId": "ABC-12345",<br>&nbsp;&nbsp;"startedAt": "2019-05-06T21:15:01.198",<br>&nbsp;&nbsp;"stoppedAt": "2019-05-06T21:17:01.198",<br>&nbsp;&nbsp;"status": "FINISHED"<br>} |
| GET /chargingSessions | Retrieve all charging sessions | | \[<br>&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;"id": "d9bb7458-d5d9-4de7-87f7-7f39edd51d18",<br>&nbsp;&nbsp;&nbsp;&nbsp;"stationId": "ABC-12345",<br>&nbsp;&nbsp;&nbsp;&nbsp;"startedAt": "2019-05-06T19:00:20.529",<br>&nbsp;&nbsp;&nbsp;&nbsp;"status": "IN_PROGRESS"<br>&nbsp;&nbsp;},<br>&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;"id": "69acaf1b-743f-47df-9339-abe50998b201",<br>&nbsp;&nbsp;&nbsp;&nbsp;"stationId": "ABC-12346",<br>&nbsp;&nbsp;&nbsp;&nbsp;"startedAt": 2019-05-06T19:01:35.245,<br>&nbsp;&nbsp;&nbsp;&nbsp;"stoppedAt": "2019-05-06T21:17:01.198",<br>&nbsp;&nbsp;&nbsp;&nbsp;"status": "FINISHED"<br>&nbsp;&nbsp;}<br>] |
| GET /chargingSessions/summary | Retrieve a summary of submitted charging sessions including:<br><br><ul><li><i>totalCount</i> – total number of charging session updates for the last minute</li><li><i>startedCount</i> – total number of started charging sessions for the last minute</li><li><i>stoppedCount</i> – total number of stopped charging sessions for the last minute</li></ul> | | {<br>&nbsp;&nbsp;"totalCount: 5,<br>&nbsp;&nbsp;"startedCount": 1<br>&nbsp;&nbsp;"stoppedCount": 4<br>} |

All endpoints return an HTTP Status code of:
* `200` when returning the expected result.
* `400` when a bad argument has been passed into the endpoint (e.g. invalid id).
* `500` when an unexpected internal error has occurred.

## Running the tests

The automated unit and integration tests for this project can be run using the `test` task in Gradle:

```
gradle test
```

### Unit Tests

Tests have been written for the entities within the web application that contain non-trivial logic:

* App Controller
* Session Statistics Repository
* Session Service

### Integration Tests

Integration tests have been written to validate the "happy path" for using the web service as well as the most likely failure scenarios:

| Test Description | Type |
| --- | --- |
| User initializes charging sessions for different stations | Positive Test |
| User initializes charging sessions for the same station | Positive Test |
| Attempting to create a session with no Station ID | Negative Test |
| Attempt to stop a session that does not exist | Negative Test |

## Notes

* The ConcurrentHashMap data structure for storing the various data types in-memory - the charging sessions
and the session start/stop statistics - has a normal computational performance of O(1) for adding/accessing/removing
one value (and O(n) for accessing all values). Thanks to the
[performance improvements](https://www.javacodegeeks.com/2014/04/hashmap-performance-improvements-in-java-8.html)
conducted on HashMap in Java 8, worst-case performance of adding/accessing/removing one value is O(log(n)),
as the underlying data structure holding the keys is transitioned from hash buckets to a binary tree.
* The data repositories have been designed as such that Spring's autowiring should be able to swap in an actual
persistence layer to save the data to disk, e.g. Postgresql. This would be conducted by creating an additional
implementation to the data repository interfaces and differentiating between the in-memory/on-disk data repository
implementations with the `@Qualifier` Spring annotation.

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
* [JUnit](https://junit.org/junit5/) - The unit test framework used
* [Cucumber](https://cucumber.io/) - The integration test framework used
* [Gradle](https://gradle.org/) - Dependency Management

## Author

* **Severn Everett**

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
