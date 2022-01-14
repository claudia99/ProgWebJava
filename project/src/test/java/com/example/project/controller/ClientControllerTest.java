package com.example.project.controller;

import com.example.project.dto.ClientDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.mapper.ClientMapper;
import com.example.project.model.Animal;
import com.example.project.model.Client;
import com.example.project.service.ClientService;
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
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @Spy
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientController clientController;

    private Client expectedClient;
    private ClientDto expectedDto;

    @BeforeEach
    void setUp() {
        List<Animal> animalList = new ArrayList<>();
        animalList.add(Animal.builder().id(1L).build());
        expectedClient = Client.builder()
                .id(1L)
                .first_name("Claudia")
                .last_name("Apostol")
                .email("apostol.claudia99@gmail.com")
                .city("Bucharest")
                .birth_date(LocalDate.of(1999, 06, 19))
                .animals(animalList)
                .build();
        expectedDto = ClientDto.builder()
                .id(1L)
                .first_name("Claudia")
                .last_name("Apostol")
                .email("apostol.claudia99@gmail.com")
                .city("Bucharest")
                .birth_date(LocalDate.of(1999, 06, 19))
                .build();
    }

    @Test
    @DisplayName("get all clients - happy flow")
    public void test_getAll_happyFlow() {
        List<Client> clientList = new ArrayList<>();
        clientList.add(expectedClient);

        when(clientService.findAll()).thenReturn(clientList);
        ResponseEntity<List<ClientDto>> result = clientController.getAll();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(clientMapper.toDto(clientList));

        verify(clientService).findAll();
        verify(clientMapper, times(2)).toDto(clientList);
    }

    @Test
    @DisplayName("get a client by id - happy flow")
    public void test_getClientById_happyFlow() {
        Long id = expectedClient.getId();

        when(clientService.findById(id)).thenReturn(expectedClient);


        ResponseEntity<ClientDto> result = clientController.getClientById(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(clientMapper.toDto(expectedClient));

        verify(clientService).findById(id);
        verify(clientMapper, times(2)).toDto(expectedClient);
        verify(clientMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("get client by id - client does not exist in database")
    public void test_getClientById_throwsEntityNotFoundException_whenClientNotFound() {
        Long id = expectedClient.getId();

        when(clientService.findById(id)).thenThrow(new EntityNotFoundException(String.format("The client with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> clientController.getClientById(id));

        assertThat(ex.getMessage()).isEqualTo(String.format("The client with id = %s does not exist in the database.",id.toString()));

        verify(clientService).findById(id);
        verify(clientMapper, times(0)).toDto(expectedClient);
        verify(clientMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("add a client - happy flow")
    public void test_addClient_happyFlow() {
        ClientDto clientDto = ClientDto.builder()
                .first_name("Claudia")
                .last_name("Apostol")
                .email("apostol.claudia99@gmail.com")
                .city("Bucharest")
                .birth_date(LocalDate.of(1999, 06, 19))
                .build();
        when(clientService.create(clientMapper.toEntity(clientDto))).thenReturn(expectedClient);

        ResponseEntity<ClientDto> result = clientController.addClient(clientDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo(clientMapper.toDto(expectedClient));

        verify(clientService, times(1)).create(clientMapper.toEntity(clientDto));
        verify(clientMapper, times(2)).toDto(expectedClient);
        verify(clientMapper, times(3)).toEntity(clientDto);
    }

    @Test
    @DisplayName("update a client - happy flow")
    public void test_updateClient_happyFlow() {
        Long id = expectedClient.getId();

        when(clientService.update(clientMapper.toEntity(expectedDto))).thenReturn(expectedClient);

        ResponseEntity<ClientDto> result = clientController.updateClient(id, expectedDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(clientMapper.toDto(expectedClient));

        verify(clientService).update(clientMapper.toEntity(expectedDto));
        verify(clientMapper, times(2)).toDto(expectedClient);
        verify(clientMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update an animal - animal does not exist in database")
    public void test_updateClient_throwsEntityNotFoundException_whenClientNotFound() {
        Long id = expectedClient.getId();
        when(clientService.update(clientMapper.toEntity(expectedDto))).thenThrow(new EntityNotFoundException(String.format("The client with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> clientController.updateClient(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo(String.format("The client with id = %s does not exist in the database.",id.toString()));

        verify(clientService).update(clientMapper.toEntity(expectedDto));
        verify(clientMapper, times(0)).toDto(expectedClient);
        verify(clientMapper, times(3)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("update a client - id from path variable and response variable do not match")
    public void test_updateClient_throwsBadRequestException_whenIdFromPathVariableAndResponseVariableDontMatch() {
        Long id = expectedClient.getId();
        expectedDto.setId(id+1);

        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () -> clientController.updateClient(id, expectedDto));

        assertThat(ex.getMessage()).isEqualTo("The path variable does not match the request body id");

        verify(clientService, times(0)).update(expectedClient);

        verify(clientMapper, times(0)).toDto(expectedClient);
        verify(clientMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("delete a client - happy flow")
    public void test_deleteClient_happyFlow() {
        Long id = expectedClient.getId();

        doNothing().when(clientService).deleteById(id);

        ResponseEntity<Void> result = clientController.deleteClient(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        verify(clientService).deleteById(id);
        verify(clientMapper, times(0)).toDto(expectedClient);
        verify(clientMapper, times(0)).toEntity(expectedDto);
    }
}