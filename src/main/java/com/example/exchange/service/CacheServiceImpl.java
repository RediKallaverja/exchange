package com.example.exchange.service;

import com.example.exchange.client.ExchangeRateApiResponse;
import com.example.exchange.exception.ServerErrorException;
import com.example.exchange.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    @Value("${exchange.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    private final CacheManager cacheManager;

    public CacheServiceImpl(RestTemplate restTemplate, CacheManager cacheManager) {
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
    }

    @Override
    @Cacheable(value = "exchangeRates", key = "#baseCurrency", unless = "#result == null")
    public ExchangeRateApiResponse getExchangeRates(Currency baseCurrency) {

        ResponseEntity<ExchangeRateApiResponse> response = restTemplate.getForEntity(
                apiUrl + baseCurrency,
                ExchangeRateApiResponse.class
        );
        log.info("GET: " + apiUrl + " | status: " + response.getStatusCode());
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            // Handle error response
            throw new ServerErrorException("Failed to fetch exchange rates. Status code: " + response.getStatusCode());
        }
    }

    @Override
    public void evictAllCacheEntries() {
        Objects.requireNonNull(cacheManager.getCache("exchangeRates")).clear();
    }

    @Override
    public Cache getCacheData() {
        return cacheManager.getCache("exchangeRates");
    }
}
