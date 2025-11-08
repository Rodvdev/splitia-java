package com.splitia.model.sales;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.contact.Contact;
import com.splitia.model.enums.ActivityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Data
@EqualsAndHashCode(callSuper = true)
public class Activity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ActivityType type;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @Column(name = "is_completed", nullable = false)
    @NotNull
    private Boolean isCompleted = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
}

