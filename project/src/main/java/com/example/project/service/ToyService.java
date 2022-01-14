package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Toy;
import com.example.project.repository.ToyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToyService {
    private final ToyRepository toyRepository;

    public ToyService(ToyRepository toyRepository) {
        this.toyRepository = toyRepository;
    }

    public List<Toy> findAll() {
        return toyRepository.findAll();
    }

    public Toy findById(Long id) {
        return toyRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(String.format("The toy with id = %s does not exist in the database.",id.toString())));
    }

    public void deleteById(Long id) {
        if(toyRepository.existsById(id)){
            toyRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("The toy with id = %s does not exist in the database.",id.toString()));
        }
    }

    public Toy create(Toy toy) {
        return toyRepository.save(toy);
    }

    public Toy update(Toy toy) {
        if(toyRepository.existsById(toy.getId())){
            return toyRepository.save(toy);
        } else {
            throw new EntityNotFoundException(String.format("The animal with id = %s does not exist in the database.",toy.getId().toString()));
        }
    }

    public Boolean existsByInventoryId(Long id) {
        return toyRepository.existsByInventoryId(id);
    }

    public Long findByInventoryId(Long id) {
        return toyRepository.findByInventoryId(id).getId();
    }
}









