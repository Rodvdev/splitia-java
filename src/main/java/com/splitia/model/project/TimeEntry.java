package com.splitia.model.project;

import com.splitia.model.BaseEntity;
import com.splitia.model.Task;
import com.splitia.model.User;
import com.splitia.model.enums.TimeEntryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "time_entries")
@Data
@EqualsAndHashCode(callSuper = true)
public class TimeEntry extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;
    
    @Column(nullable = false)
    @NotNull
    private LocalDate date;
    
    @Column(nullable = false)
    @NotNull
    private Double hours;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_billable", nullable = false)
    @NotNull
    private Boolean isBillable = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TimeEntryStatus status = TimeEntryStatus.DRAFT;
}

