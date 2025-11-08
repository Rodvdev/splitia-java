package com.splitia.mapper;

import com.splitia.dto.response.ContactResponse;
import com.splitia.dto.response.ContactTagResponse;
import com.splitia.model.contact.Contact;
import com.splitia.model.contact.ContactTag;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);
    
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", ignore = true)
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", ignore = true)
    @Mapping(target = "tags", ignore = true)
    ContactResponse toResponse(Contact contact);
    
    @AfterMapping
    default void setRelatedNames(@MappingTarget ContactResponse response, Contact contact) {
        if (contact.getOwner() != null) {
            String fullName = contact.getOwner().getName();
            if (contact.getOwner().getLastName() != null && !contact.getOwner().getLastName().isEmpty()) {
                fullName += " " + contact.getOwner().getLastName();
            }
            response.setOwnerName(fullName);
        }
        if (contact.getCompany() != null) {
            response.setCompanyName(contact.getCompany().getName());
        }
        if (contact.getTags() != null && !contact.getTags().isEmpty()) {
            response.setTags(contact.getTags().stream()
                    .map(this::tagToResponse)
                    .collect(Collectors.toList()));
        }
    }
    
    default ContactTagResponse tagToResponse(ContactTag tag) {
        ContactTagResponse response = new ContactTagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setColor(tag.getColor());
        return response;
    }
    
    List<ContactResponse> toResponseList(List<Contact> contacts);
}

