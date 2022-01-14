package com.example.project.repository;

import com.example.project.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    Boolean existsByInventoryId(Long id);
    Food findByInventoryId(Long id);
}
