package com.splitia.model.marketing;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.AutomationStatus;
import com.splitia.model.enums.AutomationTrigger;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "automations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Automation extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AutomationTrigger trigger;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AutomationStatus status = AutomationStatus.INACTIVE;
    
    @Column(name = "conditions", columnDefinition = "JSONB")
    private String conditions; // JSON string for trigger conditions
    
    @Column(name = "actions", columnDefinition = "JSONB")
    private String actions; // JSON string for actions to execute
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
}

