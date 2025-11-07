package com.splitia.repository;

import com.splitia.model.SupportTicket;
import com.splitia.model.enums.SupportCategory;
import com.splitia.model.enums.SupportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {
    Page<SupportTicket> findByCreatedById(UUID createdById, Pageable pageable);
    Page<SupportTicket> findByAssignedToId(UUID assignedToId, Pageable pageable);
    Page<SupportTicket> findByStatus(SupportStatus status, Pageable pageable);
    Page<SupportTicket> findByCategory(SupportCategory category, Pageable pageable);
}

