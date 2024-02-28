package com.example.exchange.service;

import com.example.exchange.model.Currency;
import com.example.exchange.model.ExchangeResponse;

import java.math.BigDecimal;



public interface ExchangeRateService {
    ExchangeResponse getExchangeAmount(Currency baseCurrency, BigDecimal amount, Currency targetCurrency);
}

