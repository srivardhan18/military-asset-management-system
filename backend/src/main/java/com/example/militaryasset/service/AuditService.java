package com.example.militaryasset.service;

import com.example.militaryasset.entity.AuditLog;
import com.example.militaryasset.entity.User;
import com.example.militaryasset.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog record(User user, String action, String entityType, Long entityId, String metadata) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setMetadata(metadata);
        return auditLogRepository.save(log);
    }
}