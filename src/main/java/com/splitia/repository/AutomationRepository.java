package com.splitia.repository;

import com.splitia.model.enums.AutomationStatus;
import com.splitia.model.enums.AutomationTrigger;
import com.splitia.model.marketing.Automation;
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
public interface AutomationRepository extends JpaRepository<Automation, UUID> {
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT a FROM Automation a WHERE a.deletedAt IS NULL")
    Page<Automation> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT a FROM Automation a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Automation> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT a FROM Automation a WHERE a.status = :status AND a.deletedAt IS NULL")
    Page<Automation> findByStatus(@Param("status") AutomationStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy"})
    @Query("SELECT a FROM Automation a WHERE a.trigger = :trigger AND a.status = 'ACTIVE' AND a.deletedAt IS NULL")
    List<Automation> findActiveByTrigger(@Param("trigger") AutomationTrigger trigger);
}

