package com.example.project.service;

import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.*;
import com.example.project.repository.PurchaseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private FoodService foodService;

    @Mock
    private MedicineService medicineService;

    @Mock
    private ToyService toyService;

    @InjectMocks
    private PurchaseService purchaseService;

    private Purchase expectedPurchase;
    private Item item;
    private List<Item> itemList;

    @BeforeEach
    void setUp() {
        itemList = new ArrayList<>();
        item = Item.builder()
                .id(1L)
                .orderedQuantity(100L)
                .inventory(Inventory.builder().id(1L).availableQuantity(50L).build())
                .build();
        itemList.add(item);

        expectedPurchase = Purchase.builder()
                .id(1L)
                .price(12.123F)
                .time(LocalDateTime.now())
                .client(Client.builder().id(1L).build())
                .products(itemList)
                .build();

    }

    @Test
    @DisplayName("find all purchases - happy flow")
    public void test_findAll_happyFlow() {
        List<Purchase> purchaseList = new ArrayList<>();
        purchaseList.add(expectedPurchase);

        when(purchaseRepository.findAll()).thenReturn(purchaseList);

        List<Purchase> result = purchaseService.findAll();

        assertEquals(purchaseList.size(), result.size());
        assertEquals(expectedPurchase.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedPurchase.getPrice(), result.stream().findFirst().get().getPrice());
        assertEquals(expectedPurchase.getTime(), result.stream().findFirst().get().getTime());
        assertEquals(expectedPurchase.getClient(), result.stream().findFirst().get().getClient());
        //assertEquals(expectedPurchase.getProducts(), result.stream().findFirst().get().getProducts());

        verify(purchaseRepository).findAll();
    }

    @Test
    @DisplayName("find purchase by id - happy flow")
    public void test_findById_happyFlow() {
        Long id = expectedPurchase.getId();

        when(purchaseRepository.findById(id)).thenReturn(Optional.of(expectedPurchase));

        Purchase result = purchaseService.findById(id);

        assertEquals(expectedPurchase.getId(), result.getId());
        assertEquals(expectedPurchase.getPrice(), result.getPrice());
        assertEquals(expectedPurchase.getTime(), result.getTime());
        assertEquals(expectedPurchase.getClient(), result.getClient());
        //assertEquals(expectedPurchase.getProducts(), result.getProducts());

        verify(purchaseRepository).findById(id);
    }


    @Test
    @DisplayName("find purchase by id - purchase does not exist in database")
    public void test_findById_throwsEntityNotFoundException_whenPurchaseNotFound() {
        Long id = expectedPurchase.getId();

        when(purchaseRepository.findById(id)).thenThrow(new EntityNotFoundException(String.format("The purchase with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> purchaseService.findById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The purchase with id = %s does not exist in the database.",id.toString()));

        verify(purchaseRepository).findById(id);
    }

    @Test
    @DisplayName("find purchase by client id - happy flow")
    public void test_findByClient_happyFlow() {
        Long id = expectedPurchase.getClient().getId();
        List<Purchase> purchaseList = new ArrayList<>();
        purchaseList.add(expectedPurchase);

        when(purchaseRepository.findByClientId(id)).thenReturn(purchaseList);
        List<Purchase> result = purchaseService.findByClient(id);

        assertEquals(purchaseList.size(), result.size());
        assertEquals(expectedPurchase.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedPurchase.getPrice(), result.stream().findFirst().get().getPrice());
        assertEquals(expectedPurchase.getTime(), result.stream().findFirst().get().getTime());
        assertEquals(expectedPurchase.getClient(), result.stream().findFirst().get().getClient());
        //assertEquals(expectedPurchase.getProducts(), result.stream().findFirst().get().getProducts());

        verify(purchaseRepository).findByClientId(id);
    }

    @Test
    @DisplayName("delete purchase by id - happy flow")
    public void test_deleteById_happyFlow() {
        Long id = expectedPurchase.getId();
        expectedPurchase.getProducts().stream().findFirst().get().setPurchase(expectedPurchase);
        when(purchaseRepository.existsById(id)).thenReturn(true);
        when(itemService.findByPurchaseId(id)).thenReturn(expectedPurchase.getProducts());
        when(inventoryService.update(expectedPurchase.getProducts().stream().findFirst().get().getInventory())).thenReturn(expectedPurchase.getProducts().stream().findFirst().get().getInventory());

        doNothing().when(purchaseRepository).deleteById(id);

        purchaseService.deleteById(id);

        verify(purchaseRepository).existsById(id);
        verify(purchaseRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete purchase by id - purchase does not exist in database")
    public void test_deleteById_throwsEntityNotFoundException_whenPurchaseNotFound() {
        Long id = expectedPurchase.getId();

        when(purchaseRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                purchaseService.deleteById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The purchase with id = %s does not exist in the database.",id.toString()));

        verify(purchaseRepository).existsById(id);
        verify(purchaseRepository, times(0)).deleteById(id);
    }

    @Test
    @DisplayName("create purchase - happy flow - product is food")
    public void test_create_productIsFood_happyFlow() {
        Inventory inventory = Inventory.builder().id(1L).availableQuantity(500L).build();
        ProductType productType = ProductType.builder().id(1L).type("food").build();
        Purchase purchase = Purchase.builder()
                //.price(12.123F)
                //.time(LocalDateTime.now())
                .client(Client.builder().id(1L).build())
                .products(itemList)
                .build();
        Food food = Food.builder().id(1L).price(100F).build();

        when(inventoryService.findById(purchase.getProducts().stream().findFirst().get().getInventory().getId())).thenReturn(inventory);
        when(inventoryService.findProductForInventory(purchase.getProducts().stream().findFirst().get().getInventory().getId())).thenReturn(productType);

        when(foodService.findById(productType.getId())).thenReturn(food);
        when(inventoryService.update(inventory)).thenReturn(inventory);
        when(purchaseRepository.save(purchase)).thenReturn(expectedPurchase);
        when(itemService.create(item)).thenReturn(item);

        Purchase result = purchaseService.create(purchase);

        // assert
        verify(purchaseRepository).save(purchase);
        verify(foodService).findById(productType.getId());
        verify(toyService, times(0)).findById(productType.getId());
        verify(medicineService, times(0)).findById(productType.getId());
    }

    @Test
    @DisplayName("create purchase - happy flow - product is toy")
    public void test_create_productIsToy_happyFlow() {
        Inventory inventory = Inventory.builder().id(1L).availableQuantity(500L).build();
        ProductType productType = ProductType.builder().id(1L).type("toy").build();
        Purchase purchase = Purchase.builder()
                //.price(12.123F)
                //.time(LocalDateTime.now())
                .client(Client.builder().id(1L).build())
                .products(itemList)
                .build();
        Toy toy = Toy.builder().id(1L).price(100F).build();

        when(inventoryService.findById(purchase.getProducts().stream().findFirst().get().getInventory().getId())).thenReturn(inventory);
        when(inventoryService.findProductForInventory(purchase.getProducts().stream().findFirst().get().getInventory().getId())).thenReturn(productType);

        when(toyService.findById(productType.getId())).thenReturn(toy);
        when(inventoryService.update(inventory)).thenReturn(inventory);
        when(purchaseRepository.save(purchase)).thenReturn(expectedPurchase);
        when(itemService.create(item)).thenReturn(item);

        Purchase result = purchaseService.create(purchase);

        // assert
        verify(purchaseRepository).save(purchase);
        verify(foodService, times(0)).findById(productType.getId());
        verify(toyService, times(1)).findById(productType.getId());
        verify(medicineService, times(0)).findById(productType.getId());
    }

    @Test
    @DisplayName("create purchase - happy flow - product is medicine")
    public void test_create_productIsMedicine_happyFlow() {
        Inventory inventory = Inventory.builder().id(1L).availableQuantity(500L).build();
        ProductType productType = ProductType.builder().id(1L).type("medicine").build();
        Purchase purchase = Purchase.builder()
                //.price(12.123F)
                //.time(LocalDateTime.now())
                .client(Client.builder().id(1L).build())
                .products(itemList)
                .build();
        Medicine medicine = Medicine.builder().id(1L).price(100F).build();

        when(inventoryService.findById(purchase.getProducts().stream().findFirst().get().getInventory().getId())).thenReturn(inventory);
        when(inventoryService.findProductForInventory(purchase.getProducts().stream().findFirst().get().getInventory().getId())).thenReturn(productType);

        when(medicineService.findById(productType.getId())).thenReturn(medicine);
        when(inventoryService.update(inventory)).thenReturn(inventory);
        when(purchaseRepository.save(purchase)).thenReturn(expectedPurchase);
        when(itemService.create(item)).thenReturn(item);

        Purchase result = purchaseService.create(purchase);

        // assert
        verify(purchaseRepository).save(purchase);
        verify(foodService, times(0)).findById(productType.getId());
        verify(toyService, times(0)).findById(productType.getId());
        verify(medicineService, times(1)).findById(productType.getId());
    }

    @Test
    @DisplayName("create a purchase - wanted product quantity is less then available quantity")
    public void test_create_throwsEntityNotFoundException_whenPurchaseNotFound() {
        //create Inventory with available quantity< available quantity din purchase
        Inventory inventory = Inventory.builder().id(1L).availableQuantity(10L).build();
        Long id = expectedPurchase.getId();

        when(inventoryService.findById(expectedPurchase.getProducts().stream().findFirst().get().getInventory().getId())).thenReturn(inventory);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () ->
                purchaseService.create(expectedPurchase));

        assertThat(ex.getMessage()).isEqualTo("the purchase cannot be confirmed; not enough items in inventory");
        verify(inventoryService, times(1)).findById(id);
        verify(purchaseRepository, times(0)).save(expectedPurchase);
    }

    @Test
    @DisplayName("update a purchase - happy flow")
    public void test_update_happyFlow() {
        Purchase purchase = expectedPurchase;
        Long id = expectedPurchase.getId();

        when(purchaseRepository.existsById(id)).thenReturn(true);
        when(purchaseRepository.save(purchase)).thenReturn(expectedPurchase);

        Purchase result = purchaseService.update(purchase);

        assertEquals(expectedPurchase.getId(), result.getId());
        assertEquals(expectedPurchase.getPrice(), result.getPrice());
        assertEquals(expectedPurchase.getTime(), result.getTime());
        assertEquals(expectedPurchase.getClient(), result.getClient());
        //assertEquals(expectedPurchase.getProducts(), result.getProducts());

        verify(purchaseRepository).existsById(id);
        verify(purchaseRepository).save(purchase);
    }

    @Test
    @DisplayName("update a purchase - purchase does not exist in database")
    public void test_update_throwsEntityNotFoundException_whenPurchaseNotFound() {
        Long id = expectedPurchase.getId();

        when(purchaseRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                purchaseService.update(expectedPurchase));

        assertThat(ex.getMessage()).isEqualTo(String.format("The purchase with id = %s does not exist in the database.",id.toString()));

        verify(purchaseRepository).existsById(id);
        verify(purchaseRepository, times(0)).save(expectedPurchase);
    }
}