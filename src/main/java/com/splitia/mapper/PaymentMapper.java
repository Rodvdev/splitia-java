package com.splitia.mapper;

import com.splitia.dto.response.PaymentResponse;
import com.splitia.model.finance.Payment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);
    
    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "invoiceNumber", ignore = true)
    PaymentResponse toResponse(Payment payment);
    
    @AfterMapping
    default void setInvoiceNumber(@MappingTarget PaymentResponse response, Payment payment) {
        if (payment.getInvoice() != null) {
            response.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
        }
    }
    
    List<PaymentResponse> toResponseList(List<Payment> payments);
}

