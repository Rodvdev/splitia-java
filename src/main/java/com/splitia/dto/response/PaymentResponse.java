package com.splitia.dto.response;

import com.splitia.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private BigDecimal amount;
    private LocalDate date;
    private PaymentMethod paymentMethod;
    private String reference;
    private Boolean isReconciled;
    private String currency;
    private UUID invoiceId;
    private String invoiceNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

