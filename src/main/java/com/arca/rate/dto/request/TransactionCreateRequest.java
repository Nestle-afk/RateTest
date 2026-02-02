package com.arca.rate.dto.request;

import com.arca.rate.model.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateRequest {
    @NotBlank
    @Size(min = 10, max = 10)
    private String accountFrom;
    @NotBlank
    @Size(min = 10, max = 10)
    private String accountTo;
    @NotBlank
    @Size(min = 3, max = 3)
    private String currencyShortname;
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal sum;
    @NotNull
    private ExpenseCategory expenseCategory;
    @NotNull
    private OffsetDateTime datetime;
}
