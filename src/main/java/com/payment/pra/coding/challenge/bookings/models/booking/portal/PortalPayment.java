package com.payment.pra.coding.challenge.bookings.models.booking.portal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Booking portal payment object from api
 */
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class PortalPayment {

    private String reference;
    private BigDecimal amount;
    private BigDecimal amount_received;
    private String country_from;
    private String sender_full_name;
    private String sender_address;
    private String school;
    private String currency_from;
    private Integer student_id;
    private String email;

}
