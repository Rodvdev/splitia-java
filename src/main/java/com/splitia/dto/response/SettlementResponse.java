package com.splitia.dto.response;

import com.splitia.model.enums.SettlementStatus;
import com.splitia.model.enums.SettlementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {
    private UUID id;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDateTime date;
    private SettlementStatus status;
    private SettlementType type;
    private UserResponse initiatedBy;
    private UserResponse settledWithUser;
    private LocalDateTime createdAt;
}

