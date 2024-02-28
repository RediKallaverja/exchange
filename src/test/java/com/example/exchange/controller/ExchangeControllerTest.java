package com.example.exchange.controller;

import com.example.exchange.client.ExchangeRateApiResponse;
import com.example.exchange.exception.ExceptionCustomModel;
import com.example.exchange.model.Currency;
import com.example.exchange.model.ExchangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExchangeControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setup() {
        Objects.requireNonNull(cacheManager.getCache("exchangeRates")).clear();
    }

    @Test
    void convertCurrencyHappyFlow() {
        ResponseEntity<ExchangeResponse> response =
                this.restTemplate.getForEntity("http://localhost:" + port + "/api/convert?from=USD&to=ALL&amount=100", ExchangeResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getCurrency());
        assertNotNull(response.getBody().getAmount());
        assertEquals(Currency.ALL, response.getBody().getCurrency());

//      check cache
        Optional<ExchangeRateApiResponse> cacheResult = ofNullable(cacheManager.getCache("exchangeRates")).map(
                c -> c.get(Currency.USD, ExchangeRateApiResponse.class)
        );
        assertNotNull(cacheResult);
        assertTrue(cacheResult.isPresent());
        assertEquals("success", cacheResult.get().getResult());
        assertNotNull(cacheResult.get().getDocumentation());
        assertNotNull(cacheResult.get().getTermsOfUse());
        assertEquals("USD", cacheResult.get().getBaseCode());

        assertEquals(new BigDecimal("100").multiply(cacheResult.get().getConversionRates().get("ALL")), response.getBody().getAmount());

    }


    @Test
    void convertCurrencyInvalidCurrency() {
        ResponseEntity<ExceptionCustomModel> response =
                this.restTemplate.getForEntity("http://localhost:" + port + "/api/convert?from=ABC&to=ALL&amount=100", ExceptionCustomModel.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatusCode());
        assertEquals("Invalid currency ABC", response.getBody().getMessage());

//      cache should be empty
        Optional<ExchangeRateApiResponse> cacheResult = ofNullable(cacheManager.getCache("exchangeRates")).map(
                c -> c.get("ABC", ExchangeRateApiResponse.class)
        );
        assertTrue(cacheResult.isEmpty());

    }

    @Test
    void convertCurrencyInvalidAmount() {
        ResponseEntity<ExceptionCustomModel> response =
                this.restTemplate.getForEntity("http://localhost:" + port + "/api/convert?from=USD&to=ALL&amount=100.123", ExceptionCustomModel.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatusCode());
        assertEquals("Invalid amount 100.123", response.getBody().getMessage());

//      cache should be empty
        Optional<ExchangeRateApiResponse> cacheResult = ofNullable(cacheManager.getCache("exchangeRates")).map(
                c -> c.get(Currency.USD, ExchangeRateApiResponse.class)
        );
        assertTrue(cacheResult.isEmpty());

    }
}