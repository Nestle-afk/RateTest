package com.arca.rate.repository;

import com.arca.rate.model.ExpenseCategory;
import com.arca.rate.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
            SELECT
                CASE WHEN (COALESCE(SUM(t.sumUsd), 0) + :currentAmount) > :limitAmount
                THEN true
                ELSE false
                END
            FROM Transaction t
            WHERE t.expenseCategory = :category
              AND t.limitId = :limitId
              AND t.datetime >= :periodStart
              AND t.datetime < :transactionDate
            """)
    boolean checkLimitExceeded(@Param("category") ExpenseCategory category,
                               @Param("limitId") Long limitId,
                               @Param("periodStart") OffsetDateTime periodStart,
                               @Param("transactionDate") OffsetDateTime transactionDate,
                               @Param("currentAmount") BigDecimal currentAmount,
                               @Param("limitAmount") BigDecimal limitAmount);

    @Query("""
            SELECT t, l
            FROM Transaction t
            JOIN Limit l ON t.limitId = l.id
            WHERE t.limitExceeded = true
            ORDER BY t.datetime DESC
            """)
    List<Object[]> findExceededTransactionsWithLimitDetails();

    @Query("""
            SELECT t, l
            FROM Transaction t
            JOIN Limit l ON t.limitId = l.id
            WHERE t.limitExceeded = true
              AND t.datetime BETWEEN :start AND :end
            ORDER BY t.datetime DESC
            """)
    List<Object[]> findExceededByPeriodWithLimitDetails(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}
