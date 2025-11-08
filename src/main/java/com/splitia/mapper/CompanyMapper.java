package com.splitia.mapper;

import com.splitia.dto.response.CompanyResponse;
import com.splitia.model.contact.Company;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);
    
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", ignore = true)
    CompanyResponse toResponse(Company company);
    
    @AfterMapping
    default void setOwnerName(@MappingTarget CompanyResponse response, Company company) {
        if (company.getOwner() != null) {
            String fullName = company.getOwner().getName();
            if (company.getOwner().getLastName() != null && !company.getOwner().getLastName().isEmpty()) {
                fullName += " " + company.getOwner().getLastName();
            }
            response.setOwnerName(fullName);
        }
    }
    
    List<CompanyResponse> toResponseList(List<Company> companies);
}

