package com.example.militaryasset.repository;

import com.example.militaryasset.entity.Base;
import com.example.militaryasset.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {
    List<Expenditure> findByBase(Base base);
    List<Expenditure> findByExpenditureDateBetween(LocalDate from, LocalDate to);
}
