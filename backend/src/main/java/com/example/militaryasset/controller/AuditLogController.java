package com.example.militaryasset.controller;

import com.example.militaryasset.entity.AuditLog;
import com.example.militaryasset.repository.AuditLogRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuditLogController {
    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLog> getAuditLogs(@RequestParam(required = false) LocalDate from,
                                       @RequestParam(required = false) LocalDate to,
                                       @RequestParam(required = false) Long userId,
                                       @RequestParam(required = false) String action,
                                       @RequestParam(required = false) String entity) {
        LocalDateTime fromTime = from == null ? null : from.atStartOfDay();
        LocalDateTime toTime = to == null ? null : to.plusDays(1).atStartOfDay();
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .filter(log -> fromTime == null || !log.getTimestamp().isBefore(fromTime))
                .filter(log -> toTime == null || log.getTimestamp().isBefore(toTime))
                .filter(log -> userId == null || log.getUser().getId().equals(userId))
                .filter(log -> action == null || action.isBlank() || log.getAction().equalsIgnoreCase(action))
                .filter(log -> entity == null || entity.isBlank() || log.getEntityType().equalsIgnoreCase(entity))
                .toList();
    }
}
