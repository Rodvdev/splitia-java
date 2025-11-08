package com.splitia.dto.request;

import com.splitia.model.enums.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {
    @Size(max = 50, message = "Invoice number must be less than 50 characters")
    private String invoiceNumber; // Auto-generated if not provided
    
    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    
    @NotNull(message = "Subtotal is required")
    private BigDecimal subtotal;
    
    private BigDecimal tax = BigDecimal.ZERO;
    
    @NotNull(message = "Total is required")
    private BigDecimal total;
    
    @Size(max = 10)
    private String currency = "USD";
    
    @Size(max = 10000, message = "Notes must be less than 10000 characters")
    private String notes;
    
    private UUID contactId;
    
    private UUID companyId;
    
    @Valid
    @NotNull(message = "Items are required")
    private List<CreateInvoiceItemRequest> items;
}

