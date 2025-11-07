package com.splitia.mapper;

import com.splitia.dto.response.SettlementResponse;
import com.splitia.model.Settlement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface SettlementMapper {
    SettlementMapper INSTANCE = Mappers.getMapper(SettlementMapper.class);
    
    SettlementResponse toResponse(Settlement settlement);
    
    List<SettlementResponse> toResponseList(List<Settlement> settlements);
}

