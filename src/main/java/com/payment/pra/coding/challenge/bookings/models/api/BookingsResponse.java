package com.payment.pra.coding.challenge.bookings.models.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * The bookings response object
 */
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class BookingsResponse {

    private int status;
    private HttpStatus httpStatus;
    private String message;
    private List<BookingsWithQualityCheck> bookings;

}
