package com.example.militaryasset.repository;

import com.example.militaryasset.entity.Assignment;
import com.example.militaryasset.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByBase(Base base);
    List<Assignment> findByPersonnelNameContainingIgnoreCase(String personnelName);
}
