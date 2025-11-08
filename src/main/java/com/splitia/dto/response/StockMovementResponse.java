package com.splitia.dto.response;

import com.splitia.model.enums.StockMovementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productSku;
    private StockMovementType type;
    private Integer quantity;
    private String reason;
    private String reference;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

