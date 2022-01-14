package com.example.project.controller;

import com.example.project.dto.MedicineDto;
import com.example.project.exception.BadRequestException;
import com.example.project.mapper.MedicineMapper;
import com.example.project.model.Medicine;
import com.example.project.service.MedicineService;
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
@RequestMapping("/medicine")
public class MedicineController {
    private final MedicineService medicineService;
    private final MedicineMapper medicineMapper;

    public MedicineController(MedicineService medicineService, MedicineMapper medicineMapper) {
        this.medicineService = medicineService;
        this.medicineMapper = medicineMapper;
    }

    @GetMapping
    @Operation(operationId = "Get all medicine", summary = "Get all medicine items from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The medicine items were successfully retrieved from the database",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MedicineDto[].class))})
    })
    public ResponseEntity<List<MedicineDto>> getAll() {
        List<Medicine> response = medicineService.findAll();
        return  new ResponseEntity<>(medicineMapper.toDto(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "Get medicine by id", summary = "Get a medicine item from the database by medicine id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The medicine item was successfully retrieved from the database"),
            @ApiResponse(responseCode = "404", description = "The medicine item was not found in the database")
    })
    public ResponseEntity<MedicineDto> getMedicinelById(@PathVariable Long id) {
        Medicine response = medicineService.findById(id);
        return new ResponseEntity<>(medicineMapper.toDto(response), HttpStatus.OK);
    }

    @PostMapping
    @Operation(operationId = "Create a medicine", summary = "Add a medicine to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The medicine was successfully added into the database"),
            @ApiResponse(responseCode = "400", description = "Validation error on the received request")
    })
    public ResponseEntity<MedicineDto> createMedicine(@RequestBody @Valid MedicineDto medicine) {
        Medicine response = medicineService.create(medicineMapper.toEntity(medicine));
        return new ResponseEntity<>(medicineMapper.toDto(response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "Update a medicine item", summary = "Update a medicine item in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The medicine was successfully updated into the database"),
            @ApiResponse(responseCode = "400", description = "The path variable does not match the request body id OR validation failed") ,
            @ApiResponse(responseCode = "404", description = "The medicine was not found in the database")
    })
    public ResponseEntity<MedicineDto> updateMedicine(@PathVariable Long id, @RequestBody @Valid MedicineDto medicine) {
        if (id != medicine.getId()) {
            throw new BadRequestException("The path variable does not match the request body id");
        }
        Medicine response = medicineService.update(medicineMapper.toEntity(medicine));
        return new ResponseEntity<>(medicineMapper.toDto(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "Delete a medicine item", summary = "Delete a medicine item from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The medicine was successfully deleted from the database and NO_CONTENT was returned"),
            @ApiResponse(responseCode = "404", description = "The medicine was not found in the database")
    })
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
