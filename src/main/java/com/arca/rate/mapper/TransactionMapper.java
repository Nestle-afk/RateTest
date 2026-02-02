package com.arca.rate.mapper;

import com.arca.rate.dto.request.TransactionCreateRequest;
import com.arca.rate.dto.response.LimitResponse;
import com.arca.rate.dto.response.TransactionWithLimitResponse;
import com.arca.rate.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sumUsd", source = "sumUsd")
    @Mapping(target = "expenseCategory", source = "request.expenseCategory")
    @Mapping(target = "limitExceeded", source = "limitExceeded")
    Transaction toEntity(
            TransactionCreateRequest request,
            BigDecimal sumUsd,
            boolean limitExceeded
    );

    @Mapping(target = "expenseCategory", source = "transaction.expenseCategory")
    @Mapping(target = "limit", source = "limit")
    TransactionWithLimitResponse toTransactionWithLimitResponse(
            Transaction transaction,
            LimitResponse limit
    );
}
