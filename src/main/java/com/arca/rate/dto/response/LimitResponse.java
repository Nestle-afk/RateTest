package com.arca.rate.dto.response;

import com.arca.rate.model.ExpenseCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class LimitResponse {
    private BigDecimal limitSum;
    private OffsetDateTime createdAt;
    private ExpenseCategory expenseCategory;
    private String limitCurrencyShortname;
}
