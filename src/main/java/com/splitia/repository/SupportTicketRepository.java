package com.splitia.repository;

import com.splitia.model.SupportTicket;
import com.splitia.model.enums.SupportCategory;
import com.splitia.model.enums.SupportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {
    @Query("SELECT t FROM SupportTicket t WHERE t.createdBy.id = :createdById AND t.deletedAt IS NULL")
    Page<SupportTicket> findByCreatedById(@Param("createdById") UUID createdById, Pageable pageable);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.assignedTo.id = :assignedToId AND t.deletedAt IS NULL")
    Page<SupportTicket> findByAssignedToId(@Param("assignedToId") UUID assignedToId, Pageable pageable);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.status = :status AND t.deletedAt IS NULL")
    Page<SupportTicket> findByStatus(@Param("status") SupportStatus status, Pageable pageable);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.category = :category AND t.deletedAt IS NULL")
    Page<SupportTicket> findByCategory(@Param("category") SupportCategory category, Pageable pageable);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<SupportTicket> findByIdAndDeletedAtIsNull(@Param("id") UUID id);
}

