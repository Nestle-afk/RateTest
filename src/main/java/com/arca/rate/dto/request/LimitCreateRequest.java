package com.arca.rate.dto.request;

import com.arca.rate.model.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LimitCreateRequest {
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal limitSum;
    @NotNull
    private ExpenseCategory expenseCategory;
}
