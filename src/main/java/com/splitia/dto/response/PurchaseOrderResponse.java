package com.splitia.dto.response;

import com.splitia.model.enums.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {
    private UUID id;
    private String orderNumber;
    private UUID vendorId;
    private String vendorName;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private PurchaseOrderStatus status;
    private BigDecimal total;
    private UUID createdById;
    private String createdByName;
    private String notes;
    private List<PurchaseOrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

