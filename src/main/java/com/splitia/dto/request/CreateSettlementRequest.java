package com.splitia.dto.request;

import com.splitia.model.enums.SettlementType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSettlementRequest {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotNull(message = "Currency is required")
    private String currency;
    
    private String description;
    
    @NotNull(message = "Date is required")
    private LocalDateTime date;
    
    @NotNull(message = "Type is required")
    private SettlementType type;
    
    @NotNull(message = "Settled with user ID is required")
    private UUID settledWithUserId;
    
    @NotNull(message = "Group ID is required")
    private UUID groupId;
}

