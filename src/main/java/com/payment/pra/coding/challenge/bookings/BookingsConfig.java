package com.payment.pra.coding.challenge.bookings;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Configurations for the booking application
 */
@Configuration
public class BookingsConfig {

    /**
     * Create a bean for the webclient
     *
     * @param httpClient The HTTP Client for timeouts
     * @return The created webclient
     */
    @Bean
    public WebClient getWebClient(HttpClient httpClient,
                                  @Value("${booking.portal.baseUrl}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", baseUrl))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    /**
     * Create a bean for the HTTP Client for timeouts
     */
    @Bean
    public HttpClient getHttpClient(@Value("${booking.portal.timeout}") Integer timeout) {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .responseTimeout(Duration.ofMillis(timeout))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)));
    }

}
