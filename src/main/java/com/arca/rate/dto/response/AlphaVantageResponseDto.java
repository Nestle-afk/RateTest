package com.arca.rate.dto.response;

import com.arca.rate.model.CurrencyRate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class AlphaVantageResponseDto {
    public static final String TIME_SERIES_PROPERTY = "Time Series FX (Daily)";
    public static final String CLOSE_RATE_PROPERTY = "4. close";

    @JsonProperty(TIME_SERIES_PROPERTY)
    @NotNull(message = "Time series data is required")
    private Map<String, DailyRate> timeSeries;

    @Data
    public static class DailyRate {
        @JsonProperty(CLOSE_RATE_PROPERTY)
        @NotNull(message = "Close rate is required")
        private BigDecimal close;
    }

    public List<CurrencyRate> toCurrencyRates(String from, String to) {
        List<CurrencyRate> result = new ArrayList<>();
        for (Map.Entry<String, DailyRate> entry : timeSeries.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey());
            BigDecimal close = entry.getValue().getClose();
            result.add(new CurrencyRate(from, to, date, close, LocalDateTime.now()));
        }
        return result;
    }
}

