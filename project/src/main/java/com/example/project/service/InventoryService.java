package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Inventory;
import com.example.project.model.ProductType;
import com.example.project.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final FoodService foodService;
    private final ToyService toyService;
    private final MedicineService medicineService;

    public InventoryService(InventoryRepository inventoryRepository, FoodService foodService, ToyService toyService, MedicineService medicineService) {
        this.inventoryRepository = inventoryRepository;
        this.foodService = foodService;
        this.toyService = toyService;
        this.medicineService = medicineService;
    }

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public Inventory findById(Long id) {
        return inventoryRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(String.format("The inventory with id = %s does not exist in the database.",id.toString())));
    }

    public void deleteById(Long id) {
        if(inventoryRepository.existsById(id)){
            inventoryRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("The inventory with id = %s does not exist in the database.",id.toString()));
        }
    }

    public Inventory create(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Inventory update(Inventory inventory) {
        if(inventoryRepository.existsById(inventory.getId())){
            return inventoryRepository.save(inventory);
        } else {
            throw new EntityNotFoundException(String.format("The inventory with id = %s does not exist in the database.",inventory.getId().toString()));
        }
    }

    public ProductType findProductForInventory(Long id) {
        if (inventoryRepository.existsById(id)) {
            if (foodService.existsByInventoryId(id)) {
                return new ProductType("food", foodService.findByInventoryId(id));
            } else if (toyService.existsByInventoryId(id)) {
                return new ProductType("toy", toyService.findByInventoryId(id));
            } else if (medicineService.existsByInventoryId(id)) {
                return new ProductType("medicine", medicineService.findByInventoryId(id));
            } else {
                throw new EntityNotFoundException(String.format("The inventory with id = %s does not exist in the database.", id.toString()));
            }
        } else {
            throw new EntityNotFoundException(String.format("The inventory with id = %s does not exist in the database.", id.toString()));
        }
    }
}
