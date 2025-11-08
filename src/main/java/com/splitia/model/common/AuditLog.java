package com.splitia.model.common;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.AuditAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "entity_type", nullable = false)
    @NotNull
    @Size(max = 100)
    private String entityType;
    
    @Column(name = "entity_id", nullable = false)
    @NotNull
    private UUID entityId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AuditAction action;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "changes", columnDefinition = "JSONB")
    private String changes; // JSON string with old and new values
    
    @Column(name = "timestamp", nullable = false)
    @NotNull
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Column(name = "ip_address")
    @Size(max = 50)
    private String ipAddress;
    
    @Column(name = "user_agent")
    @Size(max = 500)
    private String userAgent;
}

