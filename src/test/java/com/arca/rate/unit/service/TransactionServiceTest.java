package com.arca.rate.unit.service;

import com.arca.rate.dto.request.TransactionCreateRequest;
import com.arca.rate.mapper.TransactionMapper;
import com.arca.rate.model.ExpenseCategory;
import com.arca.rate.model.Limit;
import com.arca.rate.model.Transaction;
import com.arca.rate.repository.TransactionRepository;
import com.arca.rate.service.CurrencyRateService;
import com.arca.rate.service.LimitService;
import com.arca.rate.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrencyRateService currencyRateService;

    @Mock
    private LimitService limitService;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionCreateRequest request;
    private Limit limit;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        request = new TransactionCreateRequest();
        request.setAccountFrom("1234567890");
        request.setAccountTo("0987654321");
        request.setCurrencyShortname("KZT");
        request.setSum(new BigDecimal("10000.00"));
        request.setExpenseCategory(ExpenseCategory.PRODUCT);
        request.setDatetime(OffsetDateTime.of(2022, 1, 2, 10, 0, 0, 0, ZoneOffset.UTC));

        limit = new Limit();
        limit.setId(1L);
        limit.setLimitSum(new BigDecimal("1000.00"));
        limit.setRemainingSum(new BigDecimal("1000.00"));
        limit.setCreatedAt(OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setLimitExceeded(false);
    }

    @Test
    void testProcessTransaction_WithinLimit() {
        when(limitService.getActiveLimit(any(ExpenseCategory.class), any(OffsetDateTime.class))).thenReturn(limit);
        when(currencyRateService.convertToUsd(any(BigDecimal.class), any(String.class), any())).thenReturn(Mono.just(new BigDecimal("100.00")));
        when(transactionMapper.toEntity(any(TransactionCreateRequest.class), any(BigDecimal.class), any(Boolean.class))).thenReturn(transaction);

        transactionService.processTransaction(request);

        assertFalse(transaction.isLimitExceeded());
        assertEquals(new BigDecimal("900.00"), limit.getRemainingSum());
    }
}
