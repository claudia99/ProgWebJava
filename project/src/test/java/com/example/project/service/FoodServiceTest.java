package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Food;
import com.example.project.model.Inventory;
import com.example.project.repository.FoodRepository;
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
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodService foodService;

    private Food expectedFood;

    @BeforeEach
    void setUp() {
        expectedFood = Food.builder()
                .id(1L)
                .animal("pisica")
                .brand("un brand")
                .type("uscata")
                .price(12.123F)
                .quantityPerUnit(12L)
                .inventory(Inventory.builder().id(1L).build())
                .build();

    }

    @Test
    @DisplayName("find all food - happy flow")
    public void test_findAll_happyFlow() {
        List<Food> foodList = new ArrayList<>();
        foodList.add(expectedFood);

        when(foodRepository.findAll()).thenReturn(foodList);

        List<Food> result = foodService.findAll();

        assertEquals(foodList.size(), result.size());
        assertEquals(expectedFood.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedFood.getAnimal(), result.stream().findFirst().get().getAnimal());
        assertEquals(expectedFood.getBrand(), result.stream().findFirst().get().getBrand());
        assertEquals(expectedFood.getPrice(), result.stream().findFirst().get().getPrice());
        assertEquals(expectedFood.getInventory(), result.stream().findFirst().get().getInventory());
        assertEquals(expectedFood.getType(), result.stream().findFirst().get().getType());
        assertEquals(expectedFood.getQuantityPerUnit(), result.stream().findFirst().get().getQuantityPerUnit());

        verify(foodRepository).findAll();
    }

    @Test
    @DisplayName("find food by id - happy flow")
    public void test_findById_happyFlow() {
        Long id = expectedFood.getId();

        when(foodRepository.findById(id)).thenReturn(Optional.of(expectedFood));

        Food result = foodService.findById(id);

        assertEquals(expectedFood.getId(), result.getId());
        assertEquals(expectedFood.getPrice(), result.getPrice());
        assertEquals(expectedFood.getBrand(), result.getBrand());
        assertEquals(expectedFood.getAnimal(), result.getAnimal());
        assertEquals(expectedFood.getQuantityPerUnit(), result.getQuantityPerUnit());
        assertEquals(expectedFood.getType(), result.getType());
        assertEquals(expectedFood.getInventory(), result.getInventory());

        verify(foodRepository).findById(id);
    }


    @Test
    @DisplayName("find food by id - food does not exist in database")
    public void test_findById_throwsEntityNotFoundException_whenFoodNotFound() {
        Long id = expectedFood.getId();

        when(foodRepository.findById(id)).thenThrow(new EntityNotFoundException(String.format("The food item with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> foodService.findById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The food item with id = %s does not exist in the database.",id.toString()));

        verify(foodRepository).findById(id);
    }

    @Test
    @DisplayName("delete food by id - happy flow")
    public void test_deleteById_happyFlow() {
        Long id = expectedFood.getId();

        when(foodRepository.existsById(id)).thenReturn(true);
        doNothing().when(foodRepository).deleteById(id);

        foodService.deleteById(id);

        verify(foodRepository).existsById(id);
        verify(foodRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete food by id - food does not exist in database")
    public void test_deleteById_throwsEntityNotFoundException_whenFoodNotFound() {
        Long id = expectedFood.getId();

        when(foodRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                foodService.deleteById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The food item with id = %s does not exist in the database.",id.toString()));

        verify(foodRepository).existsById(id);
        verify(foodRepository, times(0)).deleteById(id);
    }

    @Test
    @DisplayName("create food - happy flow")
    public void test_create_happyFlow() {
        Food food = Food.builder()
                .animal("pisica")
                .brand("un brand")
                .type("uscata")
                .price(12.123F)
                .quantityPerUnit(12L)
                .inventory(Inventory.builder().id(1L).build())
                .build();

        when(foodRepository.save(food)).thenReturn(expectedFood);

        Food result = foodService.create(food);

        assertEquals(expectedFood.getId(), result.getId());
        assertEquals(expectedFood.getAnimal(), result.getAnimal());
        assertEquals(expectedFood.getBrand(), result.getBrand());
        assertEquals(expectedFood.getPrice(), result.getPrice());
        assertEquals(expectedFood.getInventory(), result.getInventory());
        assertEquals(expectedFood.getType(), result.getType());
        assertEquals(expectedFood.getQuantityPerUnit(), result.getQuantityPerUnit());

        verify(foodRepository).save(food);
    }

    @Test
    @DisplayName("update a food - happy flow")
    public void test_update_happyFlow() {
        Food food = expectedFood;
        Long id = expectedFood.getId();

        when(foodRepository.existsById(id)).thenReturn(true);
        when(foodRepository.save(food)).thenReturn(expectedFood);

        Food result = foodService.update(food);

        assertEquals(expectedFood.getId(), result.getId());
        assertEquals(expectedFood.getAnimal(), result.getAnimal());
        assertEquals(expectedFood.getBrand(), result.getBrand());
        assertEquals(expectedFood.getPrice(), result.getPrice());
        assertEquals(expectedFood.getInventory(), result.getInventory());
        assertEquals(expectedFood.getType(), result.getType());
        assertEquals(expectedFood.getQuantityPerUnit(), result.getQuantityPerUnit());

        verify(foodRepository).existsById(id);
        verify(foodRepository).save(food);
    }

    @Test
    @DisplayName("update a food - food does not exist in database")
    public void test_update_throwsEntityNotFoundException_whenFoodNotFound() {
        Long id = expectedFood.getId();

        when(foodRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                foodService.update(expectedFood));

        assertThat(ex.getMessage()).isEqualTo(String.format("The food item with id = %s does not exist in the database.",id.toString()));

        verify(foodRepository).existsById(id);
        verify(foodRepository, times(0)).save(expectedFood);
    }

    @Test
    @DisplayName("check if food exists in inventory - happy flow")
    void test_existsByInventoryId_happyFlow() {
        Long id = expectedFood.getInventory().getId();

        when(foodRepository.existsByInventoryId(id)).thenReturn(true);

        Boolean result = foodService.existsByInventoryId(id);

        assertEquals(true, result);

        verify(foodRepository).existsByInventoryId(id);
    }

    @Test
    @DisplayName("get food id by inventory - happy flow")
    public void test_findByInventoryId_happyFlow() {
        Long id = expectedFood.getInventory().getId();

        when(foodRepository.findByInventoryId(id)).thenReturn(expectedFood);

        Long result = foodService.findByInventoryId(id);

        assertEquals(expectedFood.getId(), result);

        verify(foodRepository).findByInventoryId(id);
    }
}