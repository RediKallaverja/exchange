package com.example.exchange.controller;

import com.example.exchange.constraint.ValidCurrency;
import com.example.exchange.model.Currency;
import com.example.exchange.model.ExchangeResponse;
import com.example.exchange.service.CacheService;
import com.example.exchange.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import org.springframework.cache.Cache;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api")
@Validated
public class ExchangeController {

    private final ExchangeRateService exchangeRateService;
    private final CacheService cacheService;

    public ExchangeController(ExchangeRateService exchangeRateService, CacheService cacheService) {
        this.exchangeRateService = exchangeRateService;
        this.cacheService = cacheService;
    }

    @GetMapping("/convert")
    @Operation(summary = "Convert an arbitrary amount of a given base currency into a specified target currency.")
    public ResponseEntity<ExchangeResponse> convertCurrency(
            @RequestParam
            @ValidCurrency
            String from,
            @RequestParam
            @ValidCurrency
            String to,
            @RequestParam
            @DecimalMin(value = "0.01", message = "Invalid amount")
            @Digits(integer = 19, fraction = 2, message = "Invalid amount")
            BigDecimal amount) {
        return ResponseEntity.ok(exchangeRateService.getExchangeAmount(Currency.valueOf(from), amount, Currency.valueOf(to)));
    }

    @GetMapping("/cache")
    @Operation(summary = "Endpoint for testing to check cache data")
    public ResponseEntity<Cache> getCacheData() {
        return ResponseEntity.ok(cacheService.getCacheData());
    }
}

