package com.arca.rate.unit.service;

import com.arca.rate.model.CurrencyRate;
import com.arca.rate.repository.cassandra.CurrencyRateRepository;
import com.arca.rate.service.CurrencyRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceTest {

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    private CurrencyRateService currencyRateService;

    private CurrencyRate currencyRate;

    @BeforeEach
    void setUp() {
        currencyRate = new CurrencyRate();
        currencyRate.setCloseRate(new BigDecimal("0.02"));
    }

    @Test
    void convertToUsd_supportedCurrency_returnsConvertedAmount() {
        when(currencyRateRepository.findClosestPreviousRate(eq("KZT"), eq("USD"), any(LocalDate.class)))
                .thenReturn(Mono.just(currencyRate));

        Mono<BigDecimal> result = currencyRateService.convertToUsd(new BigDecimal("1000"), "KZT", LocalDate.now());

        StepVerifier.create(result)
                .expectNext(new BigDecimal("20.00"))
                .verifyComplete();
        verify(currencyRateRepository).findClosestPreviousRate(eq("KZT"), eq("USD"), any(LocalDate.class));
    }

    @Test
    void convertToUsd_unsupportedCurrency_returnsError() {
        Mono<BigDecimal> result = currencyRateService.convertToUsd(new BigDecimal("100"), "EUR", LocalDate.now());

        StepVerifier.create(result)
                .expectError()
                .verify();
        verify(currencyRateRepository, never()).findClosestPreviousRate(anyString(), anyString(), any(LocalDate.class));
    }
}
