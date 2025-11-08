package com.splitia.dto.request;

import com.splitia.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    @Size(max = 255, message = "Reference must be less than 255 characters")
    private String reference;
    
    @Size(max = 10)
    private String currency = "USD";
    
    private UUID invoiceId;
    
    @Size(max = 10000, message = "Notes must be less than 10000 characters")
    private String notes;
}

