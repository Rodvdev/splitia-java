package com.splitia.repository;

import com.splitia.model.hr.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID>, JpaSpecificationExecutor<Employee> {
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT e FROM Employee e WHERE e.deletedAt IS NULL")
    Page<Employee> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT e FROM Employee e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Employee> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT e FROM Employee e WHERE e.user.id = :userId AND e.deletedAt IS NULL")
    Optional<Employee> findByUserId(@Param("userId") UUID userId);
    
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT e FROM Employee e WHERE e.employeeNumber = :employeeNumber AND e.deletedAt IS NULL")
    Optional<Employee> findByEmployeeNumber(@Param("employeeNumber") String employeeNumber);
}

