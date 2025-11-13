package com.splitia.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferencesRequest {
    private String currency;
    private String language;
    private String theme;
    private Boolean notificationsEnabled;
    private String dateFormat;
    private String timeFormat;
}

