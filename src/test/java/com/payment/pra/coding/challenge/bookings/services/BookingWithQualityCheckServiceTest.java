package com.payment.pra.coding.challenge.bookings.services;

import com.payment.pra.coding.challenge.bookings.connect.apis.BookingsPortalConnector;
import com.payment.pra.coding.challenge.bookings.models.api.BookingsResponse;
import com.payment.pra.coding.challenge.bookings.models.api.BookingsWithQualityCheck;
import com.payment.pra.coding.challenge.bookings.models.booking.portal.PortalBookings;
import com.payment.pra.coding.challenge.bookings.models.booking.portal.PortalPayment;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingWithQualityCheckServiceTest {

    public static Random random = new Random(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());

    @Mock
    private BookingsPortalConnector connector;

    @InjectMocks
    private BookingWithQualityCheckService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new BookingWithQualityCheckService(connector);
    }

    @AfterEach
    void tearDown() {
        service = new BookingWithQualityCheckService(connector);
    }

    @Test
    void retrieveBookingsWithQualityCheck() throws Exception {
        // Arrange
        BigDecimal amountWithFees = BigDecimal.TEN.multiply(BigDecimal.valueOf(1.05)).setScale(0, RoundingMode.HALF_UP);
        PortalPayment payment1 = createPortalPayment(BigDecimal.TEN, amountWithFees);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, BigDecimal.ONE);
        payment1.setEmail("joe@self.com");
        payment1.setReference(RandomStringUtils.secure().nextAlphanumeric(10));
        payment2.setReference(RandomStringUtils.secure().nextAlphanumeric(10));
        List<PortalPayment> paymentList = Lists.newArrayList(payment1, payment2);
        PortalBookings portalBookings = PortalBookings.builder().bookings(paymentList).build();
        when(connector.executePaymentsWithQualityCheck()).thenReturn(portalBookings);
        service = new BookingWithQualityCheckService(connector);
        // Act
        BookingsResponse result = service.retrieveBookingsWithQualityCheck();
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBookings()).isNotNull().isNotEmpty();
        Assertions.assertThat(result.getBookings().get(0).getQualityCheck()).isNull();
        Assertions.assertThat(result.getBookings().get(0).getReference()).isEqualToIgnoringCase(payment1.getReference());
        Assertions.assertThat(result.getBookings().get(0).getOverPayment()).isEqualTo(Boolean.FALSE);
        Assertions.assertThat(result.getBookings().get(0).getUnderPayment()).isEqualTo(Boolean.FALSE);
        Assertions.assertThat(result.getBookings().get(0).getAmountReceived()).isEqualTo(payment1.getAmount_received());
        Assertions.assertThat(result.getBookings().get(0).getAmount()).isEqualTo(payment1.getAmount());
        Assertions.assertThat(result.getBookings().get(0).getAmountWithFees()).isEqualTo(amountWithFees);
    }

    @Test
    void callBookingPortal() throws Exception {
        // Arrange
        BigDecimal amountWithFees = BigDecimal.TEN.multiply(BigDecimal.valueOf(1.05)).setScale(0, RoundingMode.HALF_UP);
        PortalPayment payment1 = createPortalPayment(BigDecimal.TEN, amountWithFees);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, BigDecimal.ONE);
        payment1.setEmail("joe@self.com");
        payment1.setReference(RandomStringUtils.secure().nextAlphanumeric(10));
        payment2.setReference(RandomStringUtils.secure().nextAlphanumeric(10));
        List<PortalPayment> paymentList = Lists.newArrayList(payment1, payment2);
        PortalBookings portalBookings = PortalBookings.builder().bookings(paymentList).build();
        when(connector.executePaymentsWithQualityCheck()).thenReturn(portalBookings);
        service = new BookingWithQualityCheckService(connector);
        // Act
        PortalBookings result = service.callBookingPortal();
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBookings()).isNotNull().isNotEmpty();
        Assertions.assertThat(result.getBookings().get(0).getReference()).isEqualToIgnoringCase(payment1.getReference());
        Assertions.assertThat(result.getBookings().get(0).getAmount_received()).isEqualTo(payment1.getAmount_received());
        Assertions.assertThat(result.getBookings().get(0).getAmount()).isEqualTo(payment1.getAmount());
    }

    @Test
    void convertPortalPaymentsToBookingsWithQuality() {
        // Arrange
        BigDecimal amountWithFees = BigDecimal.TEN.multiply(BigDecimal.valueOf(1.05)).setScale(0, RoundingMode.HALF_UP);
        PortalPayment payment1 = createPortalPayment(BigDecimal.TEN, amountWithFees);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, BigDecimal.ONE);
        payment1.setEmail("joe@self.com");
        payment1.setReference(RandomStringUtils.secure().nextAlphanumeric(10));
        payment2.setReference(RandomStringUtils.secure().nextAlphanumeric(10));
        List<PortalPayment> paymentList = Lists.newArrayList(payment1, payment2);
        PortalBookings portalBookings = PortalBookings.builder().bookings(paymentList).build();
        // Act
        BookingsResponse result = service.convertPortalPaymentsToBookingsWithQuality(portalBookings);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBookings()).isNotNull().isNotEmpty();
        Assertions.assertThat(result.getBookings().get(0).getQualityCheck()).isNull();
        Assertions.assertThat(result.getBookings().get(0).getReference()).isEqualToIgnoringCase(payment1.getReference());
        Assertions.assertThat(result.getBookings().get(0).getOverPayment()).isEqualTo(Boolean.FALSE);
        Assertions.assertThat(result.getBookings().get(0).getUnderPayment()).isEqualTo(Boolean.FALSE);
        Assertions.assertThat(result.getBookings().get(0).getAmountReceived()).isEqualTo(payment1.getAmount_received());
        Assertions.assertThat(result.getBookings().get(0).getAmount()).isEqualTo(payment1.getAmount());
        Assertions.assertThat(result.getBookings().get(0).getAmountWithFees()).isEqualTo(amountWithFees);
    }

    @Test
    void test_convertSinglePayment() {
        // Arrange
        BigDecimal amountWithFees = BigDecimal.TEN.multiply(BigDecimal.valueOf(1.05)).setScale(0, RoundingMode.HALF_UP);
        PortalPayment payment1 = createPortalPayment(BigDecimal.TEN, amountWithFees);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, BigDecimal.ONE);
        payment1.setEmail("joe@self.com");
        payment1.setReference(RandomStringUtils.secure().nextAlphanumeric(10));
        payment2.setReference(RandomStringUtils.secure().nextAlphanumeric(10));
        List<PortalPayment> paymentList = Lists.newArrayList(payment1, payment2);
        // Act
        BookingsWithQualityCheck result = service.convertSinglePayment(payment1, paymentList);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getQualityCheck()).isNull();
        Assertions.assertThat(result.getReference()).isEqualToIgnoringCase(payment1.getReference());
        Assertions.assertThat(result.getOverPayment()).isEqualTo(Boolean.FALSE);
        Assertions.assertThat(result.getUnderPayment()).isEqualTo(Boolean.FALSE);
        Assertions.assertThat(result.getAmountReceived()).isEqualTo(payment1.getAmount_received());
        Assertions.assertThat(result.getAmount()).isEqualTo(payment1.getAmount());
        Assertions.assertThat(result.getAmountWithFees()).isEqualTo(amountWithFees);
    }

    @ParameterizedTest
    @MethodSource("determineUnderPaymentData")
    void test_determineUnderPayment(BigDecimal cost, BigDecimal amount, Boolean expected) {
        // Arrange
        PortalPayment payment = createPortalPayment(cost, amount);
        // Act
        Boolean result = service.determineUnderPayment(payment, cost);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking if checking underpayment working
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> determineUnderPaymentData() {
        return Stream.of(
                Arguments.of(BigDecimal.TEN, BigDecimal.ONE, Boolean.TRUE),
                Arguments.of(BigDecimal.TEN, BigDecimal.TEN, Boolean.FALSE),
                Arguments.of(BigDecimal.ONE, BigDecimal.TEN, Boolean.FALSE)
        );
    }


    @ParameterizedTest
    @MethodSource("determineOverPaymentData")
    void test_determineOverPayment(BigDecimal cost, BigDecimal amount, Boolean expected) {
        // Arrange
        PortalPayment payment = createPortalPayment(cost, amount);
        // Act
        Boolean result = service.determineOverPayment(payment, cost);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking if checking overpayment working
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> determineOverPaymentData() {
        return Stream.of(
                Arguments.of(BigDecimal.TEN, BigDecimal.ONE, Boolean.FALSE),
                Arguments.of(BigDecimal.TEN, BigDecimal.TEN, Boolean.FALSE),
                Arguments.of(BigDecimal.ONE, BigDecimal.TEN, Boolean.TRUE)
        );
    }


    @ParameterizedTest
    @MethodSource("addValueData")
    void test_addValue(StringBuilder current, String add, String expected) {
        // Act
        String result = service.addValue(current, add);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualToIgnoringCase(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking if string adding with comma working
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> addValueData() {
        return Stream.of(
                Arguments.of(new StringBuilder(), "TEST", "TEST"),
                Arguments.of(new StringBuilder("ABCD"), "TEST", ",TEST"),
                Arguments.of(new StringBuilder(), "", "")
        );
    }


    @ParameterizedTest
    @MethodSource("determineQualityData")
    void test_determineQuality(String email, Integer sid1, Integer sid2, String school1, String school2, BigDecimal amount1, BigDecimal amount2, String expected) {
        // Arrange
        PortalPayment payment1 = createPortalPayment(BigDecimal.ONE, amount1);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, amount2);
        payment1.setEmail(email);
        payment1.setStudent_id(sid1);
        payment1.setSchool(school1);
        payment2.setStudent_id(sid2);
        payment2.setSchool(school2);
        List<PortalPayment> paymentList = Lists.newArrayList(payment1, payment2);
        // Act
        String result = service.determineQuality(payment1, amount1, paymentList);
        // Assert
        if (Optional.ofNullable(expected).isPresent()) {
            Assertions.assertThat(result).isNotNull();
        }
        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking if quality is reported correctly
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> determineQualityData() {
        return Stream.of(
                Arguments.of("joe@self.com", 123, 123, "UTA", "UTA", BigDecimal.TEN, BigDecimal.ONE, null),
                Arguments.of(RandomStringUtils.secure().nextAlphanumeric(10), 123, 123, "UTA", "UTA", BigDecimal.TEN, BigDecimal.ONE, "InvalidEmail"),
                Arguments.of("joe@self.com", 123, 123, "UTA", "UTA", BigDecimal.valueOf(110000000), BigDecimal.ONE, "AmountThreshold"),
                Arguments.of("joe@self.com", 123, 123, "UTA", "UTA", BigDecimal.TEN, BigDecimal.TEN, "DuplicatedPayment"),
                Arguments.of(RandomStringUtils.secure().nextAlphanumeric(10), 123, 123, "UTA", "UTA", BigDecimal.valueOf(110000000), BigDecimal.valueOf(110000000), "InvalidEmail,DuplicatedPayment,AmountThreshold")
        );
    }

    @ParameterizedTest
    @MethodSource("checkDuplicateData")
    void test_checkDuplicate(Integer sid1, Integer sid2, String school1, String school2, BigDecimal amount1, BigDecimal amount2, String expected) {
        // Arrange
        PortalPayment payment1 = createPortalPayment(BigDecimal.ONE, amount1);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, amount2);
        payment1.setStudent_id(sid1);
        payment1.setSchool(school1);
        payment2.setStudent_id(sid2);
        payment2.setSchool(school2);
        List<PortalPayment> paymentList = Lists.newArrayList(payment1, payment2);
        // Act
        String result = service.checkDuplicate(payment1, paymentList);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking if payment is duplicated
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> checkDuplicateData() {
        return Stream.of(
                Arguments.of(123, 123, "UTA", "UTA", BigDecimal.TEN, BigDecimal.TEN, "DuplicatedPayment"),
                Arguments.of(123, random.nextInt(1000), "UTA", "UTA", BigDecimal.TEN, BigDecimal.TEN, ""),
                Arguments.of(123, 123, "UTA", "UTA", BigDecimal.TEN, BigDecimal.ONE, "")
        );
    }


    @ParameterizedTest
    @MethodSource("checkDuplicateMatchData")
    void test_checkDuplicateMatch(Integer sid1, Integer sid2, String school1, String school2, BigDecimal amount1, BigDecimal amount2, Boolean expected) {
        // Arrange
        PortalPayment payment1 = createPortalPayment(BigDecimal.ONE, amount1);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, amount2);
        payment1.setStudent_id(sid1);
        payment1.setSchool(school1);
        payment2.setStudent_id(sid2);
        payment2.setSchool(school2);
        // Act
        Boolean result = service.checkDuplicateMatch(payment1, payment2);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking if payment is duplicated
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> checkDuplicateMatchData() {
        return Stream.of(
                Arguments.of(123, 123, "UTA", "UTA", BigDecimal.TEN, BigDecimal.TEN, Boolean.TRUE),
                Arguments.of(123, random.nextInt(1000), "UTA", "UTA", BigDecimal.TEN, BigDecimal.TEN, Boolean.FALSE),
                Arguments.of(123, 123, "UTA", "UTA", BigDecimal.TEN, BigDecimal.ONE, Boolean.FALSE)
        );
    }


    @ParameterizedTest
    @MethodSource("checkAmountData")
    void test_checkAmount(BigDecimal amount1, BigDecimal amount2, Boolean expected) {
        // Arrange
        PortalPayment payment1 = createPortalPayment(BigDecimal.ONE, amount1);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, amount2);
        // Act
        Boolean result = service.checkAmount(payment1, payment2);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking if amounts received match
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> checkAmountData() {
        return Stream.of(
                Arguments.of(BigDecimal.TEN, BigDecimal.TEN, Boolean.TRUE),
                Arguments.of(BigDecimal.TEN, BigDecimal.ONE, Boolean.FALSE)
        );
    }


    @ParameterizedTest
    @MethodSource("checkSchoolData")
    void test_checkSchool(String school1, String school2, Boolean expected) {
        // Arrange
        PortalPayment payment1 = createPortalPayment(BigDecimal.ONE, BigDecimal.ONE);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, BigDecimal.ONE);
        payment1.setSchool(school1);
        payment2.setSchool(school2);
        // Act
        Boolean result = service.checkSchool(payment1, payment2);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking if references match
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> checkSchoolData() {
        return Stream.of(
                Arguments.of("UTA", "UTA", Boolean.TRUE),
                Arguments.of("UTA", RandomStringUtils.secure().nextAlphanumeric(10), Boolean.FALSE)
        );
    }


    @ParameterizedTest
    @MethodSource("checkStudentIdData")
    void test_checkStudentId(Integer sid1, Integer sid2, Boolean expected) {
        // Arrange
        PortalPayment payment1 = createPortalPayment(BigDecimal.ONE, BigDecimal.ONE);
        PortalPayment payment2 = createPortalPayment(BigDecimal.ONE, BigDecimal.ONE);
        payment1.setStudent_id(sid1);
        payment2.setStudent_id(sid2);
        // Act
        Boolean result = service.checkStudentId(payment1, payment2);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking if references match
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> checkStudentIdData() {
        return Stream.of(
                Arguments.of(123, 123, Boolean.TRUE),
                Arguments.of(random.nextInt(1000), random.nextInt(1000), Boolean.FALSE)
        );
    }


    @ParameterizedTest
    @MethodSource("checkAmountThresholdData")
    void test_checkAmountThreshold(BigDecimal amount, String expected) {
        // Act
        String result = service.checkAmountThreshold(amount);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualToIgnoringCase(expected);
    }

    /**
     * Provide the data for the parameterized testing of checking threshold exceeded
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> checkAmountThresholdData() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(100), ""),
                Arguments.of(BigDecimal.valueOf(110000000), "AmountThreshold")
        );
    }

    @ParameterizedTest
    @MethodSource("checkInvalidEmailData")
    void test_checkInvalidEmail(String email, String expected) {
        // Arrange
        PortalPayment payment = createPortalPayment(BigDecimal.ONE, BigDecimal.ONE);
        payment.setEmail(email);
        // Act
        String result = service.checkInvalidEmail(payment);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualToIgnoringCase(expected);
    }

    /**
     * Provide the data for the parameterized testing of valid email
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> checkInvalidEmailData() {
        return Stream.of(
                Arguments.of("joe@self.com", ""),
                Arguments.of(RandomStringUtils.secure().nextAlphanumeric(50), "InvalidEmail")
        );
    }

    @ParameterizedTest
    @MethodSource("calculateAmountWIthFeesData")
    void test_calculateAmountWIthFees(BigDecimal costs, BigDecimal expectedAmount) {
        // Arrange
        PortalPayment payment = createPortalPayment(costs, costs);
        // Act
        BigDecimal result = service.calculateAmountWIthFees(payment);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualByComparingTo(expectedAmount);
    }

    /**
     * Provide the data for the parameterized testing of calculate amount with fees
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> calculateAmountWIthFeesData() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(100), BigDecimal.valueOf(100 * 1.05)),
                Arguments.of(BigDecimal.valueOf(1100), BigDecimal.valueOf(1100 * 1.03)),
                Arguments.of(BigDecimal.valueOf(11100), BigDecimal.valueOf(11100 * 1.02))
        );
    }

    @ParameterizedTest
    @MethodSource("calculateIndividualFeesData")
    void test_calculateIndividualFees(BigDecimal costs, BigDecimal expectedFees) {
        // Arrange
        PortalPayment payment = createPortalPayment(costs, costs);
        // Act
        BigDecimal result = service.calculateIndividualFees(payment);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualByComparingTo(expectedFees);
    }

    /**
     * Provide the data for the parameterized testing of calculate fees
     *
     * @return The stream of arguments
     */
    private static Stream<Arguments> calculateIndividualFeesData() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(100), BigDecimal.valueOf(100 * 0.05)),
                Arguments.of(BigDecimal.valueOf(1100), BigDecimal.valueOf(1100 * 0.03)),
                Arguments.of(BigDecimal.valueOf(11100), BigDecimal.valueOf(11100 * 0.02))
        );
    }

    /**
     * Create a portal bookings object with one item in it
     *
     * @param costs The costs amount
     * @return The created object
     */
    private static PortalPayment createPortalPayment(BigDecimal costs, BigDecimal amountReceived) {
        return PortalPayment.builder()
                .reference(RandomStringUtils.secure().nextAlphanumeric(10))
                .amount(costs)
                .amount_received(amountReceived)
                .email("joe@self.com")
                .build();
    }
}