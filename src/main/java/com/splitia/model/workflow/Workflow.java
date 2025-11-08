package com.splitia.model.workflow;

import com.splitia.model.BaseEntity;
import com.splitia.model.enums.WorkflowStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workflows")
@Data
@EqualsAndHashCode(callSuper = true)
public class Workflow extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "trigger_event", nullable = false)
    @NotNull
    @Size(max = 100)
    private String triggerEvent;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private WorkflowStatus status = WorkflowStatus.INACTIVE;
    
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowStep> steps = new ArrayList<>();
}

