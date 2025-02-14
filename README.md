# Booking Application

## Note

This was a coding challenge that I executed for possible employment.  I am including the code that I wrote and the requirements in this repo as continued demostration of my coding abilities.  This client normally communicates with a Booking Portal that was provided for the challenge but not written by myself and not included here.  To see what the portal api provided you can look at how the wiremock cucumber tests respond as they were done to mimic the portal.

## Purpose

This was written to interface with the Booking Portal and provide API access to key pieces of date.

## Requirements and Notes

### Specifications

The requirements for this can be found at bottome of this README.

Here is a quick hit summary

* The application is meant as an API data resource to pull data records from the booking portal.
* One endpoint "/payments_with_quality_check" has been implemented to pull payment record with additional quality
  checks.
* The quality checks are searching for duplicate records, invalid email and amount threshold has been breached.
* The API will return a response entity including the HTTP status and a body containing the payments with checks
  records.
* The model of the payment with quality checks can be found
  at ([BookingsWithQualityCheck.java](src/main/com/payment/pra/coding/challenge/bookingsResponse/models/api/BookingsWithQualityCheck.java))

### Assumptions, Choices and Notes

This is a list of assumptions or choices made with respect to the requirments

* Application is named "Booking" because the exercise title was that however repo is named based on functionality which is payment retrieval.
* Decision (Assumption) that a payment is duplicated if it has the same school, same amount received and same student id
* Decision to add a status, HttpStatus and message at the top level of the response object to allow for a single object
  type for all responses allowing for upstream callers to not have to receive the object as a string and determine which
  type it is.
* Decision to check for valid email based on pattern and regex match because I could not find a definitive list of the dots domains (i.e. .com, .net. etc)
* Decision for cucumber testing, I implemented wiremock to simulate the downstream endpoint to allow for greater control over the tests and the scenarios for testing.
* Decision with unit testing to add cucumber feature file behavior testings to at least exercise an end to end test and hit the broadest set of conditions hopefully shaking out the applications.
* Note the feature file ([BookingsTest.feature](src/test/resources/BookingsTest.feature)) could be expanded to include more end to end test cases.

## Technical Details

### REST API Endpoints

There is one API in this application.

* Booking Payments with Quality Checks ("/payments_with_quality_check") this is to pull the current list of payments
  from the external API and determine a set of quality checks on the data providing the final result in the response

### Request Data

This is a GET endpoint and requires no headers or body to be sent.

### Response

If successful, here is the fields that will populate on return.

* Status - Numerical value to the HTTP Status
* HttpStatus - Http Status
* Message - A status message
* Bookings - List of Booking Payments with Quality Check records
    * Payment with Quality Check Object fields.
        * Reference - The payment record reference id
        * Amount - The amount of the bill
        * AmountWithFees - The amount of the bill plus applicable fees
        * AmountReceived - The amount received from the student
        * QualityCheck - A message string that will either be null or filled with quality check validation failures
        * OverPayment - If the student has overpaid the amount owed (AmountReceived > AmountWithFees)
        * UnderPayment - If the student has underpaid the amount owed (AmountReceived < AmountWithFees)

Sample Bookings Response

```declarative
{
  "status": 200, 
  "httpStatus": "OK", 
  "message": "SUCCESS", 
  "bookings": [
    {
        "reference": "66305625", 
        "amount": 25880511, 
        "amountWithFees": 26398121.22, 
        "amountReceived": 25880503, 
        "qualityCheck": "AmountThreshold",
        "overPayment": false, 
        "underPayment": true
    }
  ]
}
```

Sample Bookings Error Response

```declarative
{
    "status": 424, 
    "httpStatus": "FAILED_DEPENDENCY", 
    "message": "Exception encountered when attempting to retireve book portal data : Connection refused: localhost/127.0.0.1:9292", 
    "bookings": null
}
```

### Exceptions

The exceptions custom to this application

* EndpointBookingsException - Thrown in the controller when an unknown and unexpected exception has been encountered and
  processed in the controller advise.
* RetrievePortalBookingsException - Thrown in the Bookings Portal API Connector when an exception (like failed to
  connect) is encountered and processed in the controller advise.


***

# Booking Exercise Requirements

### Goal:
Develop a client app that will communicate with the given Booking Portal.

NOTE: Booking Portal provided for exercise but not owned or coded by Patrick Aquilone and not included here.  To get an idea of how it responds look at the cucumber tests.

### Description:

The "Booking portal" application purpose is to record/create students payment bookings.
It includes a UI payment form with the following structure.

When the form is submitted the application creates a payment record with the provided information.

There are 2 API endpoints which described in the app.

### Exercise:
Please develop a second application, inside the client directory. The second application will communicate with the "booking portal" app (- the Server)  in order to accomplish the following:

Implement an API endpoint:  /payments_with_quality_check.  which returns a JSON response    with the next structure:

```json
{
  "bookings_with_quality_check": [
    {
      "reference": string,
      "amount": number,
      "amountWithFees": number,
      "amountReceived": number,
      "qualityCheck": string separated by commas,
      "overPayment": boolean,
      "underPayment": boolean
    }
  ]
}
```
### API Response Fields Details:

#### amountWithFees: number
Fee logic:

|                      Amount | Fee |
|----------------------------|:---:|
|                 <= 1000 USD | 5%  |
| > 1000 USD AND <= 10000 USD | 3%  |
|                 > 10000 USD | 2%  |


--
#### qualityCheck: string
Quality check messages to return:

| Quality Check                                                              |      Message      |
|--------------------------------------------------------------------|:-----------------:|
| The payment has an invalid email                                   |   InvalidEmail    |
| The user already has a booked payment in the system                | DuplicatedPayment |
| The amount of the payment in USD including the fees  >  1.000.000$ |  AmountThreshold  |
--
#### overPayment: boolean
**over-payment** field indicates if the user payed more than the tuition amount including fees (in USD).

#### underPayment: boolean
**under-payment** field indicates if the user payed less than the tuition amount including fees (in USD).

### Currency
All amounts referenced above are in USD but you can receive a different currency from the server which will need to be converted to create the extra values.

### Notes

* The client code should be implemented in a BE programming language of your preference.
* You should not use a FrontEnd library such as Angular/Vue/React or even pure JS for the client part (node.js is allowed).
  You may use FrontEnd libraries if you decided to add UI in *addition* to the BE part, but this is not mandatory.
* Tests are required.
* "Booking portal" is a given server application its code should not be change. It should be treated as an 3rd party API.
* Nothing is bulletproof. Hence please don't assume things like - "this server will not have any error"

### Important Note:
The client should be used as the BE of a web system, and the Server is the 3rd-party Payments API which we cannot modify as part the exercise.
For your convenience we added a UI for the DB used by the server so you could add mock payments to the payment repository for testing purposes.
