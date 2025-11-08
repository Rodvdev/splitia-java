package com.splitia.mapper;

import com.splitia.dto.response.ActivityResponse;
import com.splitia.model.sales.Activity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);
    
    @Mapping(target = "opportunityId", source = "opportunity.id")
    @Mapping(target = "opportunityName", ignore = true)
    @Mapping(target = "leadId", source = "lead.id")
    @Mapping(target = "leadName", ignore = true)
    @Mapping(target = "contactId", source = "contact.id")
    @Mapping(target = "contactName", ignore = true)
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", ignore = true)
    @Mapping(target = "assignedToId", source = "assignedTo.id")
    @Mapping(target = "assignedToName", ignore = true)
    ActivityResponse toResponse(Activity activity);
    
    @AfterMapping
    default void setRelatedNames(@MappingTarget ActivityResponse response, Activity activity) {
        if (activity.getOpportunity() != null) {
            response.setOpportunityName(activity.getOpportunity().getName());
        }
        if (activity.getLead() != null) {
            String fullName = activity.getLead().getFirstName();
            if (activity.getLead().getLastName() != null && !activity.getLead().getLastName().isEmpty()) {
                fullName += " " + activity.getLead().getLastName();
            }
            response.setLeadName(fullName);
        }
        if (activity.getContact() != null) {
            String fullName = activity.getContact().getFirstName();
            if (activity.getContact().getLastName() != null && !activity.getContact().getLastName().isEmpty()) {
                fullName += " " + activity.getContact().getLastName();
            }
            response.setContactName(fullName);
        }
        if (activity.getCreatedBy() != null) {
            String fullName = activity.getCreatedBy().getName();
            if (activity.getCreatedBy().getLastName() != null && !activity.getCreatedBy().getLastName().isEmpty()) {
                fullName += " " + activity.getCreatedBy().getLastName();
            }
            response.setCreatedByName(fullName);
        }
        if (activity.getAssignedTo() != null) {
            String fullName = activity.getAssignedTo().getName();
            if (activity.getAssignedTo().getLastName() != null && !activity.getAssignedTo().getLastName().isEmpty()) {
                fullName += " " + activity.getAssignedTo().getLastName();
            }
            response.setAssignedToName(fullName);
        }
    }
    
    List<ActivityResponse> toResponseList(List<Activity> activities);
}

