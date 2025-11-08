package com.splitia.dto.request;

import com.splitia.model.enums.StockMovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockMovementRequest {
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    @NotNull(message = "Type is required")
    private StockMovementType type;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    
    @Size(max = 10000, message = "Reason must be less than 10000 characters")
    private String reason;
    
    @Size(max = 255, message = "Reference must be less than 255 characters")
    private String reference;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
}

