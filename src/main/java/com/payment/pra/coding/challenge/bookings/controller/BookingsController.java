package com.payment.pra.coding.challenge.bookings.controller;

import com.payment.pra.coding.challenge.bookings.exceptions.EndpointBookingsException;
import com.payment.pra.coding.challenge.bookings.exceptions.RetrievePortalBookingsException;
import com.payment.pra.coding.challenge.bookings.models.api.BookingsResponse;
import com.payment.pra.coding.challenge.bookings.services.BookingWithQualityCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The application rest controller for the api endpoints
 */
@Slf4j
@RestController
@RequestMapping
public class BookingsController {

    /**
     * The booking service that will call the booking portal to get the payments with quality checks
     */
    private final BookingWithQualityCheckService service;

    public BookingsController(BookingWithQualityCheckService service) {
        this.service = service;
    }

    /**
     * Get the payments with quality checks
     *
     * @return The response entity of 200 with the payments list
     * @throws RetrievePortalBookingsException Thrown if an error was encountered during downstream call
     * @throws EndpointBookingsException       Thrown if an unknown error was encountered
     */
    @GetMapping("/payments_with_quality_check")
    public ResponseEntity<BookingsResponse> getPaymentsWithQualityCheck() throws EndpointBookingsException, RetrievePortalBookingsException {
        try {
            // Attempt a happy path execution
            BookingsResponse response = service.retrieveBookingsWithQualityCheck();
            response.setStatus(HttpStatus.OK.value());
            response.setHttpStatus(HttpStatus.OK);
            response.setMessage("SUCCESS");
            return ResponseEntity.ok(response);

            // Let the Controller Advise format the error responses
        } catch (RetrievePortalBookingsException exception) {
            String msg = "Retrieve Portal Exception exception encountered : " + exception.getMessage();
            log.error(msg, exception);
            throw exception;
        } catch (Exception exception) {
            String msg = "Unknown exception encountered in bookings controller : " + exception.getMessage();
            log.error(msg, exception);
            throw new EndpointBookingsException(msg, exception);
        }
    }

}
