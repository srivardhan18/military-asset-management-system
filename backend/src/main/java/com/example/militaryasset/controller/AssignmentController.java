package com.example.militaryasset.controller;

import com.example.militaryasset.entity.Assignment;
import com.example.militaryasset.entity.User;
import com.example.militaryasset.repository.AssetRepository;
import com.example.militaryasset.repository.BaseRepository;
import com.example.militaryasset.repository.AssignmentRepository;
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
public class AssignmentController {
    private final AssignmentRepository assignmentRepository;
    private final AssetRepository assetRepository;
    private final BaseRepository baseRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final BaseScopeService baseScopeService;

    public AssignmentController(AssignmentRepository assignmentRepository,
                                AssetRepository assetRepository,
                                BaseRepository baseRepository,
                                UserRepository userRepository,
                                AuditService auditService,
                                BaseScopeService baseScopeService) {
        this.assignmentRepository = assignmentRepository;
        this.assetRepository = assetRepository;
        this.baseRepository = baseRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.baseScopeService = baseScopeService;
    }

    @GetMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public List<Assignment> getAssignments(Authentication authentication) {
        Long baseId = baseScopeService.effectiveBaseId(authentication, null);
        return assignmentRepository.findAll().stream().filter(assignment -> baseId == null || assignment.getBase().getId().equals(baseId)).toList();
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody Assignment assignment, Authentication authentication) {
        assignment.setAsset(assetRepository.findById(assignment.getAsset().getId()).orElseThrow(() -> new RuntimeException("Asset not found")));
        assignment.setBase(baseRepository.findById(assignment.getBase().getId()).orElseThrow(() -> new RuntimeException("Base not found")));
        baseScopeService.requireBase(authentication, assignment.getBase().getId());
        User creator = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        assignment.setCreatedBy(creator);
        Assignment saved = assignmentRepository.save(assignment);
        auditService.record(creator, "CREATE_ASSIGNMENT", "ASSIGNMENT", saved.getId(), saved.getPersonnelName());
        return ResponseEntity.ok(saved);
    }
}
