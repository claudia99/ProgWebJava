package com.example.project.repository;

import com.example.project.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Boolean existsByInventoryId(Long id);
    Medicine findByInventoryId(Long id);
}
