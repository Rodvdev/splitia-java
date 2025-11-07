package com.splitia.mapper;

import com.splitia.dto.response.CategoryResponse;
import com.splitia.model.CustomCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
    
    CategoryResponse toResponse(CustomCategory category);
    
    List<CategoryResponse> toResponseList(List<CustomCategory> categories);
}

