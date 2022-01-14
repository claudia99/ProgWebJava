package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Animal;
import com.example.project.model.Client;
import com.example.project.repository.ClientRepository;
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
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client expectedClient;

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
    }

    @Test
    @DisplayName("find all clients - happy flow")
    public void test_findAll_happyFlow() {
        List<Client> clientList = new ArrayList<>();
        clientList.add(expectedClient);

        when(clientRepository.findAll()).thenReturn(clientList);

        List<Client> result = clientService.findAll();

        assertEquals(clientList.size(), result.size());
        assertEquals(expectedClient.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedClient.getFirst_name(), result.stream().findFirst().get().getFirst_name());
        assertEquals(expectedClient.getLast_name(), result.stream().findFirst().get().getLast_name());
        assertEquals(expectedClient.getCity(), result.stream().findFirst().get().getCity());
        assertEquals(expectedClient.getBirth_date(), result.stream().findFirst().get().getBirth_date());
        //assertEquals(expectedClient.getType(), result.stream().findFirst().get().getType());
        //assertEquals(expectedClient.getQuantityPerUnit(), result.stream().findFirst().get().getQuantityPerUnit());

        verify(clientRepository).findAll();
    }


    @Test
    @DisplayName("find client by id - happy flow")
    public void test_findById_happyFlow() {
        Long id = expectedClient.getId();

        when(clientRepository.findById(id)).thenReturn(Optional.of(expectedClient));

        Client result = clientService.findById(id);

        assertEquals(expectedClient.getId(), result.getId());
        assertEquals(expectedClient.getCity(), result.getCity());
        assertEquals(expectedClient.getBirth_date(), result.getBirth_date());
        assertEquals(expectedClient.getFirst_name(), result.getFirst_name());
        assertEquals(expectedClient.getLast_name(), result.getLast_name());
        assertEquals(expectedClient.getAnimals(), result.getAnimals());
        //assertEquals(expectedClient.getInventory(), result.getInventory());

        verify(clientRepository).findById(id);
    }


    @Test
    @DisplayName("find client by id - client does not exist in database")
    public void test_findById_throwsEntityNotFoundException_whenClientNotFound() {
        Long id = expectedClient.getId();

        when(clientRepository.findById(id)).thenThrow(new EntityNotFoundException(String.format("The client with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> clientService.findById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The client with id = %s does not exist in the database.",id.toString()));

        verify(clientRepository).findById(id);
    }

    @Test
    @DisplayName("find if client exists in database - happy flow")
    public void test_existById_happyFlow() {
        Long id = expectedClient.getId();

        when(clientRepository.existsById(id)).thenReturn(true);

        Boolean result = clientService.existById(id);

        assertEquals(true, result);

        verify(clientRepository).existsById(id);
    }

    @Test
    @DisplayName("delete client by id - happy flow")
    public void test_deleteById_happyFlow() {
        Long id = expectedClient.getId();

        when(clientRepository.existsById(id)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(id);

        clientService.deleteById(id);

        verify(clientRepository).existsById(id);
        verify(clientRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete client by id - client does not exist in database")
    public void test_deleteById_throwsEntityNotFoundException_whenClientNotFound() {
        Long id = expectedClient.getId();

        when(clientRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                clientService.deleteById(id));
        assertThat(ex.getMessage()).isEqualTo(String.format("The client with id = %s does not exist in the database.",id.toString()));

        verify(clientRepository).existsById(id);
        verify(clientRepository, times(0)).deleteById(id);
    }

    @Test
    @DisplayName("create client - happy flow")
    public void test_create_happyFlow() {
        Client client = Client.builder()
                .first_name("Claudia")
                .last_name("Apostol")
                .city("Bucharest")
                .birth_date(LocalDate.of(1999, 06, 19))
                .build();

        when(clientRepository.save(client)).thenReturn(expectedClient);

        Client result = clientService.create(client);

        assertEquals(expectedClient.getId(), result.getId());
        assertEquals(expectedClient.getCity(), result.getCity());
        assertEquals(expectedClient.getBirth_date(), result.getBirth_date());
        assertEquals(expectedClient.getFirst_name(), result.getFirst_name());
        assertEquals(expectedClient.getLast_name(), result.getLast_name());

        verify(clientRepository).save(client);
    }

    @Test
    @DisplayName("update a client - happy flow")
    public void test_update_happyFlow() {
        Client client = expectedClient;
        Long id = expectedClient.getId();

        when(clientRepository.existsById(id)).thenReturn(true);
        when(clientRepository.save(client)).thenReturn(expectedClient);

        Client result = clientService.update(client);

        assertEquals(expectedClient.getId(), result.getId());
        assertEquals(expectedClient.getCity(), result.getCity());
        assertEquals(expectedClient.getBirth_date(), result.getBirth_date());
        assertEquals(expectedClient.getFirst_name(), result.getFirst_name());
        assertEquals(expectedClient.getLast_name(), result.getLast_name());

        verify(clientRepository).existsById(id);
        verify(clientRepository).save(client);
    }

    @Test
    @DisplayName("update a client - client does not exist in database")
    public void test_update_throwsEntityNotFoundException_whenClientNotFound() {
        Long id = expectedClient.getId();

        when(clientRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                clientService.update(expectedClient));

        assertThat(ex.getMessage()).isEqualTo(String.format("The client with id = %s does not exist in the database.",id.toString()));

        verify(clientRepository).existsById(id);
        verify(clientRepository, times(0)).save(expectedClient);
    }
}