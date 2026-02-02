package com.arca.rate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_from", nullable = false, length = 10)
    private String accountFrom;

    @Column(name = "account_to", nullable = false, length = 10)
    private String accountTo;

    @Column(name = "currency_shortname", nullable = false, length = 3)
    private String currencyShortname;

    @Column(name = "sum", nullable = false, precision = 15, scale = 2)
    private BigDecimal sum;

    @Column(name = "sum_usd", nullable = false, precision = 15, scale = 2)
    private BigDecimal sumUsd;

    @Column(name = "expense_category", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    @Column(name = "limit_exceeded", nullable = false)
    private boolean limitExceeded;

    @Column(name = "limit_id")
    private Long limitId;

    @Column(name = "datetime", nullable = false)
    private OffsetDateTime datetime;
}

