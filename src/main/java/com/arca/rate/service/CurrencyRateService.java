package com.arca.rate.service;

import com.arca.rate.dto.response.AlphaVantageResponseDto;
import com.arca.rate.model.CurrencyCode;
import com.arca.rate.model.CurrencyRate;
import com.arca.rate.repository.cassandra.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateService {

    private final CurrencyRateRepository currencyRateRepository;
    private final WebClient webClient;
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of(CurrencyCode.RUB, CurrencyCode.KZT);

    @Value("${alphavantage.api.key}")
    private String apiKey;

    public Mono<BigDecimal> convertToUsd(BigDecimal amount, String from, LocalDate date) {
        if (!SUPPORTED_CURRENCIES.contains(from.toUpperCase())) {
            log.warn("Unsupported currency requested: {}", from);
            return Mono.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Currency is not supported, supported currencies" + SUPPORTED_CURRENCIES));
        }

        return getCurrencyToUsdRate(from, date)
                .map(amount::multiply)
                .doOnError(e -> log.error("Conversion error when converting from {} to {}: {}",
                        from, CurrencyCode.USD, e.getMessage()));
    }

    private Mono<BigDecimal> getCurrencyToUsdRate(String from, LocalDate date){
        return getCurrencyRate(from, CurrencyCode.USD, date);
    }

    private Mono<BigDecimal> getCurrencyRate(String from, String to, LocalDate date) {
        return currencyRateRepository.findClosestPreviousRate(from, to, date)
                .map(CurrencyRate::getCloseRate)
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Conversion rate not found", e)));
    }

    /**
     * Request every hour in case of unexpected shutdown/network problems
     * Check which rates need update before because API calls is expensive
     **/
    @Scheduled(cron = "0 0 * * * *")
    public void updateRates() {
        Set<String> missingRates = SUPPORTED_CURRENCIES.stream()
                .filter(currency ->
                        currencyRateRepository.findFirstForDate(currency, CurrencyCode.USD, LocalDate.now())
                                .block() == null
                )
                .collect(Collectors.toSet());
        log.info("Updating USD exchange rates for currencies: {}", missingRates);
        missingRates.forEach(rate -> loadRates(rate, CurrencyCode.USD)
                .flatMap(currencyRateRepository::save)
                .blockLast());
    }

    private Flux<CurrencyRate> loadRates(String from, String to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/query")
                        .queryParam("function", "FX_DAILY")
                        .queryParam("from_symbol", from)
                        .queryParam("to_symbol", to)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(AlphaVantageResponseDto.class)
                .flatMapMany(dto -> Flux.fromIterable(dto.toCurrencyRates(from, to)));
    }
}
