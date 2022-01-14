package com.example.project.controller;

import com.example.project.dto.ToyDto;
import com.example.project.exception.BadRequestException;
import com.example.project.mapper.ToyMapper;
import com.example.project.model.Toy;
import com.example.project.service.ToyService;
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
@RequestMapping("/toys")
public class ToyController {
    private final ToyService toyService;
    private final ToyMapper toyMapper;

    public ToyController(ToyService toyService, ToyMapper toyMapper) {
        this.toyService = toyService;
        this.toyMapper = toyMapper;
    }

    @GetMapping
    @Operation(operationId = "Get all toys", summary = "Get all toys from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The toys were successfully retrieved from the database",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ToyDto[].class))})
    })
    public ResponseEntity<List<ToyDto>> getAll() {
        List<Toy> response = toyService.findAll();
        return  new ResponseEntity<>(toyMapper.toDto(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "Get a toy by id", summary = "Get a toy from the database by toy id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The toy was successfully retrieved from the database"),
            @ApiResponse(responseCode = "404", description = "The toy was not found in the database")
    })
    public ResponseEntity<ToyDto> getToyById(@PathVariable Long id) {
        Toy response = toyService.findById(id);
        return new ResponseEntity<>(toyMapper.toDto(response), HttpStatus.OK);
    }

    @PostMapping
    @Operation(operationId = "Create a toy", summary = "Add a toy to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The toy was successfully added into the database"),
            @ApiResponse(responseCode = "400", description = "Validation error on the received request")
    })
    public ResponseEntity<ToyDto> createToy(@RequestBody @Valid ToyDto toy) {
        Toy response = toyService.create(toyMapper.toEntity(toy));
        return new ResponseEntity<>(toyMapper.toDto(response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "Update a toy", summary = "Update a toy in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The toy was successfully updated into the database"),
            @ApiResponse(responseCode = "400", description = "The path variable does not match the request body id OR validation failed") ,
            @ApiResponse(responseCode = "404", description = "The toy was not found in the database")
    })
    public ResponseEntity<ToyDto> updateToy(@PathVariable Long id, @RequestBody @Valid ToyDto toy) {
        if (id != toy.getId()) {
            throw new BadRequestException("The path variable does not match the request body id");
        }
        Toy response = toyService.update(toyMapper.toEntity(toy));
        return new ResponseEntity<>(toyMapper.toDto(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "Delete a toy", summary = "Delete a toy from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The toy was successfully deleted from the database and NO_CONTENT was returned"),
            @ApiResponse(responseCode = "404", description = "The toy was not found in the database")
    })
    public ResponseEntity<Void> deleteToy(@PathVariable Long id) {
        toyService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
