package com.arca.rate.controller;

import com.arca.rate.dto.request.TransactionCreateRequest;
import com.arca.rate.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction API", description = "Endpoints for managing transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create a new transaction", description = "Processes and stores a new transaction")
    public ResponseEntity<Void> createTransaction(
            @Valid @RequestBody TransactionCreateRequest request
    ) {
        transactionService.processTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
