package com.splitia.dto.response;

import com.splitia.model.enums.ShareType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FutureExpenseShareResponse {
    private UUID userId;
    private String userName;
    private BigDecimal amount;
    private ShareType type;
}

