package com.example.militaryasset.repository;

import com.example.militaryasset.entity.Base;
import com.example.militaryasset.entity.EquipmentType;
import com.example.militaryasset.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByFromBase(Base base);
    List<Transfer> findByToBase(Base base);
    List<Transfer> findByEquipmentType(EquipmentType equipmentType);
    List<Transfer> findByTransferDateBetween(LocalDate from, LocalDate to);
}
