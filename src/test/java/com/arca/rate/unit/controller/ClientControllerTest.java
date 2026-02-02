package com.arca.rate.unit.controller;

import com.arca.rate.controller.ClientController;
import com.arca.rate.dto.request.LimitCreateRequest;
import com.arca.rate.dto.response.LimitResponse;
import com.arca.rate.dto.response.TransactionWithLimitResponse;
import com.arca.rate.service.LimitService;
import com.arca.rate.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private LimitService limitService;

    @InjectMocks
    private ClientController clientController;

    @Test
    void testGetExceededTransactions() {
        List<TransactionWithLimitResponse> transactions = Collections.emptyList();
        when(transactionService.getExceededTransactionsWithLimits()).thenReturn(transactions);

        ResponseEntity<List<TransactionWithLimitResponse>> response = clientController.getExceededTransactions();

        assertEquals(transactions, response.getBody());
        verify(transactionService).getExceededTransactionsWithLimits();
    }

    @Test
    void testGetExceededTransactionsByMonth() {
        List<TransactionWithLimitResponse> transactions = Collections.emptyList();
        YearMonth yearMonth = YearMonth.of(2022, 1);
        when(transactionService.getExceededTransactionsByMonth(yearMonth)).thenReturn(transactions);

        ResponseEntity<List<TransactionWithLimitResponse>> response = clientController.getExceededTransactionsByMonth(2022, 1);

        assertEquals(transactions, response.getBody());
        verify(transactionService).getExceededTransactionsByMonth(yearMonth);
    }

    @Test
    void testCreateLimit() {
        LimitCreateRequest request = new LimitCreateRequest();
        LimitResponse limitResponse = new LimitResponse();
        when(limitService.createNewLimit(request)).thenReturn(limitResponse);

        ResponseEntity<LimitResponse> response = clientController.createLimit(request);

        assertEquals(limitResponse, response.getBody());
        verify(limitService).createNewLimit(request);
    }
}
