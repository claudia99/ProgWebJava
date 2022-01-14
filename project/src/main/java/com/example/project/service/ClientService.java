package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Client;
import com.example.project.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("The client with id = %s does not exist in the database.", id.toString())));
    }

    public void deleteById(Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("The client with id = %s does not exist in the database.", id.toString()));
        }
    }

    public Boolean existById(Long id) {
        return clientRepository.existsById(id);
    }

    public Client create(Client client) {
        return clientRepository.save(client);
    }

    public Client update(Client client) {
        if(clientRepository.existsById(client.getId())) {
            return clientRepository.save(client);
        } else {
            throw new EntityNotFoundException(String.format("The client with id = %s does not exist in the database.",client.getId().toString()));
        }
    }
}
