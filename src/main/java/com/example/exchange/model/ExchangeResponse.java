package com.example.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeResponse {
    private BigDecimal amount;
    private Currency currency;
}
