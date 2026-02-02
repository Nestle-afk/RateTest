package com.arca.rate.controller;

import com.arca.rate.dto.request.LimitCreateRequest;
import com.arca.rate.dto.response.LimitResponse;
import com.arca.rate.dto.response.TransactionWithLimitResponse;
import com.arca.rate.service.LimitService;
import com.arca.rate.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Client API", description = "Endpoints for client operations")
public class ClientController {
    private final TransactionService transactionService;
    private final LimitService limitService;

    @GetMapping("/transactions/exceeded")
    @Operation(summary = "Get all exceeded transactions",
            description = "Returns a list of all transactions that exceeded their limits")
    public ResponseEntity<List<TransactionWithLimitResponse>> getExceededTransactions() {
        return ResponseEntity.ok(transactionService.getExceededTransactionsWithLimits());
    }

    @GetMapping("/transactions/exceeded/{year}/{month}")
    @Operation(summary = "Get exceeded transactions by month",
            description = "Returns transactions that exceeded their limits for a specific month")
    public ResponseEntity<List<TransactionWithLimitResponse>> getExceededTransactionsByMonth(@PathVariable int year,
                                                                                             @PathVariable int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return ResponseEntity.ok(transactionService.getExceededTransactionsByMonth(yearMonth));
    }

    @PostMapping("/limits")
    @Operation(summary = "Create a new limit",
            description = "Creates a new spending limit for a specific expense category")
    public ResponseEntity<LimitResponse> createLimit(@Valid @RequestBody LimitCreateRequest request) {
        return ResponseEntity.ok(limitService.createNewLimit(request));
    }
}
