package com.splitia.model.hr;

import com.splitia.model.BaseEntity;
import com.splitia.model.User;
import com.splitia.model.enums.EmployeeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Data
@EqualsAndHashCode(callSuper = true)
public class Employee extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull
    private User user;
    
    @Column(name = "employee_number", nullable = false, unique = true)
    @NotNull
    @Size(max = 50)
    private String employeeNumber;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 100)
    private String department;
    
    @Column(nullable = false)
    @NotNull
    @Size(max = 100)
    private String position;
    
    @Column(name = "hire_date", nullable = false)
    @NotNull
    private LocalDate hireDate;
    
    @Column(name = "salary", precision = 19, scale = 2)
    private BigDecimal salary;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
    
    @Column(name = "manager_id")
    private UUID managerId;
}

