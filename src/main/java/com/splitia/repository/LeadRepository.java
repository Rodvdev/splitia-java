package com.splitia.repository;

import com.splitia.model.enums.LeadSource;
import com.splitia.model.enums.LeadStatus;
import com.splitia.model.sales.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID>, JpaSpecificationExecutor<Lead> {
    @EntityGraph(attributePaths = {"assignedTo", "contact"})
    @Query("SELECT l FROM Lead l WHERE l.deletedAt IS NULL")
    Page<Lead> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact"})
    @Query("SELECT l FROM Lead l WHERE l.id = :id AND l.deletedAt IS NULL")
    Optional<Lead> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact"})
    @Query("SELECT l FROM Lead l WHERE l.status = :status AND l.deletedAt IS NULL")
    Page<Lead> findByStatus(@Param("status") LeadStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact"})
    @Query("SELECT l FROM Lead l WHERE l.source = :source AND l.deletedAt IS NULL")
    Page<Lead> findBySource(@Param("source") LeadSource source, Pageable pageable);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact"})
    @Query("SELECT l FROM Lead l WHERE l.assignedTo.id = :assignedToId AND l.deletedAt IS NULL")
    Page<Lead> findByAssignedToId(@Param("assignedToId") UUID assignedToId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact"})
    @Query("SELECT l FROM Lead l WHERE l.email = :email AND l.deletedAt IS NULL")
    Optional<Lead> findByEmail(@Param("email") String email);
    
    @Query("SELECT l FROM Lead l WHERE l.contact.id = :contactId AND l.deletedAt IS NULL")
    List<Lead> findByContactId(@Param("contactId") UUID contactId);
}

