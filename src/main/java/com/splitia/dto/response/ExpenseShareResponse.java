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
public class ExpenseShareResponse {
    private UUID id;
    private BigDecimal amount;
    private ShareType type;
    private UserResponse user;
}

