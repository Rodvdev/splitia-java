package com.splitia.repository;

import com.splitia.model.common.AuditLog;
import com.splitia.model.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    @Query("SELECT al FROM AuditLog al WHERE al.deletedAt IS NULL ORDER BY al.timestamp DESC")
    Page<AuditLog> findAllActive(Pageable pageable);
    
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.entityId = :entityId AND al.deletedAt IS NULL ORDER BY al.timestamp DESC")
    Page<AuditLog> findByEntity(@Param("entityType") String entityType, @Param("entityId") UUID entityId, Pageable pageable);
    
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId AND al.deletedAt IS NULL ORDER BY al.timestamp DESC")
    Page<AuditLog> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT al FROM AuditLog al WHERE al.action = :action AND al.deletedAt IS NULL ORDER BY al.timestamp DESC")
    Page<AuditLog> findByAction(@Param("action") AuditAction action, Pageable pageable);
    
    @Query("SELECT al FROM AuditLog al WHERE al.timestamp BETWEEN :startDate AND :endDate AND al.deletedAt IS NULL ORDER BY al.timestamp DESC")
    Page<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}

