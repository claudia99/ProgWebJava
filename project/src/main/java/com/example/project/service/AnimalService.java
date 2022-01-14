package com.example.project.service;

import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Animal;
import com.example.project.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final ClientService clientService;
    public AnimalService(AnimalRepository animalRepository, ClientService clientService) {
        this.animalRepository = animalRepository;
        this.clientService = clientService;
    }

    public List<Animal> findAll() {
        return animalRepository.findAll();
    }

    public List<Animal> findByClient(Long id) {
        return animalRepository.findByOwnerId(id);
    }

    public Animal findById(Long id) {
        return animalRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(String.format("The animal with id = %s does not exist in the database.",id.toString())));
    }

    public void deleteById(Long id) {
        if(animalRepository.existsById(id)){
                animalRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("The animal with id = %s does not exist in the database.",id.toString()));
        }
    }

    public Animal create(Animal animal) {
        if (clientService.existById(animal.getOwner().getId())) {
            return animalRepository.save(animal);
        } else {
            throw new BadRequestException("You have to create the client before adding its animal!");
        }
    }

    public Animal update(Animal animal) {
        if(animalRepository.existsById(animal.getId())){
           return animalRepository.save(animal);
        } else {
            throw new EntityNotFoundException(String.format("The animal with id = %s does not exist in the database.",animal.getId().toString()));
        }
    }

}
