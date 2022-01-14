package com.example.project.controller;

import com.example.project.dto.PurchaseDto;
import com.example.project.mapper.PurchaseMapper;
import com.example.project.model.Purchase;
import com.example.project.service.PurchaseService;
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
@RequestMapping("/purchases")
//@Api(value = "/purchases", tags = "All purchases existing in  the database")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final PurchaseMapper purchaseMapper;

    public PurchaseController(PurchaseService purchaseService, PurchaseMapper purchaseMapper) {
        this.purchaseService = purchaseService;
        this.purchaseMapper = purchaseMapper;
    }

    @GetMapping
    @Operation(operationId = "Get all purchases", summary = "Get all purchases from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The purchases were successfully retrieved from the database",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PurchaseDto[].class))})
    })
    public ResponseEntity<List<PurchaseDto>> findAll() {
        List<Purchase> response = purchaseService.findAll();
        return new ResponseEntity<>(purchaseMapper.toDto(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "Get a purchase by id", summary = "Get a purchase from the database by purchase id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The purchase was successfully retrieved from the database"),
            @ApiResponse(responseCode = "404", description = "The purchase was not found in the database")
    })
    public ResponseEntity<PurchaseDto> findById(@PathVariable Long id) {
        Purchase response = purchaseService.findById(id);
        return new ResponseEntity<>(purchaseMapper.toDto(response), HttpStatus.OK);
    }

    @GetMapping("/client")
    @Operation(operationId = "Get purchases by client", summary = "Get all purchases from the database filtered by client id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The purchases were successfully retrieved from the database",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PurchaseDto[].class))})
    })
    public ResponseEntity<List<PurchaseDto>> findByClient(@RequestParam Long id) {
        List<Purchase> response = purchaseService.findByClient(id);
        return new ResponseEntity<>(purchaseMapper.toDto(response), HttpStatus.OK);
    }

    @PostMapping
    @Operation(operationId = "Create a purchase", summary = "Add a purchase to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The purchase was successfully added into the database"),
            @ApiResponse(responseCode = "400", description = "Validation error on the received request")
    })
    public ResponseEntity<PurchaseDto> createPurchase(@RequestBody @Valid PurchaseDto purchase) {
        Purchase response = purchaseService.create(purchaseMapper.toEntity(purchase));
        return new ResponseEntity<>(purchaseMapper.toDto(response), HttpStatus.CREATED);
    }

    /*@PutMapping("/{id}") ----> un user nu poate sa modifice o comanda, poate doar sa o anuleze(delete)
    public ResponseEntity<PurchaseDto> updatePurchase(@PathVariable Long id, @RequestBody @Valid PurchaseDto purchase) {
        if (id != purchase.getId()) {
            throw new BadRequestException("The path variable does not match the request body id");
        }
        Purchase response = purchaseService.update(purchaseMapper.toEntity(purchase));
        return new ResponseEntity<>(purchaseMapper.toDto(response), HttpStatus.OK);
    }*/

    @DeleteMapping("/{id}")
    @Operation(operationId = "Delete a purchase", summary = "Delete a purchase from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The purchase was successfully deleted from the database and NO_CONTENT was returned"),
            @ApiResponse(responseCode = "404", description = "The purchase was not found in the database")
    })
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        purchaseService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
