package com.example.weather.component;

import com.example.weather.models.ExchangeRates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class BotProxy {

    private final RestTemplate rest = new RestTemplate();

    @Value("${name.service.url}")
    private String paymentsServiceUrl;

    public ExchangeRates getExchange() {
        ExchangeRates exchangeRates = rest.getForObject(paymentsServiceUrl, ExchangeRates.class);
        System.out.println(exchangeRates.getDate());
        return exchangeRates;

    }
}
