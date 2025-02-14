package com.payment.pra.coding.challenge.bookings.connect.apis;

import com.payment.pra.coding.challenge.bookings.exceptions.RetrievePortalBookingsException;
import com.payment.pra.coding.challenge.bookings.models.booking.portal.PortalBookings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;

/**
 * The booking portal connector which will call the apis
 */
@Slf4j
@Service
public class BookingsPortalConnector {

    /**
     * The web client for making the call to the booking portal
     */
    private final WebClient client;

    public BookingsPortalConnector(WebClient client) {
        this.client = client;
    }

    /**
     * Execute the web client call to the booking portal for the data
     *
     * @return The list of Bookings with Quality
     * @throws Exception thrown if an error is encountered
     */
    public PortalBookings executePaymentsWithQualityCheck() throws Exception {
        return callToGetBookingsResponse();
    }

    /**
     * Call to get a response from the bookings portal
     *
     * @return The response list block
     * @throws URISyntaxException Thrown if URL can not be created
     */
    protected PortalBookings callToGetBookingsResponse() throws URISyntaxException {
        return client
                .get()
                .uri("/api/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> createErrorResponse(clientResponse, "Client Error: "))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> createErrorResponse(clientResponse, "Server Error: "))
                .bodyToMono(PortalBookings.class)
                .block();
    }

    /**
     * Create an error response
     *
     * @param clientResponse The response from the call
     * @param errorMessage   The error message
     * @return The created response
     */
    protected static Mono<Throwable> createErrorResponse(ClientResponse clientResponse, String errorMessage) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(body -> Mono.error(new RetrievePortalBookingsException(errorMessage + body)));
    }
}
