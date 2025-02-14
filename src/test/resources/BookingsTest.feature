Feature: Bookings Application Additional Tests

  Scenario: Testing a call to the server to request a payments with not validation issues
    When the client calls /payments_with_quality_check to get a list of payments
    And with the following portal payment data
      | reference | amount | amount_received | country_from | sender_full_name | sender_address | school  | student_id | email              |
      | 12345     | 2000   | 2060            | USA          | John Smith       |                | Rutgers | 123        | johnsmith@some.com |
    And Call is made to the booking application
    Then the client receives the following payment response for a single record
      | reference | amount | amountWithFees | amountReceived | qualityCheck | overPayment | underPayment |
      | 12345     | 2000   | 2060           | 2060           |              | false       | false        |

  Scenario: Testing a call to the server to request a payments with overpayment
    When the client calls /payments_with_quality_check to get a list of payments
    And with the following portal payment data
      | reference | amount | amount_received | country_from | sender_full_name | sender_address | school  | student_id | email              |
      | 12345     | 2000   | 2100            | USA          | John Smith       |                | Rutgers | 123        | johnsmith@some.com |
    And Call is made to the booking application
    Then the client receives the following payment response for a single record
      | reference | amount | amountWithFees | amountReceived | qualityCheck | overPayment | underPayment |
      | 12345     | 2000   | 2060           | 2100           |              | true        | false        |

  Scenario: Testing a call to the server to request a payments with email invalid quality check
    When the client calls /payments_with_quality_check to get a list of payments
    And with the following portal payment data
      | reference | amount | amount_received | country_from | sender_full_name | sender_address | school  | student_id | email            |
      | 12345     | 2000   | 1000            | USA          | John Smith       |                | Rutgers | 123        | johnsmithsomecom |
    And Call is made to the booking application
    Then the client receives the following payment response for a single record
      | reference | amount | amountWithFees | amountReceived | qualityCheck | overPayment | underPayment |
      | 12345     | 2000   | 2060           | 1000           | InvalidEmail | false       | true         |

  Scenario: Testing a call to the server to request a payments with amount threshold exceede quality check
    When the client calls /payments_with_quality_check to get a list of payments
    And with the following portal payment data
      | reference | amount | amount_received | country_from | sender_full_name | sender_address | school  | student_id | email              |
      | 12345     | 2000   | 111100000       | USA          | John Smith       |                | Rutgers | 123        | johnsmith@some.com |
    And Call is made to the booking application
    Then the client receives the following payment response for a single record
      | reference | amount | amountWithFees | amountReceived | qualityCheck    | overPayment | underPayment |
      | 12345     | 2000   | 2060           | 111100000      | AmountThreshold | true        | false        |

  Scenario: Testing a call to the server to request a payments with duplicate payment quality check
    When the client calls /payments_with_quality_check to get a list of payments
    And with the following portal payment data
      | reference | amount | amount_received | country_from | sender_full_name | sender_address | school  | student_id | email              |
      | 12345     | 2000   | 2060            | USA          | John Smith       |                | Rutgers | 123        | johnsmith@some.com |
    And Call is made to the booking application
    Then the client receives the following payment response for a single record
      | reference | amount | amountWithFees | amountReceived | qualityCheck | overPayment | underPayment |
      | 12345     | 2000   | 2060           | 2060           |              | false       | false        |

  Scenario: Testing a call to the server to request a payments with duplicate payment found qaulity check
    When the client calls /payments_with_quality_check to get a list of payments
    And with the following portal payment data
      | reference | amount | amount_received | country_from | sender_full_name | sender_address | school  | student_id | email              |
      | 12345     | 2000   | 2060            | USA          | John Smith       |                | Rutgers | 123        | johnsmith@some.com |
    And with the following portal payment data
      | reference | amount | amount_received | country_from | sender_full_name | sender_address | school  | student_id | email              |
      | 987654    | 2000   | 2060            | USA          | John Smith       |                | Rutgers | 123        | johnsmith@some.com |
    And Call is made to the booking application
    Then the client receives the following payment response for a single record
      | reference | amount | amountWithFees | amountReceived | qualityCheck      | overPayment | underPayment |
      | 12345     | 2000   | 2060           | 2060           | DuplicatedPayment | false       | false        |

  Scenario: Testing a call to the server to request a payments with invalid email, duplicate payment and amount threshold found qaulity check
    When the client calls /payments_with_quality_check to get a list of payments
    And with the following portal payment data
      | reference | amount | amount_received | country_from | sender_full_name | sender_address | school  | student_id | email            |
      | 12345     | 2000   | 111100000       | USA          | John Smith       |                | Rutgers | 123        | johnsmithsomecom |
    And with the following portal payment data
      | reference | amount | amount_received | country_from | sender_full_name | sender_address | school  | student_id | email            |
      | 987654    | 2000   | 111100000       | USA          | John Smith       |                | Rutgers | 123        | johnsmithsomecom |
    And Call is made to the booking application
    Then the client receives the following payment response for a single record
      | reference | amount | amountWithFees | amountReceived | qualityCheck                                   | overPayment | underPayment |
      | 12345     | 2000   | 2060           | 111100000      | InvalidEmail,DuplicatedPayment,AmountThreshold | true        | false        |
