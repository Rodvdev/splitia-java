package com.splitia.repository;

import com.splitia.model.workflow.Workflow;
import com.splitia.model.enums.WorkflowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    @EntityGraph(attributePaths = {"steps"})
    @Query("SELECT w FROM Workflow w WHERE w.deletedAt IS NULL")
    Page<Workflow> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"steps"})
    @Query("SELECT w FROM Workflow w WHERE w.id = :id AND w.deletedAt IS NULL")
    Optional<Workflow> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"steps"})
    @Query("SELECT w FROM Workflow w WHERE w.status = :status AND w.deletedAt IS NULL")
    Page<Workflow> findByStatus(@Param("status") WorkflowStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"steps"})
    @Query("SELECT w FROM Workflow w WHERE w.triggerEvent = :triggerEvent AND w.status = 'ACTIVE' AND w.deletedAt IS NULL")
    List<Workflow> findActiveByTrigger(@Param("triggerEvent") String triggerEvent);
}

