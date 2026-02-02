package com.arca.rate.repository.cassandra;

import com.arca.rate.model.CurrencyRate;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface CurrencyRateRepository extends ReactiveCassandraRepository<CurrencyRate, String> {

    @Query("""
            SELECT *
            FROM currency_rate
            WHERE currency_from = :from
              AND currency_to = :to
              AND rate_date <= :date
            ORDER BY rate_date DESC
            LIMIT 1
            """)
    Mono<CurrencyRate> findClosestPreviousRate(String from, String to, LocalDate date);

    @Query("""
            SELECT *
            FROM currency_rate
            WHERE currency_from = :from
              AND currency_to = :to
              AND rate_date = :date
            LIMIT 1
            """)
    Mono<CurrencyRate> findFirstForDate(String from, String to, LocalDate date);
}
