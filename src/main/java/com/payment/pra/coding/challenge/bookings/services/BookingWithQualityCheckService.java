package com.payment.pra.coding.challenge.bookings.services;

import com.payment.pra.coding.challenge.bookings.connect.apis.BookingsPortalConnector;
import com.payment.pra.coding.challenge.bookings.exceptions.RetrievePortalBookingsException;
import com.payment.pra.coding.challenge.bookings.models.api.BookingsResponse;
import com.payment.pra.coding.challenge.bookings.models.api.BookingsWithQualityCheck;
import com.payment.pra.coding.challenge.bookings.models.booking.portal.PortalBookings;
import com.payment.pra.coding.challenge.bookings.models.booking.portal.PortalPayment;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service class to make call to booking portal and retrieve the payments and do quality checks
 */
@Service
public class BookingWithQualityCheckService {

    public static final int LESS_THAN = -1;
    public static final int GREATER_THAN = 1;
    public static final BigDecimal AMOUNT_THRESHOLD_VALUE = BigDecimal.valueOf(100000000);
    public static final String AMOUNT_THRESHOLD_TEXT = "AmountThreshold";
    public static final String INVALID_EMAIL_TEXT = "InvalidEmail";
    public static final String DUPLICATED_PAYMENT_TEXT = "DuplicatedPayment";
    public static final String EMAIL_REGEX = "^(.*\\@.*\\..*)";

    /**
     * The bookings portal connect
     */
    protected final BookingsPortalConnector connector;

    public BookingWithQualityCheckService(BookingsPortalConnector connector) {
        this.connector = connector;
    }

    /**
     * Attempt to retrieve the payment booking data from the portal and convert it to bookings
     *
     * @return THe bookings
     * @throws RetrievePortalBookingsException thrown if error encountered
     */
    public BookingsResponse retrieveBookingsWithQualityCheck() throws RetrievePortalBookingsException {
        PortalBookings portalBookings = callBookingPortal();
        BookingsResponse bookingsResponse = convertPortalPaymentsToBookingsWithQuality(portalBookings);
        return bookingsResponse;
    }

    /**
     * Attempt to call the booking portal
     *
     * @return The portal bookings
     * @throws RetrievePortalBookingsException thrown if exception encountered
     */
    protected PortalBookings callBookingPortal() throws RetrievePortalBookingsException {
        PortalBookings portalBookings = null;
        try {
            portalBookings = connector.executePaymentsWithQualityCheck();
        } catch (Exception exception) {
            String msg = "Exception encountered when attempting to retireve book portal data : " + exception.getMessage();
            throw new RetrievePortalBookingsException(msg, exception);
        }
        return portalBookings;
    }

    /**
     * Convert the portal data to the booking object for responding
     *
     * @param portalBookings The portal booking data
     * @return The converted objects
     */
    protected BookingsResponse convertPortalPaymentsToBookingsWithQuality(PortalBookings portalBookings) {
        BookingsResponse bookingsResponse = BookingsResponse.builder().bookings(new ArrayList<>()).build();
        if (Optional.ofNullable(portalBookings).isPresent()) {
            List<PortalPayment> paymentsList = portalBookings.getBookings();
            if (!CollectionUtils.isEmpty(paymentsList)) {
                for (PortalPayment payment : paymentsList) {
                    bookingsResponse.getBookings().add(convertSinglePayment(payment, paymentsList));
                }
            }
        }
        return bookingsResponse;
    }

    /**
     * Convert a single payemnet
     *
     * @param payment      The payment to convert
     * @param paymentsList The payment list for checking for duplicates
     * @return The create booking with quality check object
     */
    protected BookingsWithQualityCheck convertSinglePayment(PortalPayment payment, List<PortalPayment> paymentsList) {
        BigDecimal amountWithFees = calculateAmountWIthFees(payment);
        return BookingsWithQualityCheck.builder()
                .reference(payment.getReference())
                .amount(payment.getAmount())
                .amountWithFees(amountWithFees)
                .amountReceived(payment.getAmount_received())
                .qualityCheck(determineQuality(payment, amountWithFees, paymentsList))
                .overPayment(determineOverPayment(payment, amountWithFees))
                .underPayment(determineUnderPayment(payment, amountWithFees))
                .build();
    }

    /**
     * Determine if the student has under paid
     *
     * @param payment        The current student payment record
     * @param amountWithFees The amount with fees owed
     * @return True if yes, false otherwise
     */
    protected Boolean determineUnderPayment(PortalPayment payment, BigDecimal amountWithFees) {
        return payment.getAmount_received().compareTo(amountWithFees) == LESS_THAN;
    }

    /**
     * Determine if the student has over paid
     *
     * @param payment        The current student payment record
     * @param amountWithFees The amount with fees owed
     * @return True if yes, false otherwise
     */
    protected Boolean determineOverPayment(PortalPayment payment, BigDecimal amountWithFees) {
        return payment.getAmount_received().compareTo(amountWithFees) == GREATER_THAN;
    }

    /**
     * Determine quality checks on payment record
     *
     * @param payment        The current student payment record
     * @param amountWithFees The amount with fees owed
     * @param paymentsList   The list of all payments retrieved for checking for duplicates
     * @return The string message of quality check failures or null
     */
    protected String determineQuality(PortalPayment payment, BigDecimal amountWithFees, List<PortalPayment> paymentsList) {
        StringBuilder quality = new StringBuilder();
        String invalidEmail = checkInvalidEmail(payment);
        String amtThreshold = checkAmountThreshold(payment.getAmount_received());
        String duplicate = checkDuplicate(payment, paymentsList);
        quality.append(addValue(quality, invalidEmail));
        quality.append(addValue(quality, duplicate));
        quality.append(addValue(quality, amtThreshold));
        return Optional.of(quality).map(StringBuilder::toString).filter(StringUtils::isNotBlank).orElse(null);
    }

