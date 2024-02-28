package com.example.exchange.scheduler;

import com.example.exchange.ExchangeApplication;
import com.example.exchange.client.ExchangeRateApiResponse;
import com.example.exchange.model.Currency;
import com.example.exchange.service.CacheServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ExchangeApplication.class)
class ScheduledTasksTest {

    @Autowired
    private CacheServiceImpl cacheService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Test
    void clearCache() {
        ExchangeRateApiResponse exchangeRateApiResponse = cacheService.getExchangeRates(Currency.EUR);

//      check cache
        Optional<ExchangeRateApiResponse> cacheResult = ofNullable(cacheManager.getCache("exchangeRates")).map(
                c -> c.get(Currency.EUR, ExchangeRateApiResponse.class)
        );
        assertEquals(exchangeRateApiResponse.getResult(), cacheResult.get().getResult());
        assertEquals(exchangeRateApiResponse.getBaseCode(), cacheResult.get().getBaseCode());
        assertEquals(exchangeRateApiResponse.getConversionRates(), cacheResult.get().getConversionRates());
        assertEquals(exchangeRateApiResponse.getTimeNextUpdateUnix(), cacheResult.get().getTimeNextUpdateUnix());
        assertEquals(exchangeRateApiResponse.getTimeNextUpdateUtc(), cacheResult.get().getTimeNextUpdateUtc());
        assertEquals(exchangeRateApiResponse.getTimeLastUpdateUnix(), cacheResult.get().getTimeLastUpdateUnix());
        assertEquals(exchangeRateApiResponse.getTimeLastUpdateUtc(), cacheResult.get().getTimeLastUpdateUtc());

//      clear cache
        scheduledTasks.clearCache();
        
//      check cache
        Optional<ExchangeRateApiResponse> emptyCacheResult = ofNullable(cacheManager.getCache("exchangeRates")).map(
                c -> c.get(Currency.EUR, ExchangeRateApiResponse.class)
        );
        assertFalse(emptyCacheResult.isPresent());
    }
}