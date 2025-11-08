package com.splitia.repository;

import com.splitia.model.enums.ProjectStatus;
import com.splitia.model.project.Project;
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
public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {
    @EntityGraph(attributePaths = {"manager", "tasks"})
    @Query("SELECT p FROM Project p WHERE p.deletedAt IS NULL")
    Page<Project> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"manager", "tasks"})
    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Project> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"manager", "tasks"})
    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.deletedAt IS NULL")
    Page<Project> findByStatus(@Param("status") ProjectStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"manager", "tasks"})
    @Query("SELECT p FROM Project p WHERE p.manager.id = :managerId AND p.deletedAt IS NULL")
    Page<Project> findByManagerId(@Param("managerId") UUID managerId, Pageable pageable);
}

