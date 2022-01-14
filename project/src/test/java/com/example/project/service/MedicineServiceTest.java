package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Inventory;
import com.example.project.model.Medicine;
import com.example.project.repository.MedicineRepository;
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
class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineService medicineService;

    private Medicine expectedMedicine;

    @BeforeEach
    void setUp() {
        expectedMedicine = Medicine.builder()
                .id(1L)
                .animal("pisica")
                .price(12.55F)
                .purpose("durere de burtica")
                .inventory(Inventory.builder().id(1L).build())
                .build();
    }

    @Test
    @DisplayName("find all medicine - happy flow")
    public void test_findAll_happyFlow() {
        List<Medicine> medicineList = new ArrayList<>();
        medicineList.add(expectedMedicine);

        when(medicineRepository.findAll()).thenReturn(medicineList);

        List<Medicine> result = medicineService.findAll();

        assertEquals(medicineList.size(), result.size());
        assertEquals(expectedMedicine.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedMedicine.getAnimal(), result.stream().findFirst().get().getAnimal());
        assertEquals(expectedMedicine.getPrice(), result.stream().findFirst().get().getPrice());
        assertEquals(expectedMedicine.getPurpose(), result.stream().findFirst().get().getPurpose());
        assertEquals(expectedMedicine.getInventory(), result.stream().findFirst().get().getInventory());

        verify(medicineRepository).findAll();
    }

    @Test
    @DisplayName("find medicine by id - happy flow")
    public void test_findById_happyFlow() {
        Long id = expectedMedicine.getId();

        when(medicineRepository.findById(id)).thenReturn(Optional.of(expectedMedicine));

        Medicine result = medicineService.findById(id);

        assertEquals(expectedMedicine.getId(), result.getId());
        assertEquals(expectedMedicine.getPrice(), result.getPrice());
        assertEquals(expectedMedicine.getPurpose(), result.getPurpose());
        assertEquals(expectedMedicine.getAnimal(), result.getAnimal());
        assertEquals(expectedMedicine.getInventory(), result.getInventory());

        verify(medicineRepository).findById(id);
    }

    @Test
    @DisplayName("find medicine by id - medicine does not exist in database")
    public void test_findById_throwsEntityNotFoundException_whenMedicineNotFound() {
        Long id = expectedMedicine.getId();

        when(medicineRepository.findById(id)).thenThrow(new EntityNotFoundException(String.format("The medicine with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> medicineService.findById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The medicine with id = %s does not exist in the database.",id.toString()));

        verify(medicineRepository).findById(id);
    }

    @Test
    @DisplayName("delete medicine by id - happy flow")
    public void test_deleteById_happyFlow() {
        Long id = expectedMedicine.getId();

        when(medicineRepository.existsById(id)).thenReturn(true);
        doNothing().when(medicineRepository).deleteById(id);

        medicineService.deleteById(id);

        verify(medicineRepository).existsById(id);
        verify(medicineRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete medicine by id - medicine does not exist in database")
    public void test_deleteById_throwsEntityNotFoundException_whenMedicineNotFound() {
        Long id = expectedMedicine.getId();

        when(medicineRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                medicineService.deleteById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The medicine item with id = %s does not exist in the database.",id.toString()));

        verify(medicineRepository).existsById(id);
        verify(medicineRepository, times(0)).deleteById(id);
    }


    @Test
    @DisplayName("create medicine - happy flow")
    void test_create_happyFlow() {
        Medicine medicine = Medicine.builder()
                .animal("pisica")
                .price(12.55F)
                .purpose("durere de burtica")
                .build();

        when(medicineRepository.save(medicine)).thenReturn(expectedMedicine);

        Medicine result = medicineService.create(medicine);

        assertEquals(expectedMedicine.getId(), result.getId());
        assertEquals(expectedMedicine.getPrice(), result.getPrice());
        assertEquals(expectedMedicine.getPurpose(), result.getPurpose());
        assertEquals(expectedMedicine.getAnimal(), result.getAnimal());
        assertEquals(expectedMedicine.getInventory(), result.getInventory());
        verify(medicineRepository).save(medicine);
    }

    @Test
    @DisplayName("update medicine - happy flow")
    void test_update_happyFlow() {
        Medicine medicine = expectedMedicine;
        Long id = expectedMedicine.getId();

        when(medicineRepository.existsById(id)).thenReturn(true);
        when(medicineRepository.save(medicine)).thenReturn(expectedMedicine);

        Medicine result = medicineService.update(medicine);

        assertEquals(expectedMedicine.getId(), result.getId());
        assertEquals(expectedMedicine.getAnimal(), result.getAnimal());
        assertEquals(expectedMedicine.getPurpose(), result.getPurpose());
        assertEquals(expectedMedicine.getPrice(), result.getPrice());

        verify(medicineRepository).existsById(id);
        verify(medicineRepository).save(medicine);
    }

    @Test
    @DisplayName("update medicine - medicine does not exist in database")
    public void test_update_throwsEntityNotFoundException_whenMedicineNotFound() {
        Long id = expectedMedicine.getId();

        when(medicineRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                medicineService.update(expectedMedicine));

        assertThat(ex.getMessage()).isEqualTo(String.format("The medicine item with id = %s does not exist in the database.",id.toString()));

        verify(medicineRepository).existsById(id);
        verify(medicineRepository, times(0)).save(expectedMedicine);
    }

    @Test
    @DisplayName("check if medicine exists in inventory - happy flow")
    void test_existsByInventoryId_happyFlow() {
        Long id = expectedMedicine.getInventory().getId();

        when(medicineRepository.existsByInventoryId(id)).thenReturn(true);

        Boolean result = medicineService.existsByInventoryId(id);

        assertEquals(true, result);

        verify(medicineRepository).existsByInventoryId(id);
    }

    @Test
    @DisplayName("get medicine id by inventory - happy flow")
    void test_findByInventoryId_happyFlow() {
        Long id = expectedMedicine.getInventory().getId();

        when(medicineRepository.findByInventoryId(id)).thenReturn(expectedMedicine);

        Long result = medicineService.findByInventoryId(id);

        assertEquals(expectedMedicine.getId(), result);

        verify(medicineRepository).findByInventoryId(id);
    }
}