package com.splitia.repository;

import com.splitia.model.enums.AttendanceStatus;
import com.splitia.model.hr.Attendance;
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
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    @EntityGraph(attributePaths = {"employee", "employee.user"})
    @Query("SELECT a FROM Attendance a WHERE a.deletedAt IS NULL")
    Page<Attendance> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"employee", "employee.user"})
    @Query("SELECT a FROM Attendance a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Attendance> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"employee", "employee.user"})
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.deletedAt IS NULL")
    Page<Attendance> findByEmployeeId(@Param("employeeId") UUID employeeId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"employee", "employee.user"})
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.date = :date AND a.deletedAt IS NULL")
    Optional<Attendance> findByEmployeeIdAndDate(@Param("employeeId") UUID employeeId, @Param("date") LocalDate date);
}

