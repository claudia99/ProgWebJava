package com.example.project.service;

import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.*;
import com.example.project.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.abs;

@Service
@Transactional // se fac updateuri in bd ddecat daca totu mwrge corect
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final InventoryService inventoryService;
    private final FoodService foodService;
    private final MedicineService medicineService;
    private final ToyService toyService;
    private final ItemService itemService;

    public PurchaseService(PurchaseRepository purchaseRepository, InventoryService inventoryService, FoodService foodService, MedicineService medicineService, ToyService toyService, ItemService itemService) {
        this.purchaseRepository = purchaseRepository;
        this.inventoryService = inventoryService;
        this.foodService = foodService;
        this.medicineService = medicineService;
        this.toyService = toyService;
        this.itemService = itemService;
    }

    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    public Purchase findById(Long id) {
        return purchaseRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(String.format("The purchase with id = %s does not exist in the database.",id.toString())));
     }

     public List<Purchase> findByClient(Long id) {
        return purchaseRepository.findByClientId(id);
     }

    public void deleteById(Long id) {
        if(purchaseRepository.existsById(id)) {
            List<Item> itemsToBeDeleted = itemService.findByPurchaseId(id);
            for(Item it: itemsToBeDeleted) {
                Inventory inventoryToBeModified = it.getInventory();
                inventoryToBeModified.setAvailableQuantity(inventoryToBeModified.getAvailableQuantity() + it.getOrderedQuantity());
                inventoryToBeModified = inventoryService.update(inventoryToBeModified);
                itemService.deleteById(it.getId());
            }
            purchaseRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("The purchase with id = %s does not exist in the database.",id.toString()));
        }
    }

    public Purchase create(Purchase purchase) {
        Float price = 0F;

        for (Item it : purchase.getProducts()) {
            Inventory existingInventory = inventoryService.findById(it.getInventory().getId());
            if (existingInventory.getAvailableQuantity() - it.getOrderedQuantity() >= 0) {
                ProductType product = inventoryService.findProductForInventory(it.getInventory().getId());
                switch (product.getType()) {
                    case "food":
                        Food food = foodService.findById(product.getId());
                        price+=food.getPrice() * abs(it.getOrderedQuantity());
                        break;
                    case "toy":
                        Toy toy = toyService.findById(product.getId());
                        price += toy.getPrice() * abs(it.getOrderedQuantity());
                        break;
                    case "medicine":
                        Medicine medicine = medicineService.findById(product.getId());
                        price +=medicine.getPrice() * abs(it.getOrderedQuantity());
                        break;
                }
                existingInventory.setAvailableQuantity(existingInventory.getAvailableQuantity() - it.getOrderedQuantity());

                existingInventory = inventoryService.update(existingInventory);
            }
            else {
                throw new BadRequestException("the purchase cannot be confirmed; not enough items in inventory");
            }
        }
        purchase.setPrice(price);
        purchase.setTime(LocalDateTime.now());
        Purchase result =   purchaseRepository.save(purchase);

        for(Item it: purchase.getProducts()){
            it.setPurchase(result);
            Item savedItem = itemService.create(it);
        }

        return result;
    }

    public Purchase update(Purchase purchase) {
        if(purchaseRepository.existsById(purchase.getId())) {
            return purchaseRepository.save(purchase);
        } else {
            throw new EntityNotFoundException(String.format("The purchase with id = %s does not exist in the database.",purchase.getId().toString()));
        }
    }


}
