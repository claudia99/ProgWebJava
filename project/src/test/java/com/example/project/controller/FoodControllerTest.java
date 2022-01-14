package com.example.project.controller;

import com.example.project.dto.FoodDto;
import com.example.project.dto.InventoryDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.mapper.FoodMapper;
import com.example.project.model.Food;
import com.example.project.model.Inventory;
import com.example.project.service.FoodService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodControllerTest {

    @Mock
    private FoodService foodService;

    @Spy
    private FoodMapper foodMapper;

    @InjectMocks
    private FoodController foodController;

    private Food expectedFood;
    private FoodDto expectedDto;


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

        expectedDto = FoodDto.builder()
                .id(1L)
                .animal("pisica")
                .brand("un brand")
                .type("uscata")
                .price(12.123F)
                .quantityPerUnit(12L)
                .inventoryDto(InventoryDto.builder().id(1L).build())
                .build();
    }

    @Test
    @DisplayName("get all food - happy flow")
    public void test_getAll_happyFlow() {
        List<Food> foodList = new ArrayList<>();
        foodList.add(expectedFood);

        when(foodService.findAll()).thenReturn(foodList);
        ResponseEntity<List<FoodDto>> result = foodController.getAll();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(foodMapper.toDto(foodList));

        verify(foodService).findAll();
        verify(foodMapper, times(2)).toDto(foodList);
    }

    @Test
    @DisplayName("get food by id - happy flow")
    public void test_getFoodById_happyFlow() {
        Long id = expectedFood.getId();

        when(foodService.findById(id)).thenReturn(expectedFood);


        ResponseEntity<FoodDto> result = foodController.getFoodById(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(foodMapper.toDto(expectedFood));

        verify(foodService).findById(id);
        verify(foodMapper, times(2)).toDto(expectedFood);
        verify(foodMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("get food by id - food does not exist in database")
    public void test_getFoodById_throwsEntityNotFoundException_whenFoodNotFound() {
        Long id = expectedFood.getId();

        when(foodService.findById(id)).thenThrow(new EntityNotFoundException(String.format("The food item with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> foodController.getFoodById(id));

        assertThat(ex.getMessage()).isEqualTo(String.format("The food item with id = %s does not exist in the database.",id.toString()));

        verify(foodService).findById(id);
        verify(foodMapper, times(0)).toDto(expectedFood);
        verify(foodMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("add food - happy flow")
    public void test_createFood_happyFlow() {
        FoodDto foodDto = FoodDto.builder()
                .animal("pisica")
                .brand("un brand")
                .type("uscata")
                .price(12.123F)
                .quantityPerUnit(12L)
                .inventoryDto(InventoryDto.builder().id(1L).build())
                .build();
        when(foodService.create(foodMapper.toEntity(foodDto))).thenReturn(expectedFood);

        ResponseEntity<FoodDto> result = foodController.createFood(foodDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo(foodMapper.toDto(expectedFood));

        verify(foodService, times(1)).create(foodMapper.toEntity(foodDto));
        verify(foodMapper, times(2)).toDto(expectedFood);
        verify(foodMapper, times(3)).toEntity(foodDto);
    }

    @Test
    @DisplayName("update food - happy flow")
    public void test_updateFood_happyFlow() {
        Long id = expectedFood.getId();

        when(foodService.update(foodMapper.toEntity(expectedDto))).thenReturn(expectedFood);

        ResponseEntity<FoodDto> result = foodController.updateFood(id, expectedDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(foodMapper.toDto(expectedFood));

        verify(foodService).update(foodMapper.toEntity(expectedDto));
        verify(foodMapper, times(2)).toDto(expectedFood);
        verify(foodMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update food - food does not exist in database")
    public void test_updateFood_throwsEntityNotFoundException_whenFoodNotFound() {
        Long id = expectedFood.getId();
        when(foodService.update(foodMapper.toEntity(expectedDto))).thenThrow(new EntityNotFoundException(String.format("The food item with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> foodController.updateFood(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo(String.format("The food item with id = %s does not exist in the database.",id.toString()));

        verify(foodService).update(foodMapper.toEntity(expectedDto));
        verify(foodMapper, times(0)).toDto(expectedFood);
        verify(foodMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update food - id from path variable and response variable do not match")
    public void test_updateFood_throwsBadRequestException_whenIdFromPathVariableAndResponseVariableDontMatch() {
        Long id = expectedFood.getId();
        expectedDto.setId(id+1);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () -> foodController.updateFood(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo("The path variable does not match the request body id");

        verify(foodService, times(0)).update(expectedFood);

        verify(foodMapper, times(0)).toDto(expectedFood);
        verify(foodMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("delete food - happy flow")
    public void test_deleteFood_happyFlow() {
        Long id = expectedFood.getId();

        doNothing().when(foodService).deleteById(id);

        ResponseEntity<Void> result = foodController.deleteFood(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        verify(foodService).deleteById(id);
        verify(foodMapper, times(0)).toDto(expectedFood);
        verify(foodMapper, times(0)).toEntity(expectedDto);
    }
}