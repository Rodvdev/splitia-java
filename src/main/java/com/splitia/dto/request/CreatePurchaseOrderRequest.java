package com.splitia.dto.request;

import com.splitia.model.enums.PurchaseOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePurchaseOrderRequest {
    @Size(max = 50, message = "Order number must be less than 50 characters")
    private String orderNumber; // Auto-generated if not provided
    
    @NotNull(message = "Vendor ID is required")
    private UUID vendorId;
    
    @NotNull(message = "Order date is required")
    private LocalDate orderDate;
    
    private LocalDate expectedDate;
    
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;
    
    @Size(max = 10000, message = "Notes must be less than 10000 characters")
    private String notes;
    
    @Valid
    @NotNull(message = "Items are required")
    private List<CreatePurchaseOrderItemRequest> items;
}

