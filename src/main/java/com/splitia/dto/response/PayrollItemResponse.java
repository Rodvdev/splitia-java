package com.splitia.dto.response;

import com.splitia.model.enums.PayrollItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollItemResponse {
    private UUID id;
    private PayrollItemType type;
    private String description;
    private BigDecimal amount;
}

