package com.splitia.model.workflow;

import com.splitia.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "workflow_steps")
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkflowStep extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    @NotNull
    private Workflow workflow;
    
    @Column(nullable = false)
    @NotNull
    private Integer stepOrder;
    
    @Column(name = "action_type", nullable = false)
    @NotNull
    @Size(max = 100)
    private String actionType;
    
    @Column(name = "action_config", columnDefinition = "JSONB")
    private String actionConfig; // JSON string
    
    @Column(name = "conditions", columnDefinition = "JSONB")
    private String conditions; // JSON string for step conditions
}

