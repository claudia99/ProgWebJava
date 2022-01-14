package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Inventory;
import com.example.project.model.Toy;
import com.example.project.repository.ToyRepository;
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
class ToyServiceTest {

    @Mock
    private ToyRepository toyRepository;

    @InjectMocks
    private ToyService toyService;

    private Toy expectedToy;


    @BeforeEach
    void setUp() {
        expectedToy = Toy.builder()
                .id(1L)
                .animal("pisica")
                .price(23.5F)
                .brand("Un brand")
                .inventory(Inventory.builder().id(2L).build())
                .build();
    }

    @Test
    @DisplayName("find all toys - happy flow")
    public void test_findAll_happyFlow() {
        List<Toy> toyList = new ArrayList<>();
        toyList.add(expectedToy);

        when(toyRepository.findAll()).thenReturn(toyList);

        List<Toy> result = toyService.findAll();

        assertEquals(toyList.size(), result.size());
        assertEquals(expectedToy.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedToy.getAnimal(), result.stream().findFirst().get().getAnimal());
        assertEquals(expectedToy.getBrand(), result.stream().findFirst().get().getBrand());
        assertEquals(expectedToy.getPrice(), result.stream().findFirst().get().getPrice());
        assertEquals(expectedToy.getInventory(), result.stream().findFirst().get().getInventory());

        verify(toyRepository).findAll();
    }

    @Test
    @DisplayName("find toy by id - happy flow")
    public void test_findById_happyFlow() {
        Long id = expectedToy.getId();

        when(toyRepository.findById(id)).thenReturn(Optional.of(expectedToy));

        Toy result = toyService.findById(id);

        assertEquals(expectedToy.getId(), result.getId());
        assertEquals(expectedToy.getPrice(), result.getPrice());
        assertEquals(expectedToy.getBrand(), result.getBrand());
        assertEquals(expectedToy.getAnimal(), result.getAnimal());

        verify(toyRepository).findById(id);
    }

    @Test
    @DisplayName("find toy by id - toy does not exist in database")
    public void test_findById_throwsEntityNotFoundException_whenToyNotFound() {
        Long id = expectedToy.getId();

        when(toyRepository.findById(id)).thenThrow(new EntityNotFoundException(String.format("The toy with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> toyService.findById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The toy with id = %s does not exist in the database.",id.toString()));

        verify(toyRepository).findById(id);
    }

    @Test
    @DisplayName("delete toy by id - happy flow")
    public void test_deleteById_happyFlow() {
        Long id = expectedToy.getId();

        when(toyRepository.existsById(id)).thenReturn(true);
        doNothing().when(toyRepository).deleteById(id);

        toyService.deleteById(id);

        verify(toyRepository).existsById(id);
        verify(toyRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete toy by id - toy does not exist in database")
    public void test_deleteById_throwsEntityNotFoundException_whenToyNotFound() {
        Long id = expectedToy.getId();

        when(toyRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                toyService.deleteById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The toy with id = %s does not exist in the database.",id.toString()));

        verify(toyRepository).existsById(id);
        verify(toyRepository, times(0)).deleteById(id);
    }

    @Test
    @DisplayName("create toy - happy flow")
    public void test_create_happyFlow() {
        Toy toy = Toy.builder()
                .animal("pisica")
                .price(23.5F)
                .brand("Un brand")
                .build();

        when(toyRepository.save(toy)).thenReturn(expectedToy);

        Toy result = toyService.create(toy);

        assertEquals(expectedToy.getId(), result.getId());
        assertEquals(expectedToy.getAnimal(), result.getAnimal());
        assertEquals(expectedToy.getBrand(), result.getBrand());
        assertEquals(expectedToy.getPrice(), result.getPrice());

        verify(toyRepository).save(toy);
    }

    @Test
    @DisplayName("update a toy - happy flow")
    public void test_update_happyFlow() {
        Toy toy = expectedToy;
        Long id = expectedToy.getId();

        when(toyRepository.existsById(id)).thenReturn(true);
        when(toyRepository.save(toy)).thenReturn(expectedToy);

        Toy result = toyService.update(toy);

        assertEquals(expectedToy.getId(), result.getId());
        assertEquals(expectedToy.getAnimal(), result.getAnimal());
        assertEquals(expectedToy.getBrand(), result.getBrand());
        assertEquals(expectedToy.getPrice(), result.getPrice());

        verify(toyRepository).existsById(id);
        verify(toyRepository).save(toy);
    }

    @Test
    @DisplayName("update a toy - toy does not exist in database")
    public void test_update_throwsEntityNotFoundException_whenToyNotFound() {
        Long id = expectedToy.getId();

        when(toyRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                toyService.update(expectedToy));

        assertThat(ex.getMessage()).isEqualTo(String.format("The animal with id = %s does not exist in the database.",id.toString()));

        verify(toyRepository).existsById(id);
        verify(toyRepository, times(0)).save(expectedToy);
    }

    @Test
    @DisplayName("check if toy exists in inventory - happy flow")
    void test_existsByInventoryId_happyFlow() {
        Long id = expectedToy.getInventory().getId();

        when(toyRepository.existsByInventoryId(id)).thenReturn(true);

        Boolean result = toyService.existsByInventoryId(id);

        assertEquals(true, result);

        verify(toyRepository).existsByInventoryId(id);
    }

    @Test
    @DisplayName("get toy id by inventory - happy flow")
    public void test_findByInventoryId_happyFlow() {
        Long id = expectedToy.getInventory().getId();
        when(toyRepository.findByInventoryId(id)).thenReturn(expectedToy);
        Long result = toyService.findByInventoryId(id);

        assertEquals(expectedToy.getId(), result);

        verify(toyRepository).findByInventoryId(id);
    }

}