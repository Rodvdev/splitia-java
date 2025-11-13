package com.splitia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String image;
    private String currency;
    private String language;
    private String theme;
    private Boolean notificationsEnabled;
    private String dateFormat;
    private String timeFormat;
    private String role;
    private LocalDateTime createdAt;
}

