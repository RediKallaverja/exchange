package com.example.exchange.service;

import com.example.exchange.client.ExchangeRateApiResponse;
import com.example.exchange.exception.ServerErrorException;
import com.example.exchange.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CacheServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    private CacheServiceImpl cacheService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        cacheService = new CacheServiceImpl(restTemplate, cacheManager);
        ReflectionTestUtils.setField(cacheService, "apiUrl", "https://v6.exchangerate-api.com/v6/b50ceedd19619d37ffbfd6e8/latest/");
        Objects.requireNonNull(cacheManager.getCache("exchangeRates")).clear();
    }

    @Test
    void getExchangeRates() {
        Currency baseCurrency = Currency.USD;
        ExchangeRateApiResponse exchangeRateApiResponse = new ExchangeRateApiResponse();
        exchangeRateApiResponse.setBaseCode(baseCurrency.toString());
        exchangeRateApiResponse.setResult("Success");
        exchangeRateApiResponse.setDocumentation("MockDocumentation");
        exchangeRateApiResponse.setTermsOfUse("MockTermsOfUse");
        exchangeRateApiResponse.setConversionRates(Collections.emptyMap());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));


        when(restTemplate.getForEntity(
                        "https://v6.exchangerate-api.com/v6/b50ceedd19619d37ffbfd6e8/latest/" + baseCurrency,
                        ExchangeRateApiResponse.class
                )
        ).thenReturn(ResponseEntity.ok().body(exchangeRateApiResponse));


        ExchangeRateApiResponse response = cacheService.getExchangeRates(baseCurrency);

        assertEquals(response.getBaseCode(), exchangeRateApiResponse.getBaseCode());
        assertEquals(response.getResult(), exchangeRateApiResponse.getResult());
        assertEquals(response.getConversionRates(), exchangeRateApiResponse.getConversionRates());

    }


    @Test
    void getExchangeRatesFail() {
        Currency baseCurrency = Currency.USD;
        ExchangeRateApiResponse exchangeRateApiResponse = new ExchangeRateApiResponse();
        exchangeRateApiResponse.setBaseCode(baseCurrency.toString());
        exchangeRateApiResponse.setResult("Success");
        exchangeRateApiResponse.setConversionRates(Collections.emptyMap());


        when(restTemplate.getForEntity(
                        "https://v6.exchangerate-api.com/v6/b50ceedd19619d37ffbfd6e8/latest/" + baseCurrency,
                        ExchangeRateApiResponse.class
                )
        ).thenThrow(new ServerErrorException("Error on external API"));

        try {
            cacheService.getExchangeRates(baseCurrency);
        } catch (ServerErrorException ex) {
            assertEquals("Error on external API", ex.getMessage());
        }

    }
}