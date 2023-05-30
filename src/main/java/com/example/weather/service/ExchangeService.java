package com.example.weather.service;

import com.example.weather.component.BotProxy;
import com.example.weather.models.ExchangeRates;
import com.example.weather.models.Message;
import com.example.weather.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final BotProxy proxy;

    public String getAllExchange(){
        ExchangeRates exchangeRates = proxy.getExchange();
        String cur = "1 USD: " + round(Double.parseDouble(exchangeRates.getRates().getEUR())) + " EUR" +
                "\n1 USD: " +  round(Double.parseDouble(exchangeRates.getRates().getRUB())) + " RUB" +
                "\n1 USD: " + round(Double.parseDouble(exchangeRates.getRates().getKZT())) + " KZT";

        return cur;
    }
    public String convertUSDToKZT(double val) {
        double usd = Double.parseDouble(proxy.getExchange().getRates().getKZT());
        double kzt = val*usd;
        kzt = round(kzt);
        return String.valueOf(kzt) + " тенге";
    }
    public String convertKZTToUSD(double val){
        double kzt = Double.parseDouble(proxy.getExchange().getRates().getKZT());
        double usd = val/kzt;
        usd = round(usd);
        return String.valueOf(usd) + " $";

    }
    public double round(double val){ //округление чисел
        return Math.round(val * 100.0) / 100.0;
    }

}
