package com.example.project.repository;

import com.example.project.model.Toy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToyRepository extends JpaRepository<Toy, Long> {
    Boolean existsByInventoryId(Long id);
    Toy findByInventoryId(Long id);
}
