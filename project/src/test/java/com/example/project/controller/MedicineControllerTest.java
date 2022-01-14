package com.example.project.controller;

import com.example.project.dto.InventoryDto;
import com.example.project.dto.MedicineDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.mapper.MedicineMapper;
import com.example.project.model.Inventory;
import com.example.project.model.Medicine;
import com.example.project.service.MedicineService;
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
class MedicineControllerTest {

    @Mock
    private MedicineService medicineService;

    @Spy
    private MedicineMapper medicineMapper;

   @InjectMocks
   private MedicineController medicineController;

   private Medicine expectedMedicine;
   private MedicineDto expectedDto;

    @BeforeEach
    void setUp() {
        expectedMedicine = Medicine.builder()
                .id(1L)
                .animal("pisica")
                .price(12.55F)
                .purpose("durere de burtica")
                .inventory(Inventory.builder().id(1L).build())
                .build();
        expectedDto = MedicineDto.builder()
                .id(1L)
                .animal("pisica")
                .price(12.55F)
                .purpose("durere de burtica")
                .inventoryDto(InventoryDto.builder().id(1L).build())
                .build();
    }

    @Test
    @DisplayName("get all medicine - happy flow")
    public void test_getAll_happyFlow() {
        List<Medicine> medicineList = new ArrayList<>();
        medicineList.add(expectedMedicine);

        when(medicineService.findAll()).thenReturn(medicineList);
        ResponseEntity<List<MedicineDto>> result = medicineController.getAll();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(medicineMapper.toDto(medicineList));

        verify(medicineService).findAll();
        verify(medicineMapper, times(2)).toDto(medicineList);
    }

    @Test
    @DisplayName("get medicine by id - happy flow")
    void test_getMedicineById_happyFlow() {
        Long id = expectedMedicine.getId();

        when(medicineService.findById(id)).thenReturn(expectedMedicine);


        ResponseEntity<MedicineDto> result = medicineController.getMedicinelById(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(medicineMapper.toDto(expectedMedicine));

        verify(medicineService).findById(id);
        verify(medicineMapper, times(2)).toDto(expectedMedicine);
        verify(medicineMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("get medicine by id - medicine does not exist in database")
    public void test_getMedicineById_throwsEntityNotFoundException_whenMedicineNotFound() {
        Long id = expectedMedicine.getId();

        when(medicineService.findById(id)).thenThrow(new EntityNotFoundException(String.format("The medicine with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> medicineController.getMedicinelById(id));

        assertThat(ex.getMessage()).isEqualTo(String.format("The medicine with id = %s does not exist in the database.",id.toString()));

        verify(medicineService).findById(id);
        verify(medicineMapper, times(0)).toDto(expectedMedicine);
        verify(medicineMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("add a medicine - happy flow")
    public void createMedicine() {
        MedicineDto medicineDto = MedicineDto.builder()
                .animal("pisica")
                .price(12.55F)
                .purpose("durere de burtica")
                .inventoryDto(InventoryDto.builder().id(1L).build())
                .build();
        when(medicineService.create(medicineMapper.toEntity(medicineDto))).thenReturn(expectedMedicine);

        ResponseEntity<MedicineDto> result = medicineController.createMedicine(medicineDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo(medicineMapper.toDto(expectedMedicine));

        verify(medicineService, times(1)).create(medicineMapper.toEntity(medicineDto));
        verify(medicineMapper, times(2)).toDto(expectedMedicine);
        verify(medicineMapper, times(3)).toEntity(medicineDto);

    }

    @Test
    @DisplayName("update medicine - happy flow")
    public void test_updateMedicine_happyFlow() {
        Long id = expectedMedicine.getId();

        when(medicineService.update(medicineMapper.toEntity(expectedDto))).thenReturn(expectedMedicine);

        ResponseEntity<MedicineDto> result = medicineController.updateMedicine(id, expectedDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(medicineMapper.toDto(expectedMedicine));

        verify(medicineService).update(medicineMapper.toEntity(expectedDto));
        verify(medicineMapper, times(2)).toDto(expectedMedicine);
        verify(medicineMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update medicine - medicine does not exist in database")
    public void test_updateMedicine_throwsEntityNotFoundException_whenMedicineNotFound() {
        Long id = expectedMedicine.getId();
        when(medicineService.update(medicineMapper.toEntity(expectedDto))).thenThrow(new EntityNotFoundException(String.format("The medicine with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> medicineController.updateMedicine(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo(String.format("The medicine with id = %s does not exist in the database.",id.toString()));

        verify(medicineService).update(medicineMapper.toEntity(expectedDto));
        verify(medicineMapper, times(0)).toDto(expectedMedicine);
        verify(medicineMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update medicine - id from path variable and response variable do not match")
    public void test_updateMedicine_throwsBadRequestException_whenIdFromPathVariableAndResponseVariableDontMatch() {
        Long id = expectedMedicine.getId();
        expectedDto.setId(id+1);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () -> medicineController.updateMedicine(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo("The path variable does not match the request body id");

        verify(medicineService, times(0)).update(expectedMedicine);

        verify(medicineMapper, times(0)).toDto(expectedMedicine);
        verify(medicineMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("delete medicine - happy flow")
    public void test_deleteMedicine_happyFlow() {
        Long id = expectedMedicine.getId();

        doNothing().when(medicineService).deleteById(id);

        ResponseEntity<Void> result = medicineController.deleteMedicine(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        verify(medicineService).deleteById(id);
        verify(medicineMapper, times(0)).toDto(expectedMedicine);
        verify(medicineMapper, times(0)).toEntity(expectedDto);
    }
}