package com.splitia.dto.request;

import com.splitia.model.enums.OpportunityStage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOpportunityRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;
    
    @Size(max = 10000, message = "Description must be less than 10000 characters")
    private String description;
    
    private BigDecimal estimatedValue;
    
    @Min(value = 0, message = "Probability must be between 0 and 100")
    @Max(value = 100, message = "Probability must be between 0 and 100")
    private Integer probability = 0;
    
    private OpportunityStage stage = OpportunityStage.LEAD;
    
    private LocalDate expectedCloseDate;
    
    private UUID assignedToId;
    
    private UUID contactId;
    
    private UUID companyId;
    
    @Size(max = 10)
    private String currency = "USD";
}

