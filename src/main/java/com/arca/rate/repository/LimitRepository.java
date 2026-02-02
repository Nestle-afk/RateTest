package com.arca.rate.repository;

import com.arca.rate.model.ExpenseCategory;
import com.arca.rate.model.Limit;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {
    /*
    Lock for case of multiple simultaneous transactions
     */
    @Query("""
            SELECT l
            FROM Limit l
            WHERE l.expenseCategory = :category
              AND l.createdAt <= :transactionDate
            ORDER BY l.createdAt DESC
            LIMIT 1
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Limit> findAndLockActiveLimit(@Param("category") ExpenseCategory category,
                                           @Param("transactionDate") OffsetDateTime transactionDate);
}
