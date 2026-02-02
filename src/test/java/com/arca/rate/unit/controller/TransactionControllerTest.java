package com.arca.rate.unit.controller;

import com.arca.rate.controller.TransactionController;
import com.arca.rate.dto.request.TransactionCreateRequest;
import com.arca.rate.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void testCreateTransaction() {
        TransactionCreateRequest request = new TransactionCreateRequest();

        ResponseEntity<Void> response = transactionController.createTransaction(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService).processTransaction(request);
    }
}
