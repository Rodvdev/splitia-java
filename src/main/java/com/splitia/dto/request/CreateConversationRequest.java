package com.splitia.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
    
    @NotNull(message = "Participant IDs are required")
    private List<UUID> participantIds;
}

