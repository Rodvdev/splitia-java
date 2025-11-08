package com.splitia.mapper;

import com.splitia.dto.response.OpportunityResponse;
import com.splitia.model.sales.Opportunity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OpportunityMapper {
    OpportunityMapper INSTANCE = Mappers.getMapper(OpportunityMapper.class);
    
    @Mapping(target = "assignedToId", source = "assignedTo.id")
    @Mapping(target = "assignedToName", ignore = true)
    @Mapping(target = "contactId", source = "contact.id")
    @Mapping(target = "contactName", ignore = true)
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", ignore = true)
    OpportunityResponse toResponse(Opportunity opportunity);
    
    @AfterMapping
    default void setRelatedNames(@MappingTarget OpportunityResponse response, Opportunity opportunity) {
        if (opportunity.getAssignedTo() != null) {
            String fullName = opportunity.getAssignedTo().getName();
            if (opportunity.getAssignedTo().getLastName() != null && !opportunity.getAssignedTo().getLastName().isEmpty()) {
                fullName += " " + opportunity.getAssignedTo().getLastName();
            }
            response.setAssignedToName(fullName);
        }
        if (opportunity.getContact() != null) {
            String fullName = opportunity.getContact().getFirstName();
            if (opportunity.getContact().getLastName() != null && !opportunity.getContact().getLastName().isEmpty()) {
                fullName += " " + opportunity.getContact().getLastName();
            }
            response.setContactName(fullName);
        }
        if (opportunity.getCompany() != null) {
            response.setCompanyName(opportunity.getCompany().getName());
        }
    }
    
    List<OpportunityResponse> toResponseList(List<Opportunity> opportunities);
}

