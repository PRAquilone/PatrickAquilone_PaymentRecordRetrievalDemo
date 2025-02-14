package com.payment.pra.coding.challenge.bookings.exceptions;

/**
 * Custom exception for errors encountered when attempting to connect to Book Portal
 */
public class RetrievePortalBookingsException extends Exception {

    /**
     * Constructor for just a message
     *
     * @param message The exception message
     */
    public RetrievePortalBookingsException(String message) {
        super(message);
    }

    /**
     * Constructor for message and throwable to be able to report
     *
     * @param message The message
     * @param cause   The throwable that was encountered
     */
    public RetrievePortalBookingsException(String message, Throwable cause) {
        super(message, cause);
    }

}
