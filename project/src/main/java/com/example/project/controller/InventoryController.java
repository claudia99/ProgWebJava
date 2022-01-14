package com.example.project.controller;

import com.example.project.dto.InventoryDto;
import com.example.project.dto.ProductTypeDto;
import com.example.project.exception.BadRequestException;
import com.example.project.mapper.InventoryMapper;
import com.example.project.mapper.ProductTypeMapper;
import com.example.project.model.Inventory;
import com.example.project.model.ProductType;
import com.example.project.service.InventoryService;
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
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;
    private final ProductTypeMapper productTypeMapper;

    public InventoryController(InventoryService inventoryService, InventoryMapper inventoryMapper, ProductTypeMapper productTypeMapper) {
        this.inventoryService = inventoryService;
        this.inventoryMapper = inventoryMapper;
        this.productTypeMapper = productTypeMapper;
    }

    @GetMapping
    @Operation(operationId = "Get all inventories", summary = "Get all inventories from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The inventories were successfully retrieved from the database",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InventoryDto[].class))})
    })
    public ResponseEntity<List<InventoryDto>> getAll() {
        List<Inventory> response = inventoryService.findAll();
        return  new ResponseEntity<>(inventoryMapper.toDto(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "Get an inventory by id", summary = "Get an inventory from the database by inventory id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The inventory was successfully retrieved from the database"),
            @ApiResponse(responseCode = "404", description = "The inventory was not found in the database")
    })
    public ResponseEntity<InventoryDto> getInventoryById(@PathVariable Long id) {
        Inventory response = inventoryService.findById(id);
        return new ResponseEntity<>(inventoryMapper.toDto(response), HttpStatus.OK);
    }

    /*@PostMapping
    public ResponseEntity<InventoryDto> createInventory(@RequestBody @Valid InventoryDto inventory) {
        Inventory response = inventoryService.create(inventoryMapper.toEntity(inventory));
        return new ResponseEntity<>(inventoryMapper.toDto(response), HttpStatus.CREATED);
    }*/

    @GetMapping("/{id}/product")
    public ResponseEntity<ProductTypeDto> getProductForInventoryId(@PathVariable Long id) {
        ProductType result = inventoryService.findProductForInventory(id);
        return new ResponseEntity<>(productTypeMapper.toDto(result), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "Update an inventory", summary = "Update an inventory in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The inventory was successfully updated into the database"),
            @ApiResponse(responseCode = "400", description = "The path variable does not match the request body id OR validation failed") ,
            @ApiResponse(responseCode = "404", description = "The inventory was not found in the database")
    })
    public ResponseEntity<InventoryDto> updateInventory(@PathVariable Long id, @RequestBody @Valid InventoryDto inventory) {
        if (id != inventory.getId()) {
            throw new BadRequestException("The path variable does not match the request body id");
        }
        Inventory response = inventoryService.update(inventoryMapper.toEntity(inventory));
        return new ResponseEntity<>(inventoryMapper.toDto(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "Delete an inventory", summary = "Delete an inventory from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The inventory was successfully deleted from the database and NO_CONTENT was returned"),
            @ApiResponse(responseCode = "404", description = "The inventory was not found in the database")
    })
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
