package com.example.militaryasset.controller;

import com.example.militaryasset.entity.Purchase;
import com.example.militaryasset.entity.User;
import com.example.militaryasset.repository.BaseRepository;
import com.example.militaryasset.repository.EquipmentTypeRepository;
import com.example.militaryasset.repository.PurchaseRepository;
import com.example.militaryasset.repository.UserRepository;
import com.example.militaryasset.service.AuditService;
import com.example.militaryasset.service.BaseScopeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PurchaseController {
    private final PurchaseRepository purchaseRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final BaseScopeService baseScopeService;

    public PurchaseController(PurchaseRepository purchaseRepository,
                              BaseRepository baseRepository,
                              EquipmentTypeRepository equipmentTypeRepository,
                              UserRepository userRepository,
                              AuditService auditService,
                              BaseScopeService baseScopeService) {
        this.purchaseRepository = purchaseRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.baseScopeService = baseScopeService;
    }

    @GetMapping("/purchases")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public List<Purchase> getPurchases(Authentication authentication) {
        Long baseId = baseScopeService.effectiveBaseId(authentication, null);
        return purchaseRepository.findAll().stream().filter(purchase -> baseId == null || purchase.getBase().getId().equals(baseId)).toList();
    }

    @PostMapping("/purchases")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public ResponseEntity<Purchase> createPurchase(@Valid @RequestBody Purchase purchase, Authentication authentication) {
        purchase.setBase(baseRepository.findById(purchase.getBase().getId()).orElseThrow(() -> new RuntimeException("Base not found")));
        baseScopeService.requireBase(authentication, purchase.getBase().getId());
        purchase.setEquipmentType(equipmentTypeRepository.findById(purchase.getEquipmentType().getId()).orElseThrow(() -> new RuntimeException("Equipment type not found")));
        User creator = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        purchase.setCreatedBy(creator);
        Purchase saved = purchaseRepository.save(purchase);
        auditService.record(creator, "CREATE_PURCHASE", "PURCHASE", saved.getId(), saved.getReferenceNumber());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/purchases/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public ResponseEntity<Purchase> updatePurchase(@PathVariable Long id, @Valid @RequestBody Purchase updated, Authentication authentication) {
        Purchase existing = purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase not found"));
        baseScopeService.requireBase(authentication, existing.getBase().getId());
        existing.setBase(baseRepository.findById(updated.getBase().getId()).orElseThrow(() -> new RuntimeException("Base not found")));
        baseScopeService.requireBase(authentication, existing.getBase().getId());
        existing.setEquipmentType(equipmentTypeRepository.findById(updated.getEquipmentType().getId()).orElseThrow(() -> new RuntimeException("Equipment type not found")));
        existing.setQuantity(updated.getQuantity());
        existing.setUnitPrice(updated.getUnitPrice());
        existing.setPurchaseDate(updated.getPurchaseDate());
        existing.setReferenceNumber(updated.getReferenceNumber());
        existing.setNotes(updated.getNotes());
        Purchase saved = purchaseRepository.save(existing);
        User actor = userRepository.findByEmail(authentication.getName()).orElseThrow();
        auditService.record(actor, "UPDATE_PURCHASE", "PURCHASE", saved.getId(), saved.getReferenceNumber());
        return ResponseEntity.ok(saved);
    }
}
