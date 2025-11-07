package com.splitia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTagResponse {
    private UUID id;
    private String name;
    private String color;
    private UUID groupId;
    private LocalDateTime createdAt;
}

