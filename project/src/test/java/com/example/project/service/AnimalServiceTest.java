package com.example.project.service;

import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Animal;
import com.example.project.model.Client;
import com.example.project.repository.AnimalRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest{

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private AnimalService animalService;

    private Animal expectedAnimal;

    @BeforeEach
    void setUp() {
        expectedAnimal = Animal.builder()
                .id(1L)
                .name("Piscot")
                .species("pisica")
                .breed("persana")
                .birth_date(LocalDate.now())
                .owner(Client.builder().id(1L).build())
                .build();
    }

    @Test
    @DisplayName("find all animals - happy flow")
    public void test_findAll_happyFlow() {
        List<Animal> animalList = new ArrayList<>();
        animalList.add(expectedAnimal);

        when(animalRepository.findAll()).thenReturn(animalList);

        List<Animal> result = animalService.findAll();

        assertEquals(animalList.size(), result.size());
        assertEquals(expectedAnimal.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedAnimal.getSpecies(), result.stream().findFirst().get().getSpecies());
        assertEquals(expectedAnimal.getBreed(), result.stream().findFirst().get().getBreed());
        assertEquals(expectedAnimal.getName(), result.stream().findFirst().get().getName());
        assertEquals(expectedAnimal.getBirth_date(), result.stream().findFirst().get().getBirth_date());
        assertEquals(expectedAnimal.getOwner(), result.stream().findFirst().get().getOwner());

        verify(animalRepository).findAll();
    }

    @Test
    @DisplayName("find animal by owner id - happy flow")
    public void test_findByClient_happyFlow() {
        Long id = expectedAnimal.getOwner().getId();
        List<Animal> animalList = new ArrayList<>();
        animalList.add(expectedAnimal);

        when(animalRepository.findByOwnerId(id)).thenReturn(animalList);
        List<Animal> result = animalService.findByClient(id);

        assertEquals(animalList.size(), result.size());
        assertEquals(expectedAnimal.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedAnimal.getSpecies(), result.stream().findFirst().get().getSpecies());
        assertEquals(expectedAnimal.getBreed(), result.stream().findFirst().get().getBreed());
        assertEquals(expectedAnimal.getName(), result.stream().findFirst().get().getName());
        assertEquals(expectedAnimal.getBirth_date(), result.stream().findFirst().get().getBirth_date());
        assertEquals(expectedAnimal.getOwner(), result.stream().findFirst().get().getOwner());

        verify(animalRepository).findByOwnerId(id);
    }

    @Test
    @DisplayName("find animal by id - happy flow")
    public void test_findById_happyFlow() {
        Long id = expectedAnimal.getId();
        //pregatire
        when(animalRepository.findById(id)).thenReturn(Optional.of(expectedAnimal));
        //apelare
        Animal result = animalService.findById(id);
        //verificare
        assertEquals(expectedAnimal.getId(),result.getId());
        assertEquals(expectedAnimal.getName(),result.getName());
        assertEquals(expectedAnimal.getBreed(),result.getBreed());
        assertEquals(expectedAnimal.getSpecies(),result.getSpecies());
        assertEquals(expectedAnimal.getBirth_date(),result.getBirth_date());
        assertEquals(expectedAnimal.getOwner(),result.getOwner());

        verify(animalRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("animal does not exist in database")
    public void test_findById_throwsEntityNotFoundException_whenAnimalNotFound() {
        Long id = expectedAnimal.getId();

        when(animalRepository.findById(id)).thenThrow(new EntityNotFoundException(String.format("The animal with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> animalService.findById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The animal with id = %s does not exist in the database.",id.toString()));

        verify(animalRepository).findById(id);
    }

    @Test
    @DisplayName("delete animal by id - happy flow")
    public void test_deleteById_happyFlow() {
        Long id = expectedAnimal.getId();

        when(animalRepository.existsById(id)).thenReturn(true);
        doNothing().when(animalRepository).deleteById(id);

        animalService.deleteById(id);

        verify(animalRepository).existsById(id);
        verify(animalRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete animal by id -  animal does not exist in the database")
    public void test_deleteById_throwsEntityNotFoundException_whenAnimalNotFound() {
        Long id = expectedAnimal.getId();

        when(animalRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                animalService.deleteById(id)
        );
        assertThat(ex.getMessage()).isEqualTo(String.format("The animal with id = %s does not exist in the database.",id.toString()));

        verify(animalRepository).existsById(id);
        verify(animalRepository, times(0)).deleteById(id);
    }

    @Test
    @DisplayName("create an animal - happy flow")
    public void test_create_happyFlow() {
        Animal animal = Animal.builder()
                .name("Piscot")
                .species("pisica")
                .breed("persana")
                .birth_date(LocalDate.now())
                .owner(Client.builder().id(1L).build())
                .build();

        when(clientService.existById(animal.getOwner().getId())).thenReturn(true);
        when(animalRepository.save(animal)).thenReturn(expectedAnimal);

        Animal result = animalService.create(animal);

        assertEquals(expectedAnimal.getId(),result.getId());
        assertEquals(expectedAnimal.getName(),result.getName());
        assertEquals(expectedAnimal.getBreed(),result.getBreed());
        assertEquals(expectedAnimal.getSpecies(),result.getSpecies());
        assertEquals(expectedAnimal.getBirth_date(),result.getBirth_date());
        assertEquals(expectedAnimal.getOwner(),result.getOwner());

        verify(animalRepository, times(1)).save(animal);
        verify(clientService).existById(animal.getOwner().getId());
    }

    @Test
    @DisplayName("create an animal - its client does not exist in the database")
    public void test_create_throwsBadRequestException_whenAnimalOwnerNotFound()  {
        Animal animal = Animal.builder()
                .name("Piscot")
                .species("pisica")
                .breed("persana")
                .birth_date(LocalDate.now())
                .owner(Client.builder().id(1L).build())
                .build();

        when(clientService.existById(any())).thenReturn(false);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () ->
                animalService.create(animal)
        );

        assertThat(ex.getMessage()).isEqualTo("You have to create the client before adding its animal!");

        verify(clientService).existById(any());
        verify(animalRepository, times(0)).save(animal);
    }

    @Test
    @DisplayName("update an animal - happy flow")
    public void test_update_happyFlow() {
        Animal animal = expectedAnimal;
        Long id = expectedAnimal.getId();

        when(animalRepository.existsById(id)).thenReturn(true);
        when(animalRepository.save(animal)).thenReturn(expectedAnimal);

        Animal result = animalService.update(animal);

        assertEquals(expectedAnimal.getId(),result.getId());
        assertEquals(expectedAnimal.getName(),result.getName());
        assertEquals(expectedAnimal.getBreed(),result.getBreed());
        assertEquals(expectedAnimal.getSpecies(),result.getSpecies());
        assertEquals(expectedAnimal.getBirth_date(),result.getBirth_date());
        assertEquals(expectedAnimal.getOwner(),result.getOwner());

        verify(animalRepository).existsById(id);
        verify(animalRepository).save(animal);
    }

    @Test
    @DisplayName("update an animal - animal does not exist in the database")
    public void test_update_throwsEntityNotFoundException_whenAnimalNotFound()  {
        Long id = expectedAnimal.getId();

        when(animalRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                animalService.update(expectedAnimal)
        );

        assertThat(ex.getMessage()).isEqualTo(String.format("The animal with id = %s does not exist in the database.",id.toString()));

        verify(animalRepository).existsById(id);
        verify(animalRepository, times(0)).save(expectedAnimal);
    }

}