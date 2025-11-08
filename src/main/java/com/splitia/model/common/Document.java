package com.splitia.model.common;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.DocumentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "documents")
@Data
@EqualsAndHashCode(callSuper = true)
public class Document extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private DocumentType type;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 500)
    private String path;
    
    @Column(name = "entity_type", nullable = false)
    @NotNull
    @Size(max = 100)
    private String entityType;
    
    @Column(name = "entity_id", nullable = false)
    @NotNull
    private UUID entityId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    @NotNull
    private User uploadedBy;
    
    @Column(nullable = false)
    @NotNull
    private Long size; // File size in bytes
    
    @Column(name = "mime_type")
    @Size(max = 100)
    private String mimeType;
}

