package com.payment.pra.coding.challenge.bookings.cucumber;

import com.payment.pra.coding.challenge.bookings.models.api.BookingsWithQualityCheck;
import com.payment.pra.coding.challenge.bookings.models.booking.portal.PortalBookings;
import com.payment.pra.coding.challenge.bookings.models.booking.portal.PortalPayment;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.gson.Gson;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class BookingsStepDefinitions extends BookingsApplicationTests {

    public Gson gson = new Gson();

    private WireMockServer wireMockServer;

    public static PortalBookings portalBookings;

    @Before
    public void setup() {
        wireMockServer = new WireMockServer(options().port(9292));
        wireMockServer.start();
        WireMock.configureFor("localhost", 9292);
    }

    @After
    public void teardown() {
        wireMockServer.stop();
    }

    /**
     * Create a mock api response for similating the external api
     *
     * @param responseBody The response body we are sending
     */
    public void mockApiResponse(PortalBookings responseBody) {
        stubFor(get(urlEqualTo("/api/bookings"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(responseBody))));
    }


    /**
     * Make a call to the booking application for a response
     *
     * @throws Throwable Thrown if error encountered
     */
    @When("^the client calls /payments_with_quality_check to get a list of payments$")
    public void request() throws Throwable {
        portalBookings = null;
        bookingsResponse = null;
    }

    /**
     * Add a payment to the portal payment response
     *
     * @param table The data to add
     * @throws Throwable Thrown if an exception encountered
     */
    @And("^with the following portal payment data$")
    public void addPayment(DataTable table) throws Throwable {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        Map<String, String> item = rows.get(0);
        Long amount = Long.valueOf(item.get("amount"));
        Long amountReceived = Long.valueOf(item.get("amount_received"));
        Integer studentId = Integer.parseInt(item.get("student_id"));
        PortalPayment payment = PortalPayment.builder()
                .reference(item.get("reference"))
                .amount(BigDecimal.valueOf(amount))
                .amount_received(BigDecimal.valueOf(amountReceived))
                .country_from(item.get("country_from"))
                .sender_full_name(item.get("sender_full_name"))
                .sender_address(item.get("sender_address"))
                .school(item.get("school"))
                .currency_from(item.get("currency_from"))
                .student_id(studentId)
                .email(item.get("email"))
                .build();
        List<PortalPayment> portalPaymentList = Optional.ofNullable(portalBookings)
                .map(PortalBookings::getBookings)
                .filter(Objects::nonNull)
                .orElse(new ArrayList<>());
        portalPaymentList.add(payment);
        portalBookings = PortalBookings.builder()
                .bookings(portalPaymentList)
                .build();
        mockApiResponse(portalBookings);
    }

    /**
     * Make a call to the booking application for a response
     *
     * @param table The table or portal booking data expected
     * @throws Throwable Thrown if error encountered
     */
    @And("^Call is made to the booking application$")
    public void callBooking() throws Throwable {
        executeGetPaymentWithQualityChecks();
    }

    /**
     * Compare the results and ensure we have correct data
     *
     * @param table The data of expected results
     * @throws Throwable Thrown if an issue was encountered
     */
    @Then("^the client receives the following payment response for a single record$")
    public void responseCheck(DataTable table) throws Throwable {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        Map<String, String> item = rows.get(0);
        Assertions.assertThat(bookingsResponse).isNotNull();
        Assertions.assertThat(bookingsResponse.getBookings()).isNotNull().isNotEmpty();
        BookingsWithQualityCheck bookings = bookingsResponse.getBookings().get(0);
        testValues(item.get("reference"), bookings.getReference());
        testValues(item.get("amount"), bookings.getAmount().toPlainString());
        testValues(item.get("amountWithFees"), bookings.getAmountWithFees().toPlainString());
        testValues(item.get("amountReceived"), bookings.getAmountReceived().toPlainString());
        testValues(item.get("qualityCheck"), bookings.getQualityCheck());
        testValues(item.get("overPayment"), bookings.getOverPayment().toString());
        testValues(item.get("underPayment"), bookings.getUnderPayment().toString());
    }

    /**
     * Test the values to make sure that they equal each other.
     *
     * @param result The result from the rest call
     * @param expect The expected result
     */
    private void testValues(String expect, String result) {
        String resultValue = Optional.ofNullable(result).filter(StringUtils::isNotBlank).orElse(null);
        String expectedValue = Optional.ofNullable(expect).filter(StringUtils::isNotBlank).orElse(null);
        if ((Optional.ofNullable(resultValue).isPresent()) && (Optional.ofNullable(expectedValue).isPresent())) {
            Assertions.assertThat(resultValue).contains(expectedValue);
        }
    }

}
