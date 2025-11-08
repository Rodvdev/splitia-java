package com.splitia.mapper;

import com.splitia.dto.response.InvoiceItemResponse;
import com.splitia.dto.response.InvoiceResponse;
import com.splitia.model.finance.Invoice;
import com.splitia.model.finance.InvoiceItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceMapper INSTANCE = Mappers.getMapper(InvoiceMapper.class);
    
    @Mapping(target = "contactId", source = "contact.id")
    @Mapping(target = "contactName", ignore = true)
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", ignore = true)
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "paidAmount", ignore = true)
    @Mapping(target = "remainingAmount", ignore = true)
    InvoiceResponse toResponse(Invoice invoice);
    
    @AfterMapping
    default void setRelatedData(@MappingTarget InvoiceResponse response, Invoice invoice) {
        if (invoice.getContact() != null) {
            String fullName = invoice.getContact().getFirstName();
            if (invoice.getContact().getLastName() != null && !invoice.getContact().getLastName().isEmpty()) {
                fullName += " " + invoice.getContact().getLastName();
            }
            response.setContactName(fullName);
        }
        if (invoice.getCompany() != null) {
            response.setCompanyName(invoice.getCompany().getName());
        }
        if (invoice.getCreatedBy() != null) {
            String fullName = invoice.getCreatedBy().getName();
            if (invoice.getCreatedBy().getLastName() != null && !invoice.getCreatedBy().getLastName().isEmpty()) {
                fullName += " " + invoice.getCreatedBy().getLastName();
            }
            response.setCreatedByName(fullName);
        }
        if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
            response.setItems(invoice.getItems().stream()
                    .map(this::itemToResponse)
                    .toList());
        }
        // Calculate paid amount
        BigDecimal paidAmount = invoice.getPayments() != null ?
                invoice.getPayments().stream()
                        .filter(p -> p.getDeletedAt() == null)
                        .map(p -> p.getAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add) :
                BigDecimal.ZERO;
        response.setPaidAmount(paidAmount);
        response.setRemainingAmount(invoice.getTotal().subtract(paidAmount));
    }
    
    InvoiceItemResponse itemToResponse(InvoiceItem item);
    
    List<InvoiceResponse> toResponseList(List<Invoice> invoices);
}

