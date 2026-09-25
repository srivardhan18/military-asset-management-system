package com.example.militaryasset.controller;

import com.example.militaryasset.entity.EquipmentType;
import com.example.militaryasset.repository.EquipmentTypeRepository;
import com.example.militaryasset.repository.UserRepository;
import com.example.militaryasset.service.AuditService;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EquipmentTypeController {
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public EquipmentTypeController(EquipmentTypeRepository equipmentTypeRepository, UserRepository userRepository, AuditService auditService) {
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @GetMapping("/equipment-types")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public List<EquipmentType> getEquipmentTypes() {
        return equipmentTypeRepository.findAll();
    }

    @PostMapping("/equipment-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentType> createEquipmentType(@Valid @RequestBody EquipmentType equipmentType, Authentication authentication) {
        EquipmentType saved = equipmentTypeRepository.save(equipmentType);
        auditService.record(userRepository.findByEmail(authentication.getName()).orElseThrow(), "CREATE_EQUIPMENT_TYPE", "EQUIPMENT_TYPE", saved.getId(), saved.getName());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/equipment-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentType> updateEquipmentType(@PathVariable Long id, @Valid @RequestBody EquipmentType updated, Authentication authentication) {
        EquipmentType existing = equipmentTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Equipment type not found"));
        existing.setName(updated.getName());
        existing.setCategory(updated.getCategory());
        existing.setDescription(updated.getDescription());
        EquipmentType saved = equipmentTypeRepository.save(existing);
        auditService.record(userRepository.findByEmail(authentication.getName()).orElseThrow(), "UPDATE_EQUIPMENT_TYPE", "EQUIPMENT_TYPE", saved.getId(), saved.getName());
        return ResponseEntity.ok(saved);
    }
}