    /**
     * Determine if when adding we need to add a comma
     *
     * @param current The current string
     * @param add     The string to add
     * @return The amount to append to the current
     */
    protected String addValue(StringBuilder current, String add) {
        StringBuilder result = new StringBuilder();
        if (StringUtils.isNotEmpty(add)) {
            result.append(Optional.of(current).filter(c -> !c.isEmpty()).map(x -> ",").orElse(""));
            result.append(add);
        }
        return result.toString();
    }

    /**
     * Check if the current payment is a duplicate
     *
     * @param payment      The current student payment record
     * @param paymentsList The list of all payments retrieved for checking for duplicates
     * @return Returns either the validation failed message or empty string
     */
    protected String checkDuplicate(PortalPayment payment, List<PortalPayment> paymentsList) {
        Long numberOfPayments = paymentsList.stream()
                .filter(p -> checkDuplicateMatch(payment, p))
                .count();
        return Optional.ofNullable(numberOfPayments)
                .filter(num -> num > 1)
                .map(x -> DUPLICATED_PAYMENT_TEXT)
                .orElse("");
    }

    /**
     * Check for duplicate match on the given two payments based on student id, school and amount recieved
     *
     * @param payment1 The payment 1
     * @param payment2 The payment 2
     * @return True if a match
     */
    protected boolean checkDuplicateMatch(PortalPayment payment1, PortalPayment payment2) {
        return checkStudentId(payment1, payment2) &&
                checkAmount(payment1, payment2) &&
                checkSchool(payment1, payment2);
    }

    /**
     * Check if the schools match
     *
     * @param payment1 The payment 1
     * @param payment2 The payment 2
     * @return True if a match
     */
    protected boolean checkSchool(PortalPayment payment1, PortalPayment payment2) {
        String school1 = Optional.ofNullable(payment1.getSchool()).orElse("PAYMENT1_WONT_MATCH");
        String school2 = Optional.ofNullable(payment2.getSchool()).orElse("PAYMENT2_CANT_MATCH");
        return school1.equalsIgnoreCase(school2);
    }

    /**
     * Check the amount to see if it is equal
     *
     * @param payment1 The original amount to check
     * @param payment2 The secondary amount to check if equal
     * @return True if equal
     */
    protected Boolean checkAmount(PortalPayment payment1, PortalPayment payment2) {
        return payment1.getAmount_received().compareTo(payment2.getAmount_received()) == 0;
    }

    /**
     * Check the student id to see if it is equal
     *
     * @param payment1 The original reference
     * @param payment2 The secondary reference to check if eqaul
     * @return True if equal
     */
    protected Boolean checkStudentId(PortalPayment payment1, PortalPayment payment2) {
        Integer check1 = Optional.ofNullable(payment1.getStudent_id()).orElse(-1);
        Integer check2 = Optional.ofNullable(payment2.getStudent_id()).orElse(-2);
        return check1.equals(check2);
    }

    /**
     * Check if the amount threshold has been gone over
     *
     * @param amountRecieved The amount recieved
     * @return Returns either the validation failed message or empty string
     */
    protected String checkAmountThreshold(BigDecimal amountRecieved) {
        return Optional.ofNullable(amountRecieved)
                .filter(amt -> amt.compareTo(AMOUNT_THRESHOLD_VALUE) == 1)
                .map(x -> AMOUNT_THRESHOLD_TEXT)
                .orElse("");
    }

    /**
     * Check if email is invalid
     * Without extra specifications this will see if an @ symbol and a period are present
     *
     * @param payment The current payment object
     * @return Returns either the validation failed message or empty string
     */
    protected String checkInvalidEmail(PortalPayment payment) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(payment.getEmail());
        Boolean check = matcher.find();
        return Optional.of(check)
                .filter(Boolean.FALSE::equals)
                .map(x -> INVALID_EMAIL_TEXT)
                .orElse("");
    }

    /**
     * Calculate the amount owed with all fees
     *
     * @param payment The current payment object
     * @return The total amount owed
     */
    protected BigDecimal calculateAmountWIthFees(PortalPayment payment) {
        BigDecimal amountWithFees = BigDecimal.ZERO;
        if (Optional.ofNullable(payment.getAmount()).isPresent()) {
            amountWithFees = amountWithFees.add(payment.getAmount());
            amountWithFees = amountWithFees.add(calculateIndividualFees(payment));
        }
        return amountWithFees;
    }

    /**
     * Calculate an individual fee on a payment
     *
     * @param payment The current payment object
     * @return The total fee owed
     */
    protected BigDecimal calculateIndividualFees(PortalPayment payment) {
        BigDecimal fees = BigDecimal.ZERO;
        if (Optional.ofNullable(payment.getAmount()).isPresent()) {
            if (payment.getAmount().compareTo(BigDecimal.valueOf(10000)) == GREATER_THAN) {
                fees = payment.getAmount().multiply(BigDecimal.valueOf(0.02)).setScale(0, RoundingMode.HALF_UP);
            } else if (payment.getAmount().compareTo(BigDecimal.valueOf(1000)) == LESS_THAN) {
                fees = payment.getAmount().multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
            } else {
                fees = payment.getAmount().multiply(BigDecimal.valueOf(0.03)).setScale(0, RoundingMode.HALF_UP);
            }
        }
        return fees;
    }
}
