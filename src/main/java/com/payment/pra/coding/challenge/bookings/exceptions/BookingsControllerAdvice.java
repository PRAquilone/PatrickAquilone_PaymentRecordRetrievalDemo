package com.payment.pra.coding.challenge.bookings.exceptions;

import com.payment.pra.coding.challenge.bookings.models.api.BookingsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller advice for exception handling
 */
@RestControllerAdvice
public class BookingsControllerAdvice {

    /**
     * Exception Handler for bookings portal exception encountered
     *
     * @param exception The exception encountered
     * @return The error response
     */
    @ExceptionHandler(RetrievePortalBookingsException.class)
    public ResponseEntity<BookingsResponse> handleRetrievePortalBookingsException(RetrievePortalBookingsException exception) {
        BookingsResponse errorResponse = BookingsResponse.builder()
                .status(HttpStatus.FAILED_DEPENDENCY.value())
                .httpStatus(HttpStatus.FAILED_DEPENDENCY)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(errorResponse);
    }

    /**
     * Exception Handler for bookings controller exception encountered
     *
     * @param exception The exception encountered
     * @return The error response
     */
    @ExceptionHandler(EndpointBookingsException.class)
    public ResponseEntity<BookingsResponse> handleEndpointBookingsExceptionn(EndpointBookingsException exception) {
        BookingsResponse errorResponse = BookingsResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handle any non specific exception thrown
     *
     * @param exception The exception thrown
     * @return The error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BookingsResponse> handleGenericException(Exception exception) {
        BookingsResponse errorResponse = BookingsResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.internalServerError().body(errorResponse);
    }

}
