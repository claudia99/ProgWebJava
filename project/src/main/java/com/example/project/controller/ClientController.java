package com.example.project.controller;

import com.example.project.dto.ClientDto;
import com.example.project.exception.BadRequestException;
import com.example.project.mapper.ClientMapper;
import com.example.project.model.Client;
import com.example.project.service.ClientService;
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
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;
    private final ClientMapper clientMapper;

    public ClientController(ClientService clientService, ClientMapper clientMapper) {
        this.clientService = clientService;
        this.clientMapper = clientMapper;
    }

    @GetMapping
    @Operation(operationId = "Get all clients", summary = "Get all clients from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The clients were successfully retrieved from the database",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Client[].class))})
    })
    public ResponseEntity<List<ClientDto>> getAll() {
        List<Client> response = clientService.findAll();
        return new ResponseEntity<>(clientMapper.toDto(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "Get a client by id", summary = "Get a client from the database by client id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The client was successfully retrieved from the database"),
            @ApiResponse(responseCode = "404", description = "The client was not found in the database")
    })
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        Client response = clientService.findById(id);
        return new ResponseEntity<>(clientMapper.toDto(response), HttpStatus.OK);
    }

    @PostMapping
    @Operation(operationId = "Create a client", summary = "Add a client to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The client was successfully added into the database"),
            @ApiResponse(responseCode = "400", description = "Validation error on the received request")
    })
    public ResponseEntity<ClientDto> addClient(@RequestBody @Valid ClientDto client) {
        Client response = clientService.create(clientMapper.toEntity(client));
        return new ResponseEntity<>(clientMapper.toDto(response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "Update a client", summary = "Update a client in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The client was successfully updated into the database"),
            @ApiResponse(responseCode = "400", description = "The path variable does not match the request body id OR validation failed") ,
            @ApiResponse(responseCode = "404", description = "The client was not found in the database")
    })
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody @Valid ClientDto client) {
        if (id != client.getId()) {
            throw new BadRequestException("The path variable does not match the request body id");
        }
        Client response = clientService.update(clientMapper.toEntity(client));
        return new ResponseEntity<>(clientMapper.toDto(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "Delete an client", summary = "Delete a client from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The client was successfully deleted from the database and NO_CONTENT was returned"),
            @ApiResponse(responseCode = "404", description = "The client was not found in the database")
    })
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
