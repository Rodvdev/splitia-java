package com.splitia.dto.request;

import com.splitia.model.enums.InvoiceStatus;
import jakarta.validation.Valid;
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
public class UpdateInvoiceRequest {
    private LocalDate issueDate;
    
    private LocalDate dueDate;
    
    private InvoiceStatus status;
    
    private BigDecimal subtotal;
    
    private BigDecimal tax;
    
    private BigDecimal total;
    
    @Size(max = 10)
    private String currency;
    
    @Size(max = 10000, message = "Notes must be less than 10000 characters")
    private String notes;
    
    private UUID contactId;
    
    private UUID companyId;
    
    @Valid
    private List<CreateInvoiceItemRequest> items;
}

