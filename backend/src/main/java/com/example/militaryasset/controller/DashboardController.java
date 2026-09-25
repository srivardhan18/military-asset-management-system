package com.example.militaryasset.controller;

import com.example.militaryasset.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import com.example.militaryasset.service.BaseScopeService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {
    private final DashboardService dashboardService;
    private final BaseScopeService baseScopeService;

    public DashboardController(DashboardService dashboardService, BaseScopeService baseScopeService) {
        this.dashboardService = dashboardService;
        this.baseScopeService = baseScopeService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getSummary(from, to, baseScopeService.effectiveBaseId(authentication, baseId), equipmentTypeId));
    }

    @GetMapping("/dashboard/trends")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public ResponseEntity<Map<String, Object>> getTrends(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getTrendsForPeriod(from, to, baseScopeService.effectiveBaseId(authentication, baseId), equipmentTypeId));
    }

    @GetMapping("/dashboard/movement")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public ResponseEntity<Map<String, Object>> getMovement(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getMovement(from, to, baseScopeService.effectiveBaseId(authentication, baseId), equipmentTypeId));
    }
}
