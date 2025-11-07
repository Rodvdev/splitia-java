package com.splitia.model;

import com.splitia.model.enums.SupportCategory;
import com.splitia.model.enums.SupportPriority;
import com.splitia.model.enums.SupportStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "support_tickets")
@Data
@EqualsAndHashCode(callSuper = true)
public class SupportTicket extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull
    @Size(min = 1)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private SupportCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private SupportPriority priority = SupportPriority.MEDIUM;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private SupportStatus status = SupportStatus.OPEN;
    
    @Column(columnDefinition = "TEXT")
    private String resolution;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupportMessage> messages = new ArrayList<>();
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupportAttachment> attachments = new ArrayList<>();
}

