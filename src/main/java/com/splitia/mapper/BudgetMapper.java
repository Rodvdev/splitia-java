package com.splitia.mapper;

import com.splitia.dto.response.BudgetResponse;
import com.splitia.model.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface BudgetMapper {
    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);
    
    BudgetResponse toResponse(Budget budget);
    
    List<BudgetResponse> toResponseList(List<Budget> budgets);
}

