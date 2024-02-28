package com.example.exchange.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
public class ExchangeRateApiResponse {
    private String result;
    private String documentation;
    @JsonProperty("terms_of_use")
    private String termsOfUse;
    @JsonProperty("time_last_update_unix")
    private long timeLastUpdateUnix;
    @JsonProperty("time_last_update_utc")
    @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z")
    private Date timeLastUpdateUtc;
    @JsonProperty("time_next_update_unix")
    private long timeNextUpdateUnix;
    @JsonProperty("time_next_update_utc")
    @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z")
    private Date timeNextUpdateUtc;
    @JsonProperty("base_code")
    private String baseCode;
    @JsonProperty("conversion_rates")
    private Map<String, BigDecimal> conversionRates;
}