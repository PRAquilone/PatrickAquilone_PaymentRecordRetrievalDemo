package com.payment.pra.coding.challenge.bookings.controller;

import com.payment.pra.coding.challenge.bookings.models.api.BookingsResponse;
import com.payment.pra.coding.challenge.bookings.models.api.BookingsWithQualityCheck;
import com.payment.pra.coding.challenge.bookings.services.BookingWithQualityCheckService;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingsControllerTest {

    public Random random = new Random(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());

    @Mock
    private BookingWithQualityCheckService service;

    @InjectMocks
    private BookingsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new BookingsController(service);
    }

    @Test
    void getPaymentsWithQualityCheck() throws Exception {
        // Arrange
        BookingsWithQualityCheck book = BookingsWithQualityCheck.builder()
                .reference(RandomStringUtils.secure().nextAlphanumeric(10))
                .qualityCheck(RandomStringUtils.secure().nextAlphanumeric(10))
                .amount(BigDecimal.valueOf(random.nextInt(1000, 10000)))
                .amountReceived(BigDecimal.valueOf(random.nextInt(1000, 10000)))
                .amountWithFees(BigDecimal.valueOf(random.nextInt(1000, 10000)))
                .overPayment(Boolean.TRUE)
                .underPayment(Boolean.TRUE)
                .build();
        BookingsResponse bookingsResponse = BookingsResponse.builder()
                .bookings(Lists.newArrayList(book))
                .build();
        when(service.retrieveBookingsWithQualityCheck()).thenReturn(bookingsResponse);
        controller = new BookingsController(service);
        // Act
        ResponseEntity<BookingsResponse> result = controller.getPaymentsWithQualityCheck();
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatusCode()).isNotNull().isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody()).isNotNull();
        Assertions.assertThat(result.getBody().getBookings()).isNotNull().isNotEmpty();
        Assertions.assertThat(result.getBody().getBookings().get(0).getReference()).isEqualToIgnoringCase(book.getReference());
        Assertions.assertThat(result.getBody().getBookings().get(0).getQualityCheck()).isEqualToIgnoringCase(book.getQualityCheck());
        Assertions.assertThat(result.getBody().getBookings().get(0).getAmount()).isEqualTo(book.getAmount());
        Assertions.assertThat(result.getBody().getBookings().get(0).getUnderPayment()).isEqualTo(book.getUnderPayment());
        Assertions.assertThat(result.getBody().getBookings().get(0).getOverPayment()).isEqualTo(book.getOverPayment());
        Assertions.assertThat(result.getBody().getBookings().get(0).getAmountReceived()).isEqualTo(book.getAmountReceived());
        Assertions.assertThat(result.getBody().getBookings().get(0).getAmountWithFees()).isEqualTo(book.getAmountWithFees());
    }
}