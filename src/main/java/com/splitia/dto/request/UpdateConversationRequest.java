package com.splitia.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConversationRequest {
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
}

