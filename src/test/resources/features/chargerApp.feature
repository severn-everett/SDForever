Feature: Validating the functionality of the Charger App

  @Positive
  Scenario: User initializes charging sessions for different stations
    When I create charging sessions with the following parameters:
      | stationId |
      | abc-11111 |
      | abc-22222 |
      | abc-33333 |
      | abc-44444 |
      | abc-55555 |
    And I wait 1 second

    Then there should be 5 responses of status code 200
    # Note: The content of the responses is not fully tested due to the random
    #       nature of the id and time values making it impossible to test in a
    #       black-box test such as this; the generation of these values is
    #       covered in the unit tests.
    And there should be the following amount of created sessions:
      | stationId | count |
      | abc-11111 | 1     |
      | abc-22222 | 1     |
      | abc-33333 | 1     |
      | abc-44444 | 1     |
      | abc-55555 | 1     |

    When I stop the charging sessions with the following station ids:
      | stationId |
      | abc-11111 |
      | abc-22222 |
    And I wait 1 second
    Then there should be 7 responses of status code 200
    # Note: The content of the responses is not fully tested due to the random
    #       nature of the id and time values making it impossible to test in a
    #       black-box test such as this; the generation of these values is
    #       covered in the unit tests.
    And there should be the following amount of stopped sessions:
      | stationId | count |
      | abc-11111 | 1     |
      | abc-22222 | 1     |
      | abc-33333 | 0     |
      | abc-44444 | 0     |
      | abc-55555 | 0     |

    When I request the list of all sessions
    # Note: The content of the responses is not fully tested due to the random
    #       nature of the id and time values making it impossible to test in a
    #       black-box test such as this; the generation of these values is
    #       covered in the unit tests.
    Then there should be a list of 5 sessions

    When I request the summary of the sessions
    Then I should get a summary with the following statistics:
      | startedCount | stoppedCount | totalCount |
      | 5            | 2            | 7          |

  @Positive
  Scenario: User initializes charging sessions for the same station
    When I create charging sessions with the following parameters:
      | stationId |
      | abc-11111 |
      | abc-11111 |
      | abc-22222 |
      | abc-22222 |
    And I wait 1 second

    Then there should be 4 responses of status code 200
    # Note: The content of the responses is not fully tested due to the random
    #       nature of the id and time values making it impossible to test in a
    #       black-box test such as this; the generation of these values is
    #       covered in the unit tests.
    And there should be the following amount of created sessions:
      | stationId | count |
      | abc-11111 | 2     |
      | abc-22222 | 2     |

    When I stop the charging sessions with the following station ids:
      | stationId |
      | abc-11111 |
    And I wait 1 second
    Then there should be 6 responses of status code 200
    # Note: The content of the responses is not fully tested due to the random
    #       nature of the id and time values making it impossible to test in a
    #       black-box test such as this; the generation of these values is
    #       covered in the unit tests.
    And there should be the following amount of stopped sessions:
      | stationId | count |
      | abc-11111 | 2     |
      | abc-22222 | 0     |

    When I request the list of all sessions
    # Note: The content of the responses is not fully tested due to the random
    #       nature of the id and time values making it impossible to test in a
    #       black-box test such as this; the generation of these values is
    #       covered in the unit tests.
    Then there should be a list of 4 sessions

    When I request the summary of the sessions
    Then I should get a summary with the following statistics:
      | startedCount | stoppedCount | totalCount |
      | 4            | 2            | 6          |

  @Negative
  Scenario: Attempting to create a session with no Station ID
    When I create a charging session with no Station ID
    Then there should be 1 response of status code 400

  @Negative
  Scenario: Attempt to stop a session that does not exist
    When I have a ficticious session with Station ID "bad-11111"
    And I stop the charging sessions with the following station ids:
      | stationId |
      | bad-11111 |

    Then there should be 1 response of status code 400
