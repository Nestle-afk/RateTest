package com.arca.rate.service;

import com.arca.rate.dto.request.LimitCreateRequest;
import com.arca.rate.dto.response.LimitResponse;
import com.arca.rate.mapper.LimitMapper;
import com.arca.rate.model.CurrencyCode;
import com.arca.rate.model.ExpenseCategory;
import com.arca.rate.model.Limit;
import com.arca.rate.repository.LimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class LimitService {

    private static final BigDecimal DEFAULT_LIMIT = new BigDecimal("1000.00");
    private final LimitRepository limitRepository;
    private final LimitMapper limitMapper;

    /**
     * Will create new Limit with transaction creation date if no limit is present.
     **/
    public Limit getActiveLimit(ExpenseCategory category, OffsetDateTime transactionDate) {
        return limitRepository.findAndLockActiveLimit(category, transactionDate)
                .orElseGet(() -> createDefaultLimit(category, transactionDate));
    }

    private Limit createDefaultLimit(ExpenseCategory category, OffsetDateTime transactionDate) {
        Limit defaultLimit = Limit.builder()
                .limitSum(DEFAULT_LIMIT)
                .remainingSum(DEFAULT_LIMIT)
                .createdAt(transactionDate)
                .currencyShortname(CurrencyCode.USD)
                .expenseCategory(category)
                .build();
        return limitRepository.save(defaultLimit);
    }

    public LimitResponse createNewLimit(LimitCreateRequest request) {
        Limit newLimit = Limit.builder()
                .expenseCategory(request.getExpenseCategory())
                .currencyShortname(CurrencyCode.USD)
                .limitSum(request.getLimitSum())
                .remainingSum(request.getLimitSum())
                .createdAt(OffsetDateTime.now())
                .build();
        return limitMapper.toLimitResponse(limitRepository.save(newLimit));
    }

    public Limit save(Limit limit) {
        return limitRepository.save(limit);
    }
}
