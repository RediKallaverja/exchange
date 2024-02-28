package com.example.exchange.service;

import com.example.exchange.client.ExchangeRateApiResponse;
import com.example.exchange.exception.EntityNotFoundException;
import com.example.exchange.model.Currency;
import com.example.exchange.model.ExchangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ExchangeRateServiceImplTest {

    @Mock
    private CacheService cacheService;

    private ExchangeRateServiceImpl exchangeRateService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        exchangeRateService = new ExchangeRateServiceImpl(cacheService);
    }

    @Test
    void getExchangeAmount() {

        Currency baseCurrency = Currency.EUR;
        Currency targetCurrency = Currency.ALL;
        BigDecimal amount = new BigDecimal("100.00");

        ExchangeRateApiResponse exchangeRateApiResponse = new ExchangeRateApiResponse();
        exchangeRateApiResponse.setBaseCode(baseCurrency.toString());
        exchangeRateApiResponse.setResult("Success");
        exchangeRateApiResponse.setDocumentation("MockDocumentation");
        exchangeRateApiResponse.setTermsOfUse("MockTermsOfUse");

        Map<String, BigDecimal> map = new HashMap<>();
        map.put(Currency.ALL.toString(), new BigDecimal("103.89"));
        exchangeRateApiResponse.setConversionRates(map);

        when(cacheService.getExchangeRates(baseCurrency)).thenReturn(exchangeRateApiResponse);

        ExchangeResponse response = exchangeRateService.getExchangeAmount(baseCurrency, amount, targetCurrency);
        assertEquals(amount.multiply(new BigDecimal("103.89")), response.getAmount());
        assertEquals(targetCurrency, response.getCurrency());

    }

    @Test
    void getExchangeAmountTargetCurrencyNotSupported() {

        Currency baseCurrency = Currency.EUR;
        BigDecimal amount = new BigDecimal("100.00");

        ExchangeRateApiResponse exchangeRateApiResponse = new ExchangeRateApiResponse();
        exchangeRateApiResponse.setBaseCode(baseCurrency.toString());
        exchangeRateApiResponse.setResult("Success");
        exchangeRateApiResponse.setDocumentation("MockDocumentation");
        exchangeRateApiResponse.setTermsOfUse("MockTermsOfUse");

        Map<String, BigDecimal> map = new HashMap<>();
        map.put(Currency.ALL.toString(), new BigDecimal("103.89"));
        exchangeRateApiResponse.setConversionRates(map);

        when(cacheService.getExchangeRates(baseCurrency)).thenReturn(exchangeRateApiResponse);

        try {
            exchangeRateService.getExchangeAmount(baseCurrency, amount, Currency.AED);
        } catch (EntityNotFoundException ex) {
            assertEquals("Currency AED not supported", ex.getMessage());
        }
    }
}