package com.splitia.repository;

import com.splitia.model.enums.OpportunityStage;
import com.splitia.model.sales.Opportunity;
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
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID>, JpaSpecificationExecutor<Opportunity> {
    @EntityGraph(attributePaths = {"assignedTo", "contact", "company"})
    @Query("SELECT o FROM Opportunity o WHERE o.deletedAt IS NULL")
    Page<Opportunity> findAllActive(Pageable pageable);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact", "company"})
    @Query("SELECT o FROM Opportunity o WHERE o.id = :id AND o.deletedAt IS NULL")
    Optional<Opportunity> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact", "company"})
    @Query("SELECT o FROM Opportunity o WHERE o.stage = :stage AND o.deletedAt IS NULL")
    Page<Opportunity> findByStage(@Param("stage") OpportunityStage stage, Pageable pageable);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact", "company"})
    @Query("SELECT o FROM Opportunity o WHERE o.assignedTo.id = :assignedToId AND o.deletedAt IS NULL")
    Page<Opportunity> findByAssignedToId(@Param("assignedToId") UUID assignedToId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact", "company"})
    @Query("SELECT o FROM Opportunity o WHERE o.contact.id = :contactId AND o.deletedAt IS NULL")
    List<Opportunity> findByContactId(@Param("contactId") UUID contactId);
    
    @EntityGraph(attributePaths = {"assignedTo", "contact", "company"})
    @Query("SELECT o FROM Opportunity o WHERE o.company.id = :companyId AND o.deletedAt IS NULL")
    List<Opportunity> findByCompanyId(@Param("companyId") UUID companyId);
    
    @Query("SELECT o FROM Opportunity o WHERE o.stage = :stage AND o.deletedAt IS NULL")
    List<Opportunity> findAllByStage(@Param("stage") OpportunityStage stage);
}

