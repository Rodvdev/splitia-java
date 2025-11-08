package com.splitia.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type; // EVENT_TYPE enum
    private String module; // CRM, FINANCE, SUPPORT, etc.
    private String action; // CREATED, UPDATED, DELETED, STATUS_CHANGED, etc.
    private UUID entityId;
    private String entityType; // Opportunity, Lead, Invoice, Ticket, etc.
    private Map<String, Object> data;
    private UUID userId; // User who triggered the event
    private LocalDateTime timestamp;
    private String message;
    
    public WebSocketMessage(String type, String module, String action, UUID entityId, String entityType) {
        this.type = type;
        this.module = module;
        this.action = action;
        this.entityId = entityId;
        this.entityType = entityType;
        this.timestamp = LocalDateTime.now();
    }
}

