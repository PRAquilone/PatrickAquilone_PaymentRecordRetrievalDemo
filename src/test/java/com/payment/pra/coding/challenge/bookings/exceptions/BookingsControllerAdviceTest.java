package com.payment.pra.coding.challenge.bookings.exceptions;

import com.payment.pra.coding.challenge.bookings.models.api.BookingsResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BookingsControllerAdviceTest {

    @Mock
    private BookingsControllerAdvice controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new BookingsControllerAdvice();
    }


    @Test
    void test_handleRetrievePortalBookingsException() {
        // Arrange
        RetrievePortalBookingsException exception = new RetrievePortalBookingsException("TEST");
        // Act
        ResponseEntity<BookingsResponse> result = controller.handleRetrievePortalBookingsException(exception);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatusCode()).isNotNull().isEqualTo(HttpStatus.FAILED_DEPENDENCY);
        Assertions.assertThat(result.getBody()).isNotNull();
        Assertions.assertThat(result.getBody().getMessage()).isEqualToIgnoringCase("TEST");
    }

    @Test
    void test_handleEndpointBookingsException() {
        // Arrange
        EndpointBookingsException exception = new EndpointBookingsException("TEST");
        // Act
        ResponseEntity<BookingsResponse> result = controller.handleEndpointBookingsExceptionn(exception);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatusCode()).isNotNull().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(result.getBody()).isNotNull();
        Assertions.assertThat(result.getBody().getMessage()).isEqualToIgnoringCase("TEST");
    }

    @Test
    void test_handleGenericException() {
        // Arrange
        Exception exception = new Exception("TEST");
        // Act
        ResponseEntity<BookingsResponse> result = controller.handleGenericException(exception);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatusCode()).isNotNull().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(result.getBody()).isNotNull();
        Assertions.assertThat(result.getBody().getMessage()).isEqualToIgnoringCase("TEST");
    }
}