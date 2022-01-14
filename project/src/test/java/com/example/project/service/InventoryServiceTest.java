package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.*;
import com.example.project.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private FoodService foodService;

    @Mock
    private ToyService toyService;

    @Mock
    private MedicineService medicineService;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory expectedInventory;

    @BeforeEach
    void setUp() {
        expectedInventory = Inventory.builder()
                .id(1L)
                .availableQuantity(121L)
                .food(Food.builder().id(2L).build())
                .toy(Toy.builder().id(3L).build())
                .medicine(Medicine.builder().id(4L).build())
                //.purchases()
                .build();
    }

    @Test
    @DisplayName("find all inventories - happy flow")
    public void test_findAll_happyFlow() {
        List<Inventory> inventoryList = new ArrayList<>();
        inventoryList.add(expectedInventory);

        when(inventoryRepository.findAll()).thenReturn(inventoryList);

        List<Inventory> result = inventoryService.findAll();

        assertEquals(inventoryList.size(), result.size());
        assertEquals(expectedInventory.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedInventory.getFood(), result.stream().findFirst().get().getFood());
        assertEquals(expectedInventory.getMedicine(), result.stream().findFirst().get().getMedicine());
        assertEquals(expectedInventory.getToy(), result.stream().findFirst().get().getToy());
        assertEquals(expectedInventory.getAvailableQuantity(), result.stream().findFirst().get().getAvailableQuantity());
       // assertEquals(expectedInventory.getPurchases(), result.stream().findFirst().get().getPurchases());

        verify(inventoryRepository).findAll();
    }
    @Test
    @DisplayName("find inventory by id - happy flow")
    public void test_findById_happyFlow() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(expectedInventory));

        Inventory result = inventoryService.findById(id);

        assertEquals(expectedInventory.getId(), result.getId());
        assertEquals(expectedInventory.getFood(), result.getFood());
        assertEquals(expectedInventory.getToy(), result.getToy());
        assertEquals(expectedInventory.getMedicine(), result.getMedicine());
        assertEquals(expectedInventory.getAvailableQuantity(), result.getAvailableQuantity());
        //assertEquals(expectedInventory.getPurchases(), result.getPurchases());

        verify(inventoryRepository).findById(id);
    }


    @Test
    @DisplayName("find inventory by id - inventory does not exist in database")
    public void test_findById_throwsEntityNotFoundException_whenInventoryNotFound() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.findById(id)).thenThrow(new EntityNotFoundException(String.format("The inventory with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> inventoryService.findById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The inventory with id = %s does not exist in the database.",id.toString()));

        verify(inventoryRepository).findById(id);
    }

    @Test
    @DisplayName("delete inventory by id - happy flow")
    public void test_deleteById_happyFlow() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.existsById(id)).thenReturn(true);
        doNothing().when(inventoryRepository).deleteById(id);

        inventoryService.deleteById(id);

        verify(inventoryRepository).existsById(id);
        verify(inventoryRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete inventory by id - inventory does not exist in database")
    public void test_deleteById_throwsEntityNotFoundException_whenInventoryNotFound() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                inventoryService.deleteById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The inventory with id = %s does not exist in the database.",id.toString()));

        verify(inventoryRepository).existsById(id);
        verify(inventoryRepository, times(0)).deleteById(id);
    }

    @Test
    @DisplayName("create inventory - happy flow")
    public void test_create_happyFlow() {
        Inventory inventory = Inventory.builder()
                .availableQuantity(121L)
                .food(Food.builder().id(2L).build())
                .toy(Toy.builder().id(3L).build())
                .medicine(Medicine.builder().id(4L).build())
                //.purchases()
                .build();

        when(inventoryRepository.save(inventory)).thenReturn(expectedInventory);

        Inventory result = inventoryService.create(inventory);

        assertEquals(expectedInventory.getId(), result.getId());
        assertEquals(expectedInventory.getFood(), result.getFood());
        assertEquals(expectedInventory.getToy(), result.getToy());
        assertEquals(expectedInventory.getMedicine(), result.getMedicine());
        assertEquals(expectedInventory.getAvailableQuantity(), result.getAvailableQuantity());
        //assertEquals(expectedInventory.getPurchases(), result.getPurchases());

        verify(inventoryRepository).save(inventory);
    }

    @Test
    @DisplayName("update an inventory - happy flow")
    public void test_update_happyFlow() {
        Inventory inventory = expectedInventory;
        Long id = expectedInventory.getId();

        when(inventoryRepository.existsById(id)).thenReturn(true);
        when(inventoryRepository.save(inventory)).thenReturn(expectedInventory);

        Inventory result = inventoryService.update(inventory);

        assertEquals(expectedInventory.getId(), result.getId());
        assertEquals(expectedInventory.getFood(), result.getFood());
        assertEquals(expectedInventory.getToy(), result.getToy());
        assertEquals(expectedInventory.getMedicine(), result.getMedicine());
        assertEquals(expectedInventory.getAvailableQuantity(), result.getAvailableQuantity());
        //assertEquals(expectedInventory.getPurchases(), result.getPurchases());

        verify(inventoryRepository).existsById(id);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    @DisplayName("update an inventory - inventory does not exist in database")
    public void test_update_throwsEntityNotFoundException_whenFoodNotFound() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                inventoryService.update(expectedInventory));

        assertThat(ex.getMessage()).isEqualTo(String.format("The inventory with id = %s does not exist in the database.",id.toString()));

        verify(inventoryRepository).existsById(id);
        verify(inventoryRepository, times(0)).save(expectedInventory);
    }

    @Test
    @DisplayName("find product for inventory - product is food")
    void test_findProductForInventory_productIsFood() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.existsById(id)).thenReturn(true);
        when(foodService.existsByInventoryId(id)).thenReturn(true);
        ProductType product = new ProductType("food", expectedInventory.getFood().getId());
        when(foodService.findByInventoryId(id)).thenReturn(product.getId());

        ProductType result = inventoryService.findProductForInventory(id);

        assertEquals(product.getType(), result.getType());
        assertEquals(product.getId(), result.getId());

        verify(inventoryRepository).existsById(id);
        verify(foodService).existsByInventoryId(id);
        verify(foodService).findByInventoryId(id);

    }

    @Test
    @DisplayName("find product for inventory - product is toy")
    void test_findProductForInventory_productIsToy() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.existsById(id)).thenReturn(true);
        when(foodService.existsByInventoryId(id)).thenReturn(false);
        when(toyService.existsByInventoryId(id)).thenReturn(true);
        ProductType product = new ProductType("toy", expectedInventory.getToy().getId());
        when(toyService.findByInventoryId(id)).thenReturn(product.getId());

        ProductType result = inventoryService.findProductForInventory(id);

        assertEquals(product.getType(), result.getType());
        assertEquals(product.getId(), result.getId());

        verify(inventoryRepository).existsById(id);
        verify(foodService).existsByInventoryId(id);
        verify(toyService).existsByInventoryId(id);
        verify(toyService).findByInventoryId(id);
        verify(foodService, times(0)).findByInventoryId(id);
    }

    @Test
    @DisplayName("find product for inventory - product is medicine")
    void test_findProductForInventory_productIsMedicine() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.existsById(id)).thenReturn(true);
        when(foodService.existsByInventoryId(id)).thenReturn(false);
        when(toyService.existsByInventoryId(id)).thenReturn(false);
        when(medicineService.existsByInventoryId(id)).thenReturn(true);
        ProductType product = new ProductType("medicine", expectedInventory.getMedicine().getId());
        when(medicineService.findByInventoryId(id)).thenReturn(product.getId());

        ProductType result = inventoryService.findProductForInventory(id);

        assertEquals(product.getType(), result.getType());
        assertEquals(product.getId(), result.getId());

        verify(inventoryRepository).existsById(id);
        verify(foodService).existsByInventoryId(id);
        verify(toyService).existsByInventoryId(id);
        verify(medicineService).existsByInventoryId(id);
        verify(medicineService).findByInventoryId(id);
        verify(toyService, times(0)).findByInventoryId(id);
        verify(foodService, times(0)).findByInventoryId(id);
    }

    @Test
    @DisplayName("find product for inventory - inventory is not food, medicine or toy ")
    public void test_throwsEntityNotFoundException_whenInventoryIsNotFoodInventoryToy() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.existsById(id)).thenReturn(true);
        when(foodService.existsByInventoryId(id)).thenReturn(false);
        when(toyService.existsByInventoryId(id)).thenReturn(false);
        when(medicineService.existsByInventoryId(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                inventoryService.findProductForInventory(id));

        assertThat(ex.getMessage()).isEqualTo(String.format("The inventory with id = %s does not exist in the database.", id.toString()));
;

        verify(inventoryRepository).existsById(id);
        verify(foodService).existsByInventoryId(id);
        verify(toyService).existsByInventoryId(id);
        verify(medicineService).existsByInventoryId(id);
        verify(medicineService, times(0)).findByInventoryId(id);
        verify(toyService, times(0)).findByInventoryId(id);
        verify(foodService, times(0)).findByInventoryId(id);
    }

    @Test
    @DisplayName("find product for inventory - product does not exist in database")
    public void test_throwsEntityNotFoundException_whenInventoryNotFound() {
        Long id = expectedInventory.getId();

        when(inventoryRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                inventoryService.findProductForInventory(id));

        assertThat(ex.getMessage()).isEqualTo(String.format("The inventory with id = %s does not exist in the database.", id.toString()));
        ;

        verify(inventoryRepository).existsById(id);
        verify(foodService, times(0)).existsByInventoryId(id);
        verify(toyService, times(0)).existsByInventoryId(id);
        verify(medicineService, times(0)).existsByInventoryId(id);
        verify(medicineService, times(0)).findByInventoryId(id);
        verify(toyService, times(0)).findByInventoryId(id);
        verify(foodService, times(0)).findByInventoryId(id);
    }
}