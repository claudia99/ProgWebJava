package com.example.project.controller;

import com.example.project.dto.InventoryDto;
import com.example.project.dto.ProductTypeDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.mapper.InventoryMapper;
import com.example.project.mapper.ProductTypeMapper;
import com.example.project.model.*;
import com.example.project.service.InventoryService;
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
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @Spy
    private InventoryMapper inventoryMapper;

    @Spy
    private ProductTypeMapper productTypeMapper;

    @InjectMocks
    private InventoryController inventoryController;

    private Inventory expectedInventory;
    private InventoryDto expectedDto;

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
        expectedDto = InventoryDto.builder()
                .id(1L)
                .availableQuantity(121L)
                .build();
    }

    @Test
    @DisplayName("get all inventories - happy flow")
    public void test_getAll_happyFlow() {
        List<Inventory> inventoryList = new ArrayList<>();
        inventoryList.add(expectedInventory);

        when(inventoryService.findAll()).thenReturn(inventoryList);
        ResponseEntity<List<InventoryDto>> result = inventoryController.getAll();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(inventoryMapper.toDto(inventoryList));

        verify(inventoryService).findAll();
        verify(inventoryMapper, times(2)).toDto(inventoryList);
    }


    @Test
    @DisplayName("get inventory by id - happy flow")
    void test_getInventoryById_happyFlow() {
        Long id = expectedInventory.getId();

        when(inventoryService.findById(id)).thenReturn(expectedInventory);


        ResponseEntity<InventoryDto> result = inventoryController.getInventoryById(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(inventoryMapper.toDto(expectedInventory));

        verify(inventoryService).findById(id);
        verify(inventoryMapper, times(2)).toDto(expectedInventory);
        verify(inventoryMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("get inventory by id - inventory does not exist in database")
    public void test_getMedicineById_throwsEntityNotFoundException_whenMedicineNotFound() {
        Long id = expectedInventory.getId();

        when(inventoryService.findById(id)).thenThrow(new EntityNotFoundException(String.format("The inventory with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> inventoryController.getInventoryById(id));

        assertThat(ex.getMessage()).isEqualTo(String.format("The inventory with id = %s does not exist in the database.",id.toString()));

        verify(inventoryService).findById(id);
        verify(inventoryMapper, times(0)).toDto(expectedInventory);
        verify(inventoryMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("get product for inventory id - happy flow")
    void test_getProductForInventoryId_happyFlow() {
        ProductType productType = ProductType.builder().id(1L).type("food").build();
        Long id = expectedInventory.getId();
        when(inventoryService.findProductForInventory(id)).thenReturn(productType);
        ResponseEntity<ProductTypeDto> result = inventoryController.getProductForInventoryId(id);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(productTypeMapper.toDto(productType));
        verify(inventoryService).findProductForInventory(id);
        verify(productTypeMapper, times(2)).toDto(productType);

    }

    @Test
    @DisplayName("update inventory - happy flow")
    public void test_updateInventory_happyFlow() {
        Long id = expectedInventory.getId();

        when(inventoryService.update(inventoryMapper.toEntity(expectedDto))).thenReturn(expectedInventory);

        ResponseEntity<InventoryDto> result = inventoryController.updateInventory(id, expectedDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(inventoryMapper.toDto(expectedInventory));

        verify(inventoryService).update(inventoryMapper.toEntity(expectedDto));
        verify(inventoryMapper, times(2)).toDto(expectedInventory);
        verify(inventoryMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update inventory - inventory does not exist in database")
    public void test_updateMedicine_throwsEntityNotFoundException_whenMedicineNotFound() {
        Long id = expectedInventory.getId();
        when(inventoryService.update(inventoryMapper.toEntity(expectedDto))).thenThrow(new EntityNotFoundException(String.format("The inventory with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> inventoryController.updateInventory(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo(String.format("The inventory with id = %s does not exist in the database.",id.toString()));

        verify(inventoryService).update(inventoryMapper.toEntity(expectedDto));
        verify(inventoryMapper, times(0)).toDto(expectedInventory);
        verify(inventoryMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update inventory - id from path variable and response variable do not match")
    public void test_updateInventory_throwsBadRequestException_whenIdFromPathVariableAndResponseVariableDontMatch() {
        Long id = expectedInventory.getId();
        expectedDto.setId(id+1);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () -> inventoryController.updateInventory(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo("The path variable does not match the request body id");

        verify(inventoryService, times(0)).update(expectedInventory);

        verify(inventoryMapper, times(0)).toDto(expectedInventory);
        verify(inventoryMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("delete inventory - happy flow")
    public void test_deleteInventory_happyFlow() {
        Long id = expectedInventory.getId();

        doNothing().when(inventoryService).deleteById(id);

        ResponseEntity<Void> result = inventoryController.deleteInventory(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        verify(inventoryService).deleteById(id);
        verify(inventoryMapper, times(0)).toDto(expectedInventory);
        verify(inventoryMapper, times(0)).toEntity(expectedDto);
    }
}