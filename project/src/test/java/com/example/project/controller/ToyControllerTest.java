package com.example.project.controller;

import com.example.project.dto.InventoryDto;
import com.example.project.dto.ToyDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.mapper.ToyMapper;
import com.example.project.model.Inventory;
import com.example.project.model.Toy;
import com.example.project.service.ToyService;
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
class ToyControllerTest {

    @Mock
    private ToyService toyService;

    @Spy
    private ToyMapper toyMapper;

    @InjectMocks
    private ToyController toyController;

    private Toy expectedToy;
    private ToyDto expectedDto;

    @BeforeEach
    void setUp() {
        expectedToy = Toy.builder()
                .id(1L)
                .animal("pisica")
                .price(23.5F)
                .brand("Un brand")
                .inventory(Inventory.builder().id(2L).build())
                .build();

        expectedDto = ToyDto.builder()
                .id(1L)
                .animal("pisica")
                .price(23.5F)
                .brand("Un brand")
                .inventoryDto(InventoryDto.builder().id(2L).build())
                .build();
    }

    @Test
    @DisplayName("get all toys - happy flow")
    public void test_getAll_happyFlow() {
        List<Toy> toyList = new ArrayList<>();
        toyList.add(expectedToy);

        when(toyService.findAll()).thenReturn(toyList);
        ResponseEntity<List<ToyDto>> result = toyController.getAll();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(toyMapper.toDto(toyList));

        verify(toyService).findAll();
        verify(toyMapper, times(2)).toDto(toyList);
    }

    @Test
    @DisplayName("get a toy by id - happy flow")
    public void test_getToyById_happyFlow() {
        Long id = expectedToy.getId();

        when(toyService.findById(id)).thenReturn(expectedToy);


        ResponseEntity<ToyDto> result = toyController.getToyById(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(toyMapper.toDto(expectedToy));

        verify(toyService).findById(id);
        verify(toyMapper, times(2)).toDto(expectedToy);
        verify(toyMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("get a toy by id - toy does not exist in database")
    public void test_getToyById_throwsEntityNotFoundException_whenToyNotFound() {
        Long id = expectedToy.getId();

        when(toyService.findById(id)).thenThrow(new EntityNotFoundException(String.format("The toy with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> toyController.getToyById(id));

        assertThat(ex.getMessage()).isEqualTo(String.format("The toy with id = %s does not exist in the database.",id.toString()));

        verify(toyService).findById(id);
        verify(toyMapper, times(0)).toDto(expectedToy);
        verify(toyMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("add a toy - happy flow")
    public void test_createToy_happyFlow() {
        ToyDto toyDto = ToyDto.builder()
                .id(1L)
                .animal("pisica")
                .price(23.5F)
                .brand("Un brand")
                .inventoryDto(InventoryDto.builder().id(2L).build())
                .build();
        when(toyService.create(toyMapper.toEntity(toyDto))).thenReturn(expectedToy);

        ResponseEntity<ToyDto> result = toyController.createToy(toyDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo(toyMapper.toDto(expectedToy));

        verify(toyService, times(1)).create(toyMapper.toEntity(toyDto));
        verify(toyMapper, times(2)).toDto(expectedToy);
        verify(toyMapper, times(3)).toEntity(toyDto);
    }

    @Test
    @DisplayName("update a toy - happy flow")
    public void test_updateToy_happyFlow() {
        Long id = expectedToy.getId();

        when(toyService.update(toyMapper.toEntity(expectedDto))).thenReturn(expectedToy);

        ResponseEntity<ToyDto> result = toyController.updateToy(id, expectedDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(toyMapper.toDto(expectedToy));

        verify(toyService).update(toyMapper.toEntity(expectedDto));
        verify(toyMapper, times(2)).toDto(expectedToy);
        verify(toyMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update a toy - toy does not exist in database")
    public void test_updateToy_throwsEntityNotFoundException_whenToyNotFound() {
        Long id = expectedToy.getId();
        when(toyService.update(toyMapper.toEntity(expectedDto))).thenThrow(new EntityNotFoundException(String.format("The toy with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> toyController.updateToy(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo(String.format("The toy with id = %s does not exist in the database.",id.toString()));

        verify(toyService).update(toyMapper.toEntity(expectedDto));
        verify(toyMapper, times(0)).toDto(expectedToy);
        verify(toyMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update a toy - id from path variable and response variable do not match")
    public void test_updateToy_throwsBadRequestException_whenIdFromPathVariableAndResponseVariableDontMatch() {
        Long id = expectedToy.getId();
        expectedDto.setId(id+1);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () -> toyController.updateToy(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo("The path variable does not match the request body id");

        verify(toyService, times(0)).update(expectedToy);

        verify(toyMapper, times(0)).toDto(expectedToy);
        verify(toyMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("delete a toy - happy flow")
    public void test_deleteToy_happyFlow() {
        Long id = expectedToy.getId();

        doNothing().when(toyService).deleteById(id);

        ResponseEntity<Void> result = toyController.deleteToy(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        verify(toyService).deleteById(id);
        verify(toyMapper, times(0)).toDto(expectedToy);
        verify(toyMapper, times(0)).toEntity(expectedDto);
    }
}