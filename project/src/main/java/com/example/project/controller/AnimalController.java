package com.example.project.controller;

import com.example.project.dto.AnimalDto;
import com.example.project.exception.BadRequestException;
import com.example.project.mapper.AnimalMapper;
import com.example.project.model.Animal;
import com.example.project.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/animals")
//@Api(value = "/animals", tags = "All animals existing in  the database")
public class AnimalController {
    private final AnimalService animalService;
    private final AnimalMapper animalMapper;

    public AnimalController(AnimalService animalService, AnimalMapper animalMapper) {
        this.animalService = animalService;
        this.animalMapper = animalMapper;
    }

    @GetMapping
    @Operation(operationId = "Get all animals", summary = "Get all animals from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The animals were successfully retrieved from the database",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto[].class))})
    })
    public ResponseEntity<List<AnimalDto>> getAll() {
        List<Animal> response = animalService.findAll();
        return  new ResponseEntity<>(animalMapper.toDto(response), HttpStatus.OK);
    }

    @GetMapping("/owner")
    @Operation(operationId = "Get animals by owner", summary = "Get all animals from the database filtered by owner id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The animals were successfully retrieved from the database",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto[].class))})
    })
    public ResponseEntity<List<AnimalDto>> getByOwner(@RequestParam Long id) {
            List<Animal> response = animalService.findByClient(id);
            return  new ResponseEntity<>(animalMapper.toDto(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "Get an animal by id", summary = "Get an animal from the database by animal id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The animal was successfully retrieved from the database"),
            @ApiResponse(responseCode = "404", description = "The animal was not found in the database")
    })
    public ResponseEntity<AnimalDto> getAnimalById(@PathVariable Long id) {
        Animal response = animalService.findById(id);
        return new ResponseEntity<>(animalMapper.toDto(response), HttpStatus.OK);
    }

    @PostMapping
    @Operation(operationId = "Create an animal", summary = "Add an animal to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The animal was successfully added into the database"),
            @ApiResponse(responseCode = "400", description = "Validation error on the received request")
    })
    public ResponseEntity<AnimalDto> createAnimal(@RequestBody @Valid AnimalDto animal) {
        Animal response = animalService.create(animalMapper.toEntity(animal));
        return new ResponseEntity<>(animalMapper.toDto(response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "Update an animal", summary = "Update an animal in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The animal was successfully updated into the database"),
            @ApiResponse(responseCode = "400", description = "The path variable does not match the request body id OR validation failed") ,
            @ApiResponse(responseCode = "404", description = "The animal was not found in the database")
    })
    public ResponseEntity<AnimalDto> updateAnimal(@PathVariable Long id, @RequestBody @Valid AnimalDto animal) {
        if (id != animal.getId()) {
            throw new BadRequestException("The path variable does not match the request body id");
        }
        Animal response = animalService.update(animalMapper.toEntity(animal));
        return new ResponseEntity<>(animalMapper.toDto(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "Delete an animal", summary = "Delete an animal from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The animal was successfully deleted from the database and NO_CONTENT was returned"),
            @ApiResponse(responseCode = "404", description = "The animal was not found in the database")
    })
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        animalService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
