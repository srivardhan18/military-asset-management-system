package com.example.militaryasset.controller;

import com.example.militaryasset.entity.Asset;
import com.example.militaryasset.entity.Base;
import com.example.militaryasset.entity.EquipmentType;
import com.example.militaryasset.repository.AssetRepository;
import com.example.militaryasset.repository.BaseRepository;
import com.example.militaryasset.repository.EquipmentTypeRepository;
import com.example.militaryasset.repository.UserRepository;
import com.example.militaryasset.service.AuditService;
import com.example.militaryasset.service.BaseScopeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AssetController {
    private final AssetRepository assetRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final BaseScopeService baseScopeService;

    public AssetController(AssetRepository assetRepository,
                          BaseRepository baseRepository,
                          EquipmentTypeRepository equipmentTypeRepository,
                          UserRepository userRepository,
                          AuditService auditService,
                          BaseScopeService baseScopeService) {
        this.assetRepository = assetRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.baseScopeService = baseScopeService;
    }

    @GetMapping("/assets")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public List<Asset> getAssets(@RequestParam(required = false) Long baseId, Authentication authentication) {
        Long scopedBaseId = baseScopeService.effectiveBaseId(authentication, baseId);
        return assetRepository.findAll().stream().filter(asset -> scopedBaseId == null || asset.getBase().getId().equals(scopedBaseId)).toList();
    }

    @PostMapping("/assets")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public ResponseEntity<Asset> createAsset(@Valid @RequestBody Asset asset, Authentication authentication) {
        Base base = baseRepository.findById(asset.getBase().getId()).orElseThrow(() -> new RuntimeException("Base not found"));
        baseScopeService.requireBase(authentication, base.getId());
        EquipmentType type = equipmentTypeRepository.findById(asset.getEquipmentType().getId()).orElseThrow(() -> new RuntimeException("Equipment type not found"));
        asset.setBase(base);
        asset.setEquipmentType(type);
        Asset saved = assetRepository.save(asset);
        auditService.record(userRepository.findByEmail(authentication.getName()).orElseThrow(), "CREATE_ASSET", "ASSET", saved.getId(), saved.getAssetCode());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/assets/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public ResponseEntity<Asset> updateAsset(@PathVariable Long id, @Valid @RequestBody Asset updated, Authentication authentication) {
        Asset existing = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
        baseScopeService.requireBase(authentication, existing.getBase().getId());
        existing.setAssetCode(updated.getAssetCode());
        existing.setQuantity(updated.getQuantity());
        existing.setStatus(updated.getStatus());
        Base updatedBase = baseRepository.findById(updated.getBase().getId()).orElseThrow(() -> new RuntimeException("Base not found"));
        baseScopeService.requireBase(authentication, updatedBase.getId());
        existing.setBase(updatedBase);
        existing.setEquipmentType(equipmentTypeRepository.findById(updated.getEquipmentType().getId()).orElseThrow(() -> new RuntimeException("Equipment type not found")));
        Asset saved = assetRepository.save(existing);
        auditService.record(userRepository.findByEmail(authentication.getName()).orElseThrow(), "UPDATE_ASSET", "ASSET", saved.getId(), saved.getAssetCode());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/assets/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public ResponseEntity<Void> deactivateAsset(@PathVariable Long id, Authentication authentication) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
        baseScopeService.requireBase(authentication, asset.getBase().getId());
        asset.setStatus(com.example.militaryasset.entity.AssetStatus.MISSING);
        assetRepository.save(asset);
        auditService.record(userRepository.findByEmail(authentication.getName()).orElseThrow(), "DEACTIVATE_ASSET", "ASSET", id, asset.getAssetCode());
        return ResponseEntity.noContent().build();
    }
}
