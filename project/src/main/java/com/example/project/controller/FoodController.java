package com.example.project.controller;

import com.example.project.dto.FoodDto;
import com.example.project.exception.BadRequestException;
import com.example.project.mapper.FoodMapper;
import com.example.project.model.Food;
import com.example.project.service.FoodService;
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
@RequestMapping("/food")
public class FoodController {
    private final FoodService foodService;
    private final FoodMapper foodMapper;

    public FoodController(FoodService foodService, FoodMapper foodMapper) {
        this.foodService = foodService;
        this.foodMapper = foodMapper;
    }
    @GetMapping
    @Operation(operationId = "Get all food", summary = "Get all food from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The food was successfully retrieved from the database",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FoodDto[].class))})
    })
    public ResponseEntity<List<FoodDto>> getAll() {
        List<Food> response = foodService.findAll();
        return  new ResponseEntity<>(foodMapper.toDto(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "Get a food item by id", summary = "Get a food item from the database by food id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The food item was successfully retrieved from the database"),
            @ApiResponse(responseCode = "404", description = "The food item was not found in the database")
    })
    public ResponseEntity<FoodDto> getFoodById(@PathVariable Long id) {
        Food response = foodService.findById(id);
        return new ResponseEntity<>(foodMapper.toDto(response), HttpStatus.OK);
    }

    @PostMapping
    @Operation(operationId = "Create food", summary = "Add a food item to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The food item was successfully added into the database"),
            @ApiResponse(responseCode = "400", description = "Validation error on the received request")
    })
    public ResponseEntity<FoodDto> createFood(@RequestBody @Valid FoodDto food) {
        Food response = foodService.create(foodMapper.toEntity(food));
        return new ResponseEntity<>(foodMapper.toDto(response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "Update a food item", summary = "Update a food item in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The food item was successfully updated into the database"),
            @ApiResponse(responseCode = "400", description = "The path variable does not match the request body id OR validation failed") ,
            @ApiResponse(responseCode = "404", description = "The food item was not found in the database")
    })
    public ResponseEntity<FoodDto> updateFood(@PathVariable Long id, @RequestBody @Valid FoodDto food) {
        if (id != food.getId()) {
            throw new BadRequestException("The path variable does not match the request body id");
        }
        Food response = foodService.update(foodMapper.toEntity(food));
        return new ResponseEntity<>(foodMapper.toDto(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "Delete food", summary = "Delete a food item from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The food item was successfully deleted from the database and NO_CONTENT was returned"),
            @ApiResponse(responseCode = "404", description = "The food item was not found in the database")
    })
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
