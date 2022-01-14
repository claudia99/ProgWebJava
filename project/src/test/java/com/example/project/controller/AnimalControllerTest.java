package com.example.project.controller;

import com.example.project.dto.AnimalDto;
import com.example.project.dto.ClientDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.mapper.AnimalMapper;
import com.example.project.model.Animal;
import com.example.project.model.Client;
import com.example.project.service.AnimalService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalControllerTest {

    @Mock
    private AnimalService animalService;

    @Spy
    private AnimalMapper animalMapper;

    @InjectMocks
    private AnimalController animalController;

    private Animal expectedAnimal;
    private AnimalDto expectedDto;

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
        expectedDto = AnimalDto.builder()
                .id(1L)
                .name("Piscot")
                .species("pisica")
                .breed("persana")
                .birth_date(LocalDate.now())
                .ownerDto(ClientDto.builder().id(1L).build())
                .build();
    }

    @Test
    @DisplayName("get all animals - happy flow")
    public void test_getAll_happyFlow() {
        List<Animal> animalList = new ArrayList<>();
        animalList.add(expectedAnimal);

        when(animalService.findAll()).thenReturn(animalList);
        ResponseEntity<List<AnimalDto>> result = animalController.getAll();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(animalMapper.toDto(animalList));

        verify(animalService).findAll();
        verify(animalMapper, times(2)).toDto(animalList);
    }

    @Test
    @DisplayName("get all animals by owner id - happy flow")
    void test_getByOwner_happyFlow() {
        List<Animal> animalList = new ArrayList<>();
        animalList.add(expectedAnimal);

        when(animalService.findByClient(expectedAnimal.getOwner().getId())).thenReturn(animalList);

        ResponseEntity<List<AnimalDto>> result = animalController.getByOwner(expectedAnimal.getOwner().getId());

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(animalMapper.toDto(animalList));

        verify(animalService).findByClient(expectedAnimal.getOwner().getId());
        verify(animalMapper, times(2)).toDto(animalList);
    }

    @Test
    @DisplayName("get an animal by id - happy flow")
    void getAnimalById() {
        Long id = expectedAnimal.getId();

        when(animalService.findById(id)).thenReturn(expectedAnimal);

        ResponseEntity<AnimalDto> result = animalController.getAnimalById(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(animalMapper.toDto(expectedAnimal));

        verify(animalService).findById(id);
        verify(animalMapper, times(2)).toDto(expectedAnimal);
        verify(animalMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("get animal by id - animal does not exist in database")
    public void test_getAnimalById_throwsEntityNotFoundException_whenAnimalNotFound() {
        Long id = expectedAnimal.getId();

        when(animalService.findById(id)).thenThrow(new EntityNotFoundException(String.format("The animal with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> animalController.getAnimalById(id));

        assertThat(ex.getMessage()).isEqualTo(String.format("The animal with id = %s does not exist in the database.",id.toString()));

        verify(animalService).findById(id);
        verify(animalMapper, times(0)).toDto(expectedAnimal);
        verify(animalMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("add an animal - happy flow")
    void createAnimal() {
        AnimalDto animalDto = AnimalDto.builder()
                .name("Piscot")
                .species("pisica")
                .breed("persana")
                .birth_date(LocalDate.now())
                .ownerDto(ClientDto.builder().id(1L).build())
                .build();
        when(animalService.create(animalMapper.toEntity(animalDto))).thenReturn(expectedAnimal);

        ResponseEntity<AnimalDto> result = animalController.createAnimal(animalDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo(animalMapper.toDto(expectedAnimal));

        verify(animalService, times(1)).create(animalMapper.toEntity(animalDto));
        verify(animalMapper, times(2)).toDto(expectedAnimal);
        verify(animalMapper, times(3)).toEntity(animalDto);
    }

    @Test
    @DisplayName("update an animal - happy flow")
    void test_updateAnimal_happyFlow() {
        Long id = expectedAnimal.getId();

        when(animalService.update(animalMapper.toEntity(expectedDto))).thenReturn(expectedAnimal);

        ResponseEntity<AnimalDto> result = animalController.updateAnimal(id, expectedDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(animalMapper.toDto(expectedAnimal));

        verify(animalService).update(animalMapper.toEntity(expectedDto));
        verify(animalMapper, times(2)).toDto(expectedAnimal);
        verify(animalMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update an animal - animal does not exist in database")
    void test_updateAnimal_throwsEntityNotFoundException_whenAnimalNotFound() {
        Long id = expectedAnimal.getId();
        when(animalService.update(animalMapper.toEntity(expectedDto))).thenThrow(new EntityNotFoundException(String.format("The animal with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> animalController.updateAnimal(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo(String.format("The animal with id = %s does not exist in the database.",id.toString()));

        verify(animalService).update(animalMapper.toEntity(expectedDto));
        verify(animalMapper, times(0)).toDto(expectedAnimal);
        verify(animalMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update an animal - id from path variable and response variable do not match")
    void test_updateAnimal_throwsBadRequestException_whenIdFromPathVariableAndResponseVariableDontMatch() {
        Long id = expectedAnimal.getId();
        expectedDto.setId(id+1);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () -> animalController.updateAnimal(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo("The path variable does not match the request body id");

        verify(animalService, times(0)).update(expectedAnimal);

        verify(animalMapper, times(0)).toDto(expectedAnimal);
        verify(animalMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("delete an animal - happy flow")
    void test_deleteAnimal_happyFlow() {
        Long id = expectedAnimal.getId();

        doNothing().when(animalService).deleteById(id);

        ResponseEntity<Void> result = animalController.deleteAnimal(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        verify(animalService).deleteById(id);
        verify(animalMapper, times(0)).toDto(expectedAnimal);
        verify(animalMapper, times(0)).toEntity(expectedDto);
    }

}