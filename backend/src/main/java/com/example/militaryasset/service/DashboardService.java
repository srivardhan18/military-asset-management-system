package com.example.militaryasset.service;

import com.example.militaryasset.entity.*;
import com.example.militaryasset.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final BaseRepository baseRepository;
    private final AssetRepository assetRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;

    public DashboardService(BaseRepository baseRepository,
                           AssetRepository assetRepository,
                           PurchaseRepository purchaseRepository,
                           TransferRepository transferRepository,
                           AssignmentRepository assignmentRepository,
                           ExpenditureRepository expenditureRepository) {
        this.baseRepository = baseRepository;
        this.assetRepository = assetRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
    }

    public Map<String, Object> getSummary(LocalDate from, LocalDate to, Long baseId, Long equipmentTypeId) {
        LocalDate periodFrom = from == null ? LocalDate.of(LocalDate.now().getYear(), 1, 1) : from;
        LocalDate periodTo = to == null ? LocalDate.now() : to;
        List<Base> bases = baseRepository.findAll();
        List<Asset> assets = filteredAssets(baseId, equipmentTypeId);
        List<Purchase> purchases = filteredPurchases(periodFrom, periodTo, baseId, equipmentTypeId);
        List<Transfer> transfers = filteredTransfers(periodFrom, periodTo, baseId, equipmentTypeId);
        List<Assignment> assignments = assignmentRepository.findAll().stream()
                .filter(a -> within(a.getAssignmentDate(), periodFrom, periodTo))
                .filter(a -> baseId == null || a.getBase().getId().equals(baseId))
                .filter(a -> equipmentTypeId == null || a.getAsset().getEquipmentType().getId().equals(equipmentTypeId)).toList();
        List<Expenditure> expenditures = expenditureRepository.findAll().stream()
                .filter(e -> within(e.getExpenditureDate(), periodFrom, periodTo))
                .filter(e -> baseId == null || e.getBase().getId().equals(baseId))
                .filter(e -> equipmentTypeId == null || e.getEquipmentType().getId().equals(equipmentTypeId)).toList();

        int totalAssets = assets.stream().mapToInt(Asset::getQuantity).sum();
        int purchaseQuantity = purchases.stream().mapToInt(Purchase::getQuantity).sum();
        int transferIn = transfers.stream().filter(t -> baseId == null || t.getToBase().getId().equals(baseId)).mapToInt(Transfer::getQuantity).sum();
        int transferOut = transfers.stream().filter(t -> baseId == null || t.getFromBase().getId().equals(baseId)).mapToInt(Transfer::getQuantity).sum();
        int assignedQuantity = assignments.stream().mapToInt(Assignment::getQuantity).sum();
        int expendedQuantity = expenditures.stream().mapToInt(Expenditure::getQuantity).sum();
        int netMovement = purchaseQuantity + transferIn - transferOut;
        int closingBalance = totalAssets;
        int openingBalance = closingBalance - netMovement + assignedQuantity + expendedQuantity;
        BigDecimal totalPurchaseValue = purchases.stream()
            .map(p -> p.getUnitPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalBases", bases.size());
        summary.put("activeAssets", assets.size());
        summary.put("totalAssetQuantity", totalAssets);
        summary.put("totalPurchaseValue", totalPurchaseValue);
        summary.put("openingBalance", openingBalance);
        summary.put("closingBalance", closingBalance);
        summary.put("netMovement", netMovement);
        summary.put("assignedAssets", assignedQuantity);
        summary.put("expendedAssets", expendedQuantity);
        summary.put("purchases", purchaseQuantity);
        summary.put("transferIn", transferIn);
        summary.put("transferOut", transferOut);
        summary.put("totalTransfers", transfers.size());
        summary.put("totalAssignments", assignments.size());
        summary.put("activeStatusCount", assets.stream().filter(a -> a.getStatus() == AssetStatus.ACTIVE).count());
        summary.put("damagedStatusCount", assets.stream().filter(a -> a.getStatus() == AssetStatus.DAMAGED).count());
        summary.put("missingStatusCount", assets.stream().filter(a -> a.getStatus() == AssetStatus.MISSING).count());
        return summary;
    }

    public Map<String, Object> getTrendsForPeriod(LocalDate from, LocalDate to, Long baseId, Long equipmentTypeId) {
        List<Purchase> purchases = filteredPurchases(from, to, baseId, equipmentTypeId);
        List<Transfer> transfers = filteredTransfers(from, to, baseId, equipmentTypeId);
        List<Assignment> assignments = assignmentRepository.findAll().stream()
            .filter(a -> within(a.getAssignmentDate(), from, to))
            .filter(a -> baseId == null || a.getBase().getId().equals(baseId))
            .filter(a -> equipmentTypeId == null || a.getAsset().getEquipmentType().getId().equals(equipmentTypeId)).toList();
        List<Expenditure> expenditures = expenditureRepository.findAll().stream()
            .filter(e -> within(e.getExpenditureDate(), from, to))
            .filter(e -> baseId == null || e.getBase().getId().equals(baseId))
            .filter(e -> equipmentTypeId == null || e.getEquipmentType().getId().equals(equipmentTypeId)).toList();

        Map<String, Object> data = new HashMap<>();
        data.put("purchases", purchases.size());
        data.put("transfers", transfers.size());
        data.put("assignments", assignments.size());
        data.put("expenditures", expenditures.size());
        data.put("purchaseValue", purchases.stream().mapToDouble(p -> p.getUnitPrice().doubleValue() * p.getQuantity()).sum());
        data.put("expenditureValue", expenditures.stream().mapToDouble(e -> e.getQuantity() * 1000).sum());
        data.put("transferIn", transfers.stream().filter(t -> baseId == null || t.getToBase().getId().equals(baseId)).mapToInt(Transfer::getQuantity).sum());
        data.put("transferOut", transfers.stream().filter(t -> baseId == null || t.getFromBase().getId().equals(baseId)).mapToInt(Transfer::getQuantity).sum());
        return data;
    }

    public Map<String, Object> getMovement(LocalDate from, LocalDate to, Long baseId, Long equipmentTypeId) {
        List<Map<String, Object>> transferIn = new ArrayList<>();
        List<Map<String, Object>> transferOut = new ArrayList<>();
        filteredTransfers(from, to, baseId, equipmentTypeId).forEach(t -> {
            Map<String, Object> row = movementRow(t.getTransferDate(), t.getToBase(), t.getEquipmentType(), t.getQuantity(), t.getReferenceNumber());
            if (baseId == null || t.getToBase().getId().equals(baseId)) transferIn.add(row);
            if (baseId == null || t.getFromBase().getId().equals(baseId)) transferOut.add(movementRow(t.getTransferDate(), t.getFromBase(), t.getEquipmentType(), t.getQuantity(), t.getReferenceNumber()));
        });
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("purchases", filteredPurchases(from, to, baseId, equipmentTypeId).stream().map(p -> movementRow(p.getPurchaseDate(), p.getBase(), p.getEquipmentType(), p.getQuantity(), p.getReferenceNumber())).toList());
        result.put("transferIn", transferIn);
        result.put("transferOut", transferOut);
        return result;
    }

    private List<Asset> filteredAssets(Long baseId, Long equipmentTypeId) {
        return assetRepository.findAll().stream()
                .filter(a -> baseId == null || a.getBase().getId().equals(baseId))
                .filter(a -> equipmentTypeId == null || a.getEquipmentType().getId().equals(equipmentTypeId)).toList();
    }

    private List<Purchase> filteredPurchases(LocalDate from, LocalDate to, Long baseId, Long equipmentTypeId) {
        return purchaseRepository.findByPurchaseDateBetween(from, to).stream()
                .filter(p -> baseId == null || p.getBase().getId().equals(baseId))
                .filter(p -> equipmentTypeId == null || p.getEquipmentType().getId().equals(equipmentTypeId)).toList();
    }

    private List<Transfer> filteredTransfers(LocalDate from, LocalDate to, Long baseId, Long equipmentTypeId) {
        return transferRepository.findByTransferDateBetween(from, to).stream()
                .filter(t -> baseId == null || t.getFromBase().getId().equals(baseId) || t.getToBase().getId().equals(baseId))
                .filter(t -> equipmentTypeId == null || t.getEquipmentType().getId().equals(equipmentTypeId)).toList();
    }

    private boolean within(LocalDate value, LocalDate from, LocalDate to) {
        return !value.isBefore(from) && !value.isAfter(to);
    }

    private Map<String, Object> movementRow(LocalDate date, Base base, EquipmentType type, Integer quantity, String reference) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("date", date);
        row.put("base", base.getCode());
        row.put("equipmentType", type.getName());
        row.put("quantity", quantity);
        row.put("referenceNumber", reference);
        return row;
    }
}
