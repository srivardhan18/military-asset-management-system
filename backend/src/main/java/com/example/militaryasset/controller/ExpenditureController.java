package com.example.militaryasset.controller;

import com.example.militaryasset.entity.Expenditure;
import com.example.militaryasset.entity.User;
import com.example.militaryasset.repository.BaseRepository;
import com.example.militaryasset.repository.EquipmentTypeRepository;
import com.example.militaryasset.repository.ExpenditureRepository;
import com.example.militaryasset.repository.UserRepository;
import com.example.militaryasset.service.AuditService;
import com.example.militaryasset.service.BaseScopeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenditureController {
    private final ExpenditureRepository expenditureRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final BaseScopeService baseScopeService;

    public ExpenditureController(ExpenditureRepository expenditureRepository,
                                 BaseRepository baseRepository,
                                 EquipmentTypeRepository equipmentTypeRepository,
                                 UserRepository userRepository,
                                 AuditService auditService,
                                 BaseScopeService baseScopeService) {
        this.expenditureRepository = expenditureRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.baseScopeService = baseScopeService;
    }

    @GetMapping("/expenditures")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public List<Expenditure> getExpenditures(Authentication authentication) {
        Long baseId = baseScopeService.effectiveBaseId(authentication, null);
        return expenditureRepository.findAll().stream().filter(expenditure -> baseId == null || expenditure.getBase().getId().equals(baseId)).toList();
    }

    @PostMapping("/expenditures")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public ResponseEntity<Expenditure> createExpenditure(@Valid @RequestBody Expenditure expenditure, Authentication authentication) {
        expenditure.setBase(baseRepository.findById(expenditure.getBase().getId()).orElseThrow(() -> new RuntimeException("Base not found")));
        baseScopeService.requireBase(authentication, expenditure.getBase().getId());
        expenditure.setEquipmentType(equipmentTypeRepository.findById(expenditure.getEquipmentType().getId()).orElseThrow(() -> new RuntimeException("Equipment type not found")));
        User creator = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        expenditure.setCreatedBy(creator);
        Expenditure saved = expenditureRepository.save(expenditure);
        auditService.record(creator, "CREATE_EXPENDITURE", "EXPENDITURE", saved.getId(), saved.getReason());
        return ResponseEntity.ok(saved);
    }
}
