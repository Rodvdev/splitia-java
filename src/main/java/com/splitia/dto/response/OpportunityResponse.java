package com.splitia.dto.response;

import com.splitia.model.enums.OpportunityStage;
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
public class OpportunityResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal estimatedValue;
    private Integer probability;
    private OpportunityStage stage;
    private LocalDate expectedCloseDate;
    private LocalDate actualCloseDate;
    private BigDecimal wonAmount;
    private String lostReason;
    private String currency;
    private UUID assignedToId;
    private String assignedToName;
    private UUID contactId;
    private String contactName;
    private UUID companyId;
    private String companyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

