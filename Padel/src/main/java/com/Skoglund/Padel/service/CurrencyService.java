package com.Skoglund.Padel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private static final BigDecimal FALLBACK_RATE = new BigDecimal("0.0886");

    private final RestClient restClient;

    public CurrencyService(@Value("${currency.service.url}") String currencyServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(currencyServiceUrl)
                .build();
    }

    public BigDecimal convertSekToEur(BigDecimal amountSek) {
        try {
            BigDecimal rate = restClient.get()
                    .uri("/api/currency/rate?from=SEK&to=EUR")
                    .retrieve()
                    .body(BigDecimal.class);

            if (rate != null) {
                logger.info("Hämtade växelkurs SEK->EUR: {}", rate);
                return amountSek.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            logger.warn("Valutatjänsten otillgänglig, använder fast kurs. Fel: {}", e.getMessage());
        }
        return amountSek.multiply(FALLBACK_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}