package com.example.exchange.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExceptionCustomModel {
    private String message;
    private Integer statusCode;
}
