package com.arca.rate.model;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("currency_rate")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRate {
    @PrimaryKeyColumn(name = "currency_from", type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = CassandraType.Name.TEXT)
    private String currencyFrom;

    @PrimaryKeyColumn(name = "currency_to", type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = CassandraType.Name.TEXT)
    private String currencyTo;

    @PrimaryKeyColumn(name = "rate_date", type = PrimaryKeyType.CLUSTERED)
    @CassandraType(type = CassandraType.Name.DATE)
    private LocalDate rateDate;

    @Column("close_rate")
    @CassandraType(type = CassandraType.Name.DECIMAL)
    private BigDecimal closeRate;

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private LocalDateTime createdAt;
}
