package com.example.militaryasset.repository;

import com.example.militaryasset.entity.Asset;
import com.example.militaryasset.entity.AssetStatus;
import com.example.militaryasset.entity.Base;
import com.example.militaryasset.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByAssetCode(String assetCode);
    List<Asset> findByBase(Base base);
    List<Asset> findByEquipmentType(EquipmentType equipmentType);
    List<Asset> findByBaseAndEquipmentType(Base base, EquipmentType equipmentType);
    List<Asset> findByStatus(AssetStatus status);
}
