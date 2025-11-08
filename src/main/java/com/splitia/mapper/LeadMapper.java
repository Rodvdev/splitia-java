package com.splitia.mapper;

import com.splitia.dto.response.LeadResponse;
import com.splitia.model.sales.Lead;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    LeadMapper INSTANCE = Mappers.getMapper(LeadMapper.class);
    
    @Mapping(target = "assignedToId", source = "assignedTo.id")
    @Mapping(target = "assignedToName", ignore = true)
    @Mapping(target = "contactId", source = "contact.id")
    @Mapping(target = "contactName", ignore = true)
    LeadResponse toResponse(Lead lead);
    
    @AfterMapping
    default void setRelatedNames(@MappingTarget LeadResponse response, Lead lead) {
        if (lead.getAssignedTo() != null) {
            String fullName = lead.getAssignedTo().getName();
            if (lead.getAssignedTo().getLastName() != null && !lead.getAssignedTo().getLastName().isEmpty()) {
                fullName += " " + lead.getAssignedTo().getLastName();
            }
            response.setAssignedToName(fullName);
        }
        if (lead.getContact() != null) {
            String fullName = lead.getContact().getFirstName();
            if (lead.getContact().getLastName() != null && !lead.getContact().getLastName().isEmpty()) {
                fullName += " " + lead.getContact().getLastName();
            }
            response.setContactName(fullName);
        }
    }
    
    List<LeadResponse> toResponseList(List<Lead> leads);
}

