package com.example.weather.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rates {

    @JsonProperty("USD")
    private String USD;

    @JsonProperty("KZT")
    private String KZT;

    @JsonProperty("RUB")
    private String RUB;

    @JsonProperty("EUR")
    private String EUR;

    @JsonProperty("GBP")
    private String GBP;
}
