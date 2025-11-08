package com.splitia.mapper;

import com.splitia.dto.response.CategoryResponse;
import com.splitia.model.CustomCategory;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
    
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", ignore = true)
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", ignore = true)
    CategoryResponse toResponse(CustomCategory category);
    
    @AfterMapping
    default void setGroupAndCreatorNames(@MappingTarget CategoryResponse response, CustomCategory category) {
        if (category.getGroup() != null) {
            response.setGroupName(category.getGroup().getName());
        }
        if (category.getCreatedBy() != null) {
            String fullName = category.getCreatedBy().getName();
            if (category.getCreatedBy().getLastName() != null && !category.getCreatedBy().getLastName().isEmpty()) {
                fullName += " " + category.getCreatedBy().getLastName();
            }
            response.setCreatedByName(fullName);
        }
    }
    
    List<CategoryResponse> toResponseList(List<CustomCategory> categories);
}

