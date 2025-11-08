package com.splitia.service;

import com.splitia.model.common.AuditLog;
import com.splitia.model.enums.AuditAction;
import com.splitia.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    
    @Transactional
    public void logAction(String entityType, UUID entityId, AuditAction action, UUID userId, String changes, String ipAddress, String userAgent) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setAction(action);
        auditLog.setChanges(changes);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);
        auditLog.setTimestamp(LocalDateTime.now());
        
        if (userId != null) {
            com.splitia.model.User user = new com.splitia.model.User();
            user.setId(userId);
            auditLog.setUser(user);
        }
        
        auditLogRepository.save(auditLog);
    }
    
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(Pageable pageable, String entityType, UUID entityId, UUID userId, AuditAction action) {
        if (entityType != null && entityId != null) {
            return auditLogRepository.findByEntity(entityType, entityId, pageable);
        }
        if (userId != null) {
            return auditLogRepository.findByUserId(userId, pageable);
        }
        if (action != null) {
            return auditLogRepository.findByAction(action, pageable);
        }
        return auditLogRepository.findAllActive(pageable);
    }
}

