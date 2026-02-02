package com.arca.rate.mapper;

import com.arca.rate.dto.response.LimitResponse;
import com.arca.rate.model.Limit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LimitMapper {
    @Mapping(target = "limitCurrencyShortname", source = "limit.currencyShortname")
    LimitResponse toLimitResponse(Limit limit);
}

