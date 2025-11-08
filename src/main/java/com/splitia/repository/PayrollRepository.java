package com.splitia.repository;

import com.splitia.model.enums.PayrollStatus;
import com.splitia.model.hr.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.user", "items"})
    @Query("SELECT p FROM Payroll p WHERE p.deletedAt IS NULL")
    Page<Payroll> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"employee", "employee.user", "items"})
    @Query("SELECT p FROM Payroll p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Payroll> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"employee", "employee.user", "items"})
    @Query("SELECT p FROM Payroll p WHERE p.employee.id = :employeeId AND p.deletedAt IS NULL")
    Page<Payroll> findByEmployeeId(@Param("employeeId") UUID employeeId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"employee", "employee.user", "items"})
    @Query("SELECT p FROM Payroll p WHERE p.status = :status AND p.deletedAt IS NULL")
    Page<Payroll> findByStatus(@Param("status") PayrollStatus status, Pageable pageable);
}

