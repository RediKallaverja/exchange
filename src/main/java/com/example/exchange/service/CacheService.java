package com.example.exchange.service;

import com.example.exchange.client.ExchangeRateApiResponse;
import com.example.exchange.model.Currency;
import org.springframework.cache.Cache;

public interface CacheService {
    ExchangeRateApiResponse getExchangeRates(Currency baseCurrency);
    void evictAllCacheEntries();
    Cache getCacheData();
}
