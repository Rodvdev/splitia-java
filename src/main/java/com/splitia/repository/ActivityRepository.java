package com.splitia.repository;

import com.splitia.model.enums.ActivityType;
import com.splitia.model.sales.Activity;
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
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    @EntityGraph(attributePaths = {"opportunity", "lead", "contact", "createdBy", "assignedTo"})
    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL")
    Page<Activity> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"opportunity", "lead", "contact", "createdBy", "assignedTo"})
    @Query("SELECT a FROM Activity a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Activity> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"opportunity", "lead", "contact", "createdBy", "assignedTo"})
    @Query("SELECT a FROM Activity a WHERE a.opportunity.id = :opportunityId AND a.deletedAt IS NULL ORDER BY a.createdAt DESC")
    List<Activity> findByOpportunityId(@Param("opportunityId") UUID opportunityId);
    
    @EntityGraph(attributePaths = {"opportunity", "lead", "contact", "createdBy", "assignedTo"})
    @Query("SELECT a FROM Activity a WHERE a.lead.id = :leadId AND a.deletedAt IS NULL ORDER BY a.createdAt DESC")
    List<Activity> findByLeadId(@Param("leadId") UUID leadId);
    
    @EntityGraph(attributePaths = {"opportunity", "lead", "contact", "createdBy", "assignedTo"})
    @Query("SELECT a FROM Activity a WHERE a.contact.id = :contactId AND a.deletedAt IS NULL ORDER BY a.createdAt DESC")
    List<Activity> findByContactId(@Param("contactId") UUID contactId);
    
    @EntityGraph(attributePaths = {"opportunity", "lead", "contact", "createdBy", "assignedTo"})
    @Query("SELECT a FROM Activity a WHERE a.type = :type AND a.deletedAt IS NULL")
    Page<Activity> findByType(@Param("type") ActivityType type, Pageable pageable);
    
    @EntityGraph(attributePaths = {"opportunity", "lead", "contact", "createdBy", "assignedTo"})
    @Query("SELECT a FROM Activity a WHERE a.isCompleted = :isCompleted AND a.deletedAt IS NULL")
    Page<Activity> findByIsCompleted(@Param("isCompleted") Boolean isCompleted, Pageable pageable);
}

