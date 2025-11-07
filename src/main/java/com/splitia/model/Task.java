package com.splitia.model;

import com.splitia.model.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TaskStatus status = TaskStatus.TODO;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "priority", nullable = false)
    @NotNull
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @NotNull
    private Group group;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;
    
    @ManyToMany
    @JoinTable(
        name = "task_task_tags",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TaskTag> tags = new ArrayList<>();
    
    @Column(name = "position", nullable = false)
    @NotNull
    private Integer position = 0; // For ordering within status column
    
    // Expense relationship (optional - for referencing existing expense)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    private Expense expense;
    
    // Future expense fields (for storing future expense info without creating Expense)
    @Column(name = "future_expense_amount", precision = 19, scale = 2)
    private BigDecimal futureExpenseAmount;
    
    @Column(name = "future_expense_currency", length = 10)
    private String futureExpenseCurrency = "USD";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "future_expense_paid_by_id")
    private User futureExpensePaidBy;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "future_expense_shares", columnDefinition = "jsonb")
    private List<Map<String, Object>> futureExpenseShares;
}

