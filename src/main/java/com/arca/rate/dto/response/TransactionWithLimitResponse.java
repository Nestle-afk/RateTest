package com.arca.rate.dto.response;

import com.arca.rate.model.ExpenseCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class TransactionWithLimitResponse {
    private String accountFrom;
    private String accountTo;
    private String currencyShortname;
    private BigDecimal sum;
    private ExpenseCategory expenseCategory;
    private OffsetDateTime datetime;
    private LimitResponse limit;
}
