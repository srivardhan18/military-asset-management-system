package com.example.militaryasset.service;

import com.example.militaryasset.entity.*;
import com.example.militaryasset.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SeedDataService {
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public SeedDataService(BaseRepository baseRepository,
                          EquipmentTypeRepository equipmentTypeRepository,
                          UserRepository userRepository,
                          AssetRepository assetRepository,
                          PurchaseRepository purchaseRepository,
                          TransferRepository transferRepository,
                          AssignmentRepository assignmentRepository,
                          ExpenditureRepository expenditureRepository,
                          AuditLogRepository auditLogRepository,
                          PasswordEncoder passwordEncoder) {
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void seed() {
        if (userRepository.count() > 0) return;

        Base base1 = new Base();
        base1.setName("Headquarters Base");
        base1.setCode("HQ-01");
        base1.setLocation("Nairobi");
        base1.setActive(true);
        baseRepository.save(base1);

        Base base2 = new Base();
        base2.setName("Western Command");
        base2.setCode("WC-02");
        base2.setLocation("Kisumu");
        base2.setActive(true);
        baseRepository.save(base2);

        EquipmentType rifle = new EquipmentType();
        rifle.setName("Rifle");
        rifle.setCategory("Weapon");
        rifle.setDescription("Standard assault rifle");
        equipmentTypeRepository.save(rifle);

        EquipmentType radio = new EquipmentType();
        radio.setName("Radio Set");
        radio.setCategory("Communications");
        radio.setDescription("Field radio communications equipment");
        equipmentTypeRepository.save(radio);

        User admin = new User();
        admin.setName("System Admin");
        admin.setEmail("admin@military.local");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setBase(base1);
        admin.setActive(true);
        userRepository.save(admin);

        User commander = new User();
        commander.setName("Base Commander");
        commander.setEmail("commander@military.local");
        commander.setPassword(passwordEncoder.encode("commander123"));
        commander.setRole(Role.BASE_COMMANDER);
        commander.setBase(base2);
        commander.setActive(true);
        userRepository.save(commander);

        User logistics = new User();
        logistics.setName("Logistics Officer");
        logistics.setEmail("logistics@military.local");
        logistics.setPassword(passwordEncoder.encode("logistics123"));
        logistics.setRole(Role.LOGISTICS_OFFICER);
        logistics.setBase(base1);
        logistics.setActive(true);
        userRepository.save(logistics);

        Asset rifleAsset = new Asset();
        rifleAsset.setAssetCode("AS-1001");
        rifleAsset.setEquipmentType(rifle);
        rifleAsset.setBase(base1);
        rifleAsset.setQuantity(120);
        rifleAsset.setStatus(AssetStatus.ACTIVE);
        assetRepository.save(rifleAsset);

        Asset radioAsset = new Asset();
        radioAsset.setAssetCode("AS-2001");
        radioAsset.setEquipmentType(radio);
        radioAsset.setBase(base2);
        radioAsset.setQuantity(80);
        radioAsset.setStatus(AssetStatus.ACTIVE);
        assetRepository.save(radioAsset);

        Purchase purchase = new Purchase();
        purchase.setBase(base1);
        purchase.setEquipmentType(rifle);
        purchase.setQuantity(50);
        purchase.setUnitPrice(new BigDecimal("850.00"));
        purchase.setPurchaseDate(LocalDate.now().minusDays(12));
        purchase.setReferenceNumber("PO-1001");
        purchase.setCreatedBy(admin);
        purchaseRepository.save(purchase);

        Transfer transfer = new Transfer();
        transfer.setFromBase(base1);
        transfer.setToBase(base2);
        transfer.setEquipmentType(rifle);
        transfer.setQuantity(12);
        transfer.setTransferDate(LocalDate.now().minusDays(5));
        transfer.setReferenceNumber("TR-2001");
        transfer.setStatus("COMPLETED");
        transfer.setCreatedBy(admin);
        transferRepository.save(transfer);

        Assignment assignment = new Assignment();
        assignment.setAsset(rifleAsset);
        assignment.setBase(base2);
        assignment.setPersonnelName("Captain A. Otieno");
        assignment.setQuantity(10);
        assignment.setAssignmentDate(LocalDate.now().minusDays(2));
        assignment.setStatus("ACTIVE");
        assignment.setCreatedBy(admin);
        assignmentRepository.save(assignment);

        Expenditure expenditure = new Expenditure();
        expenditure.setEquipmentType(radio);
        expenditure.setBase(base2);
        expenditure.setQuantity(5);
        expenditure.setExpenditureDate(LocalDate.now().minusDays(3));
        expenditure.setReason("Field communications deployment");
        expenditure.setCreatedBy(logistics);
        expenditureRepository.save(expenditure);

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(admin);
        auditLog.setAction("LOGIN");
        auditLog.setEntityType("USER");
        auditLog.setEntityId(admin.getId());
        auditLog.setMetadata("Seed data initialized");
        auditLogRepository.save(auditLog);
    }
}
