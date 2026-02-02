package com.arca.rate.service;

import com.arca.rate.dto.request.TransactionCreateRequest;
import com.arca.rate.dto.response.TransactionWithLimitResponse;
import com.arca.rate.mapper.LimitMapper;
import com.arca.rate.mapper.TransactionMapper;
import com.arca.rate.model.Limit;
import com.arca.rate.model.Transaction;
import com.arca.rate.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CurrencyRateService currencyRateService;
    private final LimitService limitService;
    private final TransactionMapper transactionMapper;
    private final LimitMapper limitMapper;

    @Transactional
    public void processTransaction(TransactionCreateRequest request) {
        BigDecimal transactionAmountUsd = convertToUsd(request);
        Limit limit = limitService.getActiveLimit(request.getExpenseCategory(), request.getDatetime());
        boolean limitExceeded = limit.getRemainingSum().compareTo(transactionAmountUsd) < 0;

        Transaction transaction = transactionMapper.toEntity(request, transactionAmountUsd, limitExceeded);
        transaction.setLimitId(limit.getId());
        transactionRepository.save(transaction);
        if(!limitExceeded) {
            limit.setRemainingSum(limit.getRemainingSum().subtract(transactionAmountUsd));
            limitService.save(limit);
        }
    }

    public List<TransactionWithLimitResponse> getExceededTransactionsWithLimits() {
        return processExceededTransactions(transactionRepository.findExceededTransactionsWithLimitDetails());
    }

    public List<TransactionWithLimitResponse> getExceededTransactionsByMonth(YearMonth yearMonth) {
        OffsetDateTime start = OffsetDateTime.from(yearMonth.atDay(1).atStartOfDay(ZoneOffset.UTC));
        OffsetDateTime end = OffsetDateTime.from(yearMonth.atEndOfMonth().atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC));
        return processExceededTransactions(transactionRepository.findExceededByPeriodWithLimitDetails(start, end));
    }

    private List<TransactionWithLimitResponse> processExceededTransactions(List<Object[]> results) {
        return results.stream().map(result -> {
            Transaction transaction = (Transaction) result[0];
            Limit limit = (Limit) result[1];
            return transactionMapper.toTransactionWithLimitResponse(transaction, limitMapper.toLimitResponse(limit));
        }).toList();
    }

    private BigDecimal convertToUsd(TransactionCreateRequest request) {
        return currencyRateService.convertToUsd(
                request.getSum(),
                request.getCurrencyShortname(),
                request.getDatetime().toLocalDate()
        ).block();
    }
}
