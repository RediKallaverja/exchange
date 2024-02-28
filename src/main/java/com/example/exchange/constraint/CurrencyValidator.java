package com.example.exchange.constraint;

import com.example.exchange.model.Currency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            Currency.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
