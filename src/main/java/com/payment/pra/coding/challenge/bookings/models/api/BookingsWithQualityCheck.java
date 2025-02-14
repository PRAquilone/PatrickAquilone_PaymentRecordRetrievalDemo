package com.payment.pra.coding.challenge.bookings.models.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Bookings payments with quality check model for response
 */
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class BookingsWithQualityCheck {

    private String reference;
    private BigDecimal amount;
    private BigDecimal amountWithFees;
    private BigDecimal amountReceived;
    private String qualityCheck;
    private Boolean overPayment;
    private Boolean underPayment;

}
