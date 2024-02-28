package com.example.exchange.service;


import com.example.exchange.client.ExchangeRateApiResponse;
import com.example.exchange.exception.EntityNotFoundException;
import com.example.exchange.model.Currency;
import com.example.exchange.model.ExchangeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final CacheService cacheService;

    public ExchangeRateServiceImpl(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public ExchangeResponse getExchangeAmount(Currency baseCurrency, BigDecimal amount, Currency targetCurrency) {
        ExchangeRateApiResponse exchangeRates = cacheService.getExchangeRates(baseCurrency);

        BigDecimal exchangeRateTargetCurrency = Optional.ofNullable(exchangeRates.getConversionRates().get(targetCurrency.toString()))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Currency %s not supported", targetCurrency)));

        return new ExchangeResponse(amount.multiply(exchangeRateTargetCurrency), targetCurrency);
    }
}
