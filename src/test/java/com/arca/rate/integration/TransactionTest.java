package com.arca.rate.integration;

import com.arca.rate.dto.request.TransactionCreateRequest;
import com.arca.rate.model.ExpenseCategory;
import com.arca.rate.model.Limit;
import com.arca.rate.model.Transaction;
import com.arca.rate.repository.LimitRepository;
import com.arca.rate.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("rate")
            .withUsername("rate_user")
            .withPassword("rate_password");

    @Container
    @ServiceConnection
    static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LimitRepository limitRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        limitRepository.deleteAll();
    }

    @Test
    void test1_transactionNotExceedsLimit() {
        Limit limit = Limit.builder()
                .currencyShortname("USD")
                .expenseCategory(ExpenseCategory.PRODUCT)
                .limitSum(BigDecimal.valueOf(1000))
                .remainingSum(BigDecimal.valueOf(1000))
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1))
                .build();
        limitRepository.saveAndFlush(limit);

        TransactionCreateRequest request = new TransactionCreateRequest(
                "1234567890",
                "0987654321",
                "RUB",
                BigDecimal.valueOf(1000),
                ExpenseCategory.PRODUCT,
                OffsetDateTime.now(ZoneOffset.UTC)
        );
        var response = restTemplate.postForEntity("/api/transactions", request, Void.class);

        assertEquals(CREATED, response.getStatusCode());

        Transaction transaction = transactionRepository.findAll().get(0);
        assertFalse(transaction.isLimitExceeded());

        BigDecimal transactionUsd = transaction.getSumUsd();
        assertTrue(transactionUsd.compareTo(BigDecimal.ZERO) > 0);

        Limit updatedLimit = limitRepository.findById(limit.getId()).orElseThrow();

        BigDecimal expectedRemaining = BigDecimal.valueOf(1000).subtract(transactionUsd);
        assertEquals(0, updatedLimit.getRemainingSum().compareTo(expectedRemaining));

        assertTrue(updatedLimit.getRemainingSum().compareTo(BigDecimal.valueOf(1000)) < 0);
    }

    @Test
    void test2_transactionExceedsLimit() {
        Limit limit = Limit.builder()
                .currencyShortname("USD")
                .expenseCategory(ExpenseCategory.PRODUCT)
                .limitSum(BigDecimal.valueOf(100))
                .remainingSum(BigDecimal.valueOf(20))
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1))
                .build();
        limitRepository.saveAndFlush(limit);

        TransactionCreateRequest request = new TransactionCreateRequest(
                "1234567890",
                "0987654321",
                "RUB",
                BigDecimal.valueOf(500000),
                ExpenseCategory.PRODUCT,
                OffsetDateTime.now(ZoneOffset.UTC)
        );
        restTemplate.postForEntity("/api/transactions", request, Void.class);

        Transaction tx = transactionRepository.findAll().get(0);
        assertTrue(tx.isLimitExceeded());

        Limit unchangedLimit = limitRepository.findById(limit.getId()).orElseThrow();
        assertEquals(0, unchangedLimit.getRemainingSum().compareTo(BigDecimal.valueOf(20)));
    }

    @Test
    void test3_invalidCurrency() {
        Limit limit = Limit.builder()
                .currencyShortname("USD")
                .expenseCategory(ExpenseCategory.PRODUCT)
                .limitSum(BigDecimal.valueOf(1000))
                .remainingSum(BigDecimal.valueOf(1000))
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1))
                .build();
        limitRepository.saveAndFlush(limit);

        TransactionCreateRequest request = new TransactionCreateRequest(
                "1234567890",
                "0987654321",
                "EUR",
                BigDecimal.valueOf(50),
                ExpenseCategory.PRODUCT,
                OffsetDateTime.now(ZoneOffset.UTC)
        );
        var response = restTemplate.postForEntity("/api/transactions", request, Void.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(transactionRepository.findAll().isEmpty());
    }
}
