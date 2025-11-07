package com.splitia.mapper;

import com.splitia.dto.response.ExpenseResponse;
import com.splitia.dto.response.ExpenseShareResponse;
import com.splitia.model.Expense;
import com.splitia.model.ExpenseShare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, GroupMapper.class, CategoryMapper.class})
public interface ExpenseMapper {
    ExpenseMapper INSTANCE = Mappers.getMapper(ExpenseMapper.class);
    
    ExpenseResponse toResponse(Expense expense);
    
    List<ExpenseResponse> toResponseList(List<Expense> expenses);
    
    @Mapping(source = "user", target = "user")
    ExpenseShareResponse toShareResponse(ExpenseShare expenseShare);
    
    List<ExpenseShareResponse> toShareResponseList(List<ExpenseShare> expenseShares);
}

