package com.splitia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "support_attachments")
@Data
@EqualsAndHashCode(callSuper = true)
public class SupportAttachment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String fileName;
    
    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 500)
    private String fileUrl;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @NotNull
    private SupportTicket ticket;
}

