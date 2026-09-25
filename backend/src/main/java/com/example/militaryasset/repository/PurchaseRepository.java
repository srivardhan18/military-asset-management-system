package com.example.militaryasset.repository;

import com.example.militaryasset.entity.Base;
import com.example.militaryasset.entity.EquipmentType;
import com.example.militaryasset.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByBase(Base base);
    List<Purchase> findByEquipmentType(EquipmentType equipmentType);
    List<Purchase> findByPurchaseDateBetween(LocalDate from, LocalDate to);
}
