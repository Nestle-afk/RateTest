package com.arca.rate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "limits",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_limits_category_datetime",
                columnNames = {"expense_category", "limit_datetime"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "limit_sum", nullable = false, precision = 15, scale = 2)
    private BigDecimal limitSum;

    @Column(name = "remaining_sum", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingSum;

    @Column(name = "currency_shortname", nullable = false, length = 3)
    private String currencyShortname =  CurrencyCode.USD;

    @Column(name = "expense_category", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}

