package com.splitia.mapper;

import com.splitia.dto.response.PlanResponse;
import com.splitia.model.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanMapper {
    PlanMapper INSTANCE = Mappers.getMapper(PlanMapper.class);
    
    PlanResponse toResponse(Plan plan);
    
    List<PlanResponse> toResponseList(List<Plan> plans);
}

