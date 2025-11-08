package com.splitia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private String icon;
    private String color;
    private LocalDateTime createdAt;
    private UUID groupId;
    private String groupName;
    private UUID createdById;
    private String createdByName;
}

