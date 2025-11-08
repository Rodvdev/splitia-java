package com.splitia.model.common;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.ApiKeyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiKey extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Column(nullable = false, unique = true)
    @NotNull
    @Size(max = 500)
    private String keyHash; // Hashed API key
    
    @Column(name = "permissions", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> permissions = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ApiKeyStatus status = ApiKeyStatus.ACTIVE;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
}

