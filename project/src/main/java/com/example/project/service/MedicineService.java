package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Medicine;
import com.example.project.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> findAll() {
        return medicineRepository.findAll();
    }

    public Medicine findById(Long id) {
        return medicineRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(String.format("The medicine with id = %s does not exist in the database.",id.toString())));
    }

    public void deleteById(Long id) {
        if(medicineRepository.existsById(id)) {
            medicineRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("The medicine item with id = %s does not exist in the database.",id.toString()));
        }
    }

    public Medicine create(Medicine medicine) {
        return  medicineRepository.save(medicine);
    }

    public Medicine update(Medicine medicine) {
        if(medicineRepository.existsById(medicine.getId())) {
            return medicineRepository.save(medicine);
        } else {
            throw new EntityNotFoundException(String.format("The medicine item with id = %s does not exist in the database.",medicine.getId().toString()));
        }
    }

    public Boolean existsByInventoryId(Long id) {
        return medicineRepository.existsByInventoryId(id);
    }

    public Long findByInventoryId(Long id) {
        return medicineRepository.findByInventoryId(id).getId();
    }
}
