package com.payment.pra.coding.challenge.bookings.connect.apis;

import com.payment.pra.coding.challenge.bookings.models.booking.portal.PortalBookings;
import com.payment.pra.coding.challenge.bookings.models.booking.portal.PortalPayment;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingsPortalConnectorTest {

    public static final String TEST_MESSAGE = "TEST";
    public Random random = new Random(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());

    @InjectMocks
    private BookingsPortalConnector connector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_executePaymentsWithQualityCheck() throws Exception {
        // Arrange
        BigDecimal costs = BigDecimal.valueOf(random.nextInt(1000, 10000));
        PortalBookings bookings = createPortalBookings(costs);
        WebClient webClientMock = createWebClientMocks(bookings);
        connector = new BookingsPortalConnector(webClientMock);

        // Act
        PortalBookings result = connector.executePaymentsWithQualityCheck();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBookings()).isNotNull().isNotEmpty();
        PortalPayment resultPayment = result.getBookings().get(0);
        Assertions.assertThat(resultPayment).isNotNull();
        Assertions.assertThat(resultPayment.getAmount()).isNotNull().isEqualTo(costs);
    }

    @Test
    void test_callToGetBookingsResponse() throws URISyntaxException {
        // Arrange
        BigDecimal costs = BigDecimal.valueOf(random.nextInt(1000, 10000));
        PortalBookings bookings = createPortalBookings(costs);
        WebClient webClientMock = createWebClientMocks(bookings);
        connector = new BookingsPortalConnector(webClientMock);

        // Act
        PortalBookings result = connector.callToGetBookingsResponse();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBookings()).isNotNull().isNotEmpty();
        PortalPayment resultPayment = result.getBookings().get(0);
        Assertions.assertThat(resultPayment).isNotNull();
        Assertions.assertThat(resultPayment.getAmount()).isNotNull().isEqualTo(costs);
    }

    @Test
    void test_createErrorResponse() {
        // Arrange
        ClientResponse response = ClientResponse.create(HttpStatus.FAILED_DEPENDENCY)
                .body(TEST_MESSAGE)
                .build();
        // Act
        Mono<Throwable> result = BookingsPortalConnector.createErrorResponse(response, TEST_MESSAGE);
        // Assert
        Assertions.assertThat(result).isNotNull();
    }


    /**
     * Create a portal bookings object with one item in it
     *
     * @param costs The costs amount
     * @return The created object
     */
    private static PortalBookings createPortalBookings(BigDecimal costs) {
        PortalPayment payment = PortalPayment.builder()
                .reference(RandomStringUtils.secure().nextAlphanumeric(10))
                .amount(costs)
                .amount_received(costs)
                .email("joe@self.com")
                .build();
        return PortalBookings.builder()
                .bookings(Lists.newArrayList(payment))
                .build();
    }

    /**
     * Create the web client mock for testing
     *
     * @param bookings The bookings object to return
     * @return The created mock
     */
    private static WebClient createWebClientMocks(PortalBookings bookings) {
        // Create a mock WebClient
        WebClient webClientMock = Mockito.mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

        // Define the behavior of the mock WebClient
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/api/bookings")).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.accept(Mockito.any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(PortalBookings.class)).thenReturn(Mono.just(bookings));
        when(responseSpecMock.onStatus(Mockito.any(), Mockito.any())).thenReturn(responseSpecMock);
        return webClientMock;
    }

}