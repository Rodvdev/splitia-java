package com.splitia.model.hr;

import com.splitia.model.BaseEntity;
import com.splitia.model.enums.AttendanceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "date"}))
@Data
@EqualsAndHashCode(callSuper = true)
public class Attendance extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull
    private Employee employee;
    
    @Column(nullable = false)
    @NotNull
    private LocalDate date;
    
    @Column(name = "check_in")
    private LocalDateTime checkIn;
    
    @Column(name = "check_out")
    private LocalDateTime checkOut;
    
    @Column(name = "hours_worked")
    private Double hoursWorked;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AttendanceStatus status = AttendanceStatus.PRESENT;
}

