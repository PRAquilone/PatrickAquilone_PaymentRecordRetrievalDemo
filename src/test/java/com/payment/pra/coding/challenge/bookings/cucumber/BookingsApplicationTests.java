package com.payment.pra.coding.challenge.bookings.cucumber;

import com.payment.pra.coding.challenge.bookings.BookingsApplication;
import com.payment.pra.coding.challenge.bookings.models.api.BookingsResponse;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(classes = BookingsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BookingsApplicationTests {

    private final static String bookingsUrl = "http://127.0.0.1:8080/payments_with_quality_check";

    public static BookingsResponse bookingsResponse;

    protected RestTemplate restTemplate = new RestTemplate();

    /**
     * Execute the rest get to the service
     *
     * @throws IOException If we encounter an exception will be thrown
     */
    public void executeGetPaymentWithQualityChecks() throws IOException {
        try {
            bookingsResponse = restTemplate.getForObject(bookingsUrl, BookingsResponse.class);
        } catch (Exception exception) {
            log.error("Bookings Application Encountered exception " + exception.getMessage(), exception);
        }
    }


}
