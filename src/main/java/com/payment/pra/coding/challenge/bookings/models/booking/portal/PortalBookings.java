package com.payment.pra.coding.challenge.bookings.models.booking.portal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Booking portal main wrapper object from api
 */
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class PortalBookings {

    private List<PortalPayment> bookings;

}
