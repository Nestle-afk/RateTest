package com.arca.rate.integration;

import com.arca.rate.dto.request.LimitCreateRequest;
import com.arca.rate.dto.response.LimitResponse;
import com.arca.rate.dto.response.TransactionWithLimitResponse;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "alphavantage.api.key=demo")
@ActiveProfiles("test")
class ClientTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("rate")
            .withUsername("rate_user")
            .withPassword("rate_password");

    @Container
    @ServiceConnection
    static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1")
            .withInitScript("init-cassandra.cql");

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
    void test1_getExceededTransactions() {
        Limit limit = limitRepository.saveAndFlush(
                createTestLimit(ExpenseCategory.PRODUCT, BigDecimal.valueOf(1000), BigDecimal.valueOf(50))
        );

        transactionRepository.saveAndFlush(
                createTestTransaction("1234567890", "0987654321", BigDecimal.valueOf(50), false, limit)
        );

        transactionRepository.saveAndFlush(
                createTestTransaction("9999999999", "8888888888", BigDecimal.valueOf(500), true, limit)
        );

        ResponseEntity<List<TransactionWithLimitResponse>> response = restTemplate.exchange(
                "/api/transactions/exceeded", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<TransactionWithLimitResponse>>() {}
        );

        assertAll(
                () -> assertEquals(OK, response.getStatusCode()),
                () -> assertEquals(1, response.getBody().size()),
                () -> assertEquals("9999999999", response.getBody().get(0).getAccountFrom())
        );
    }

    @Test
    void test2_getExceededTransactionsByMonth() {

        Limit limit = limitRepository.saveAndFlush(
                createTestLimit(ExpenseCategory.PRODUCT, BigDecimal.valueOf(1000), BigDecimal.valueOf(50))
        );

        transactionRepository.saveAndFlush(
                createTestTransactionForDate(
                        "1234567890",
                        "0987654321",
                        BigDecimal.valueOf(10000),
                        true,
                        limit,
                        OffsetDateTime.of(2026, 2, 10, 10, 0, 0, 0, ZoneOffset.UTC)
                )
        );

        ResponseEntity<List<TransactionWithLimitResponse>> response = restTemplate.exchange(
                "/api/transactions/exceeded/2026/2", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<TransactionWithLimitResponse>>() {}
        );

        assertAll(
                () -> assertEquals(OK, response.getStatusCode()),
                () -> assertEquals(1, response.getBody().size()),
                () -> assertEquals("1234567890", response.getBody().get(0).getAccountFrom())
        );
    }

    @Test
    void test3_createLimit() {
        LimitCreateRequest request = new LimitCreateRequest(
                BigDecimal.valueOf(500),
                ExpenseCategory.PRODUCT
        );
        var response = restTemplate.postForEntity("/api/limits", request, LimitResponse.class);

        assertEquals(OK, response.getStatusCode());

        LimitResponse limitResponse = response.getBody();
        assertEquals(0, limitResponse.getLimitSum().compareTo(BigDecimal.valueOf(500)));
        assertEquals(ExpenseCategory.PRODUCT, limitResponse.getExpenseCategory());
        assertNotNull(limitResponse.getCreatedAt());
        assertNotNull(limitResponse.getLimitCurrencyShortname());

        List<Limit> limits = limitRepository.findAll();
        assertEquals(1, limits.size());
        assertEquals(0, limits.get(0).getLimitSum().compareTo(BigDecimal.valueOf(500)));
        assertEquals(ExpenseCategory.PRODUCT, limits.get(0).getExpenseCategory());
    }

    private Transaction createTestTransaction(String accountFrom, String accountTo,
                                              BigDecimal sum, boolean shouldExceed, Limit limit) {
        return createTestTransactionForDate(
                accountFrom,
                accountTo,
                sum,
                shouldExceed,
                limit,
                OffsetDateTime.now(ZoneOffset.UTC)
        );
    }

    private Transaction createTestTransactionForDate(String accountFrom, String accountTo,
                                                     BigDecimal sum, boolean shouldExceed,
                                                     Limit limit,
                                                     OffsetDateTime datetime) {
        return Transaction.builder()
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .currencyShortname("KZT")
                .sum(sum)
                .sumUsd(shouldExceed ? BigDecimal.valueOf(10) : BigDecimal.valueOf(1))
                .expenseCategory(ExpenseCategory.PRODUCT)
                .limitExceeded(shouldExceed)
                .limitId(limit.getId())
                .datetime(datetime)
                .build();
    }

    private Limit createTestLimit(ExpenseCategory category, BigDecimal limitSum, BigDecimal remainingSum) {
        return Limit.builder()
                .currencyShortname("USD")
                .expenseCategory(category)
                .limitSum(limitSum)
                .remainingSum(remainingSum)
                .createdAt(OffsetDateTime.of(2026, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .build();
    }
}
