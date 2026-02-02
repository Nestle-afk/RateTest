package com.arca.rate.unit.service;

import com.arca.rate.dto.request.LimitCreateRequest;
import com.arca.rate.dto.response.LimitResponse;
import com.arca.rate.mapper.LimitMapper;
import com.arca.rate.model.ExpenseCategory;
import com.arca.rate.model.Limit;
import com.arca.rate.repository.LimitRepository;
import com.arca.rate.service.LimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LimitServiceTest {

    @Mock
    private LimitRepository limitRepository;

    @InjectMocks
    private LimitService limitService;

    @Mock
    private LimitMapper limitMapper;

    private Limit limit;

    @BeforeEach
    void setUp() {
        limit = new Limit();
        limit.setId(1L);
        limit.setLimitSum(new BigDecimal("1000.00"));
        limit.setRemainingSum(new BigDecimal("1000.00"));
        limit.setCreatedAt(OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    }

    @Test
    void testGetActiveLimit_ExistingLimit() {
        when(limitRepository.findAndLockActiveLimit(any(ExpenseCategory.class), any(OffsetDateTime.class))).thenReturn(Optional.of(limit));

        Limit result = limitService.getActiveLimit(ExpenseCategory.PRODUCT, OffsetDateTime.now());

        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getLimitSum());
    }

    @Test
    void testGetActiveLimit_NoExistingLimit() {
        when(limitRepository.findAndLockActiveLimit(any(ExpenseCategory.class), any(OffsetDateTime.class))).thenReturn(Optional.empty());
        when(limitRepository.save(any(Limit.class))).thenReturn(limit);

        Limit result = limitService.getActiveLimit(ExpenseCategory.PRODUCT, OffsetDateTime.now());

        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getLimitSum());
    }

    @Test
    void createNewLimit_validRequest_createsAndReturnsResponse() {
        LimitCreateRequest request = new LimitCreateRequest();
        request.setExpenseCategory(ExpenseCategory.PRODUCT);
        request.setLimitSum(new BigDecimal("2000.00"));

        Limit savedLimit = new Limit();
        savedLimit.setId(2L);
        savedLimit.setLimitSum(new BigDecimal("2000.00"));

        when(limitRepository.save(any(Limit.class))).thenReturn(savedLimit);
        when(limitMapper.toLimitResponse(savedLimit)).thenReturn(new LimitResponse());

        LimitResponse result = limitService.createNewLimit(request);

        assertNotNull(result);
        verify(limitRepository).save(any(Limit.class));
        verify(limitMapper).toLimitResponse(savedLimit);
    }

    @Test
    void save_callsRepositorySave() {
        Limit limitToSave = new Limit();
        limitToSave.setId(1L);
        when(limitRepository.save(limitToSave)).thenReturn(limitToSave);

        Limit result = limitService.save(limitToSave);

        assertNotNull(result);
        verify(limitRepository).save(limitToSave);
    }
}
