package com.example.militaryasset.controller;

import com.example.militaryasset.entity.Base;
import com.example.militaryasset.repository.BaseRepository;
import com.example.militaryasset.repository.AssetRepository;
import com.example.militaryasset.repository.PurchaseRepository;
import com.example.militaryasset.repository.TransferRepository;
import com.example.militaryasset.repository.AssignmentRepository;
import com.example.militaryasset.repository.ExpenditureRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.example.militaryasset.service.BaseScopeService;
import com.example.militaryasset.service.AuditService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BaseController {
    private final BaseRepository baseRepository;
    private final AssetRepository assetRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final BaseScopeService baseScopeService;
    private final AuditService auditService;

    public BaseController(BaseRepository baseRepository, AssetRepository assetRepository, PurchaseRepository purchaseRepository, TransferRepository transferRepository, AssignmentRepository assignmentRepository, ExpenditureRepository expenditureRepository, BaseScopeService baseScopeService, AuditService auditService) {
        this.baseRepository = baseRepository;
        this.assetRepository = assetRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.baseScopeService = baseScopeService;
        this.auditService = auditService;
    }

    @GetMapping("/bases")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public List<Base> getBases(Authentication authentication) {
        Long baseId = baseScopeService.effectiveBaseId(authentication, null);
        return baseRepository.findAll().stream().filter(base -> baseId == null || base.getId().equals(baseId)).toList();
    }

    @PostMapping("/bases")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public ResponseEntity<Base> createBase(@Valid @RequestBody Base base, Authentication authentication) {
        Base saved = baseRepository.save(base);
        auditService.record(baseScopeService.user(authentication), "CREATE_BASE", "BASE", saved.getId(), saved.getCode());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/bases/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public ResponseEntity<Base> updateBase(@PathVariable Long id, @Valid @RequestBody Base updated, Authentication authentication) {
        Base existing = baseRepository.findById(id).orElseThrow(() -> new RuntimeException("Base not found"));
        existing.setName(updated.getName());
        existing.setCode(updated.getCode());
        existing.setLocation(updated.getLocation());
        existing.setActive(updated.isActive());
        Base saved = baseRepository.save(existing);
        auditService.record(baseScopeService.user(authentication), "UPDATE_BASE", "BASE", saved.getId(), saved.getCode());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/bases/{id}/summary")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public ResponseEntity<java.util.Map<String, Object>> getSummary(@PathVariable Long id, Authentication authentication) {
        baseScopeService.requireBase(authentication, id);
        Base base = baseRepository.findById(id).orElseThrow(() -> new RuntimeException("Base not found"));
        java.util.Map<String, Object> summary = new java.util.LinkedHashMap<>();
        summary.put("totalAssets", assetRepository.findByBase(base).stream().mapToInt(a -> a.getQuantity()).sum());
        summary.put("purchases", purchaseRepository.findAll().stream().filter(p -> p.getBase().getId().equals(id)).mapToInt(p -> p.getQuantity()).sum());
        summary.put("transferIn", transferRepository.findByToBase(base).stream().mapToInt(t -> t.getQuantity()).sum());
        summary.put("transferOut", transferRepository.findByFromBase(base).stream().mapToInt(t -> t.getQuantity()).sum());
        summary.put("assignments", assignmentRepository.findAll().stream().filter(a -> a.getBase().getId().equals(id)).mapToInt(a -> a.getQuantity()).sum());
        summary.put("expenditures", expenditureRepository.findAll().stream().filter(e -> e.getBase().getId().equals(id)).mapToInt(e -> e.getQuantity()).sum());
        return ResponseEntity.ok(summary);
    }
}
