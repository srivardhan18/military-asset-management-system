package com.example.militaryasset.controller;

import com.example.militaryasset.entity.Transfer;
import com.example.militaryasset.entity.Asset;
import com.example.militaryasset.entity.AssetStatus;
import com.example.militaryasset.entity.User;
import com.example.militaryasset.service.AuditService;
import com.example.militaryasset.service.BaseScopeService;
import jakarta.transaction.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.example.militaryasset.repository.AssetRepository;
import com.example.militaryasset.repository.BaseRepository;
import com.example.militaryasset.repository.EquipmentTypeRepository;
import com.example.militaryasset.repository.TransferRepository;
import com.example.militaryasset.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransferController {
    private final TransferRepository transferRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final AuditService auditService;
    private final BaseScopeService baseScopeService;

    public TransferController(TransferRepository transferRepository,
                              BaseRepository baseRepository,
                              EquipmentTypeRepository equipmentTypeRepository,
                              UserRepository userRepository,
                              AssetRepository assetRepository,
                              AuditService auditService,
                              BaseScopeService baseScopeService) {
        this.transferRepository = transferRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.auditService = auditService;
        this.baseScopeService = baseScopeService;
    }

    @GetMapping("/transfers")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public List<Transfer> getTransfers(Authentication authentication) {
        Long baseId = baseScopeService.effectiveBaseId(authentication, null);
        return transferRepository.findAll().stream().filter(transfer -> baseId == null || transfer.getFromBase().getId().equals(baseId) || transfer.getToBase().getId().equals(baseId)).toList();
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    @Transactional
    public ResponseEntity<Transfer> createTransfer(@Valid @RequestBody Transfer transfer, Authentication authentication) {
        transfer.setFromBase(baseRepository.findById(transfer.getFromBase().getId()).orElseThrow(() -> new RuntimeException("Source base not found")));
        transfer.setToBase(baseRepository.findById(transfer.getToBase().getId()).orElseThrow(() -> new RuntimeException("Destination base not found")));
        transfer.setEquipmentType(equipmentTypeRepository.findById(transfer.getEquipmentType().getId()).orElseThrow(() -> new RuntimeException("Equipment type not found")));
        baseScopeService.requireBase(authentication, transfer.getFromBase().getId());
        if (transfer.getFromBase().getId().equals(transfer.getToBase().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination bases must differ");
        }
        if (transfer.getQuantity() == null || transfer.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer quantity must be positive");
        }
        Asset source = assetRepository.findByBaseAndEquipmentType(transfer.getFromBase(), transfer.getEquipmentType()).stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No source inventory for this equipment type"));
        if (source.getQuantity() < transfer.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source inventory is insufficient");
        }
        User creator = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        transfer.setCreatedBy(creator);
        source.setQuantity(source.getQuantity() - transfer.getQuantity());
        assetRepository.save(source);
        Asset destination = assetRepository.findByBaseAndEquipmentType(transfer.getToBase(), transfer.getEquipmentType()).stream().findFirst().orElseGet(() -> {
            Asset created = new Asset();
            created.setAssetCode("TR-" + transfer.getReferenceNumber());
            created.setBase(transfer.getToBase());
            created.setEquipmentType(transfer.getEquipmentType());
            created.setQuantity(0);
            created.setStatus(AssetStatus.ACTIVE);
            return created;
        });
        destination.setQuantity(destination.getQuantity() + transfer.getQuantity());
        assetRepository.save(destination);
        Transfer saved = transferRepository.save(transfer);
        auditService.record(creator, "CREATE_TRANSFER", "TRANSFER", saved.getId(), transfer.getReferenceNumber());
        return ResponseEntity.ok(saved);
    }

    @Transactional
    @PostMapping("/transfers/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public ResponseEntity<Transfer> cancelTransfer(@PathVariable Long id, Authentication authentication) {
        Transfer transfer = transferRepository.findById(id).orElseThrow(() -> new RuntimeException("Transfer not found"));
        baseScopeService.requireBase(authentication, transfer.getFromBase().getId());
        if (!"COMPLETED".equalsIgnoreCase(transfer.getStatus())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only completed transfers can be cancelled");
        Asset source = assetRepository.findByBaseAndEquipmentType(transfer.getFromBase(), transfer.getEquipmentType()).stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source inventory record not found"));
        Asset destination = assetRepository.findByBaseAndEquipmentType(transfer.getToBase(), transfer.getEquipmentType()).stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination inventory record not found"));
        if (destination.getQuantity() < transfer.getQuantity()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination inventory cannot reverse this transfer");
        destination.setQuantity(destination.getQuantity() - transfer.getQuantity());
        source.setQuantity(source.getQuantity() + transfer.getQuantity());
        assetRepository.save(destination);
        assetRepository.save(source);
        transfer.setStatus("CANCELLED");
        Transfer saved = transferRepository.save(transfer);
        User actor = userRepository.findByEmail(authentication.getName()).orElseThrow();
        auditService.record(actor, "CANCEL_TRANSFER", "TRANSFER", saved.getId(), saved.getReferenceNumber());
        return ResponseEntity.ok(saved);
    }
}
