package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Food;
import com.example.project.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService {
    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public List<Food> findAll() {
        return foodRepository.findAll();
    }

    public Food findById(Long id) {
        return foodRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(String.format("The purchase with id = %s does not exist in the database.",id.toString())));
    }

    public void deleteById(Long id) {
        if(foodRepository.existsById(id)) {
            foodRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("The food item with id = %s does not exist in the database.",id.toString()));
        }
    }

    public Food create(Food food) {
        return  foodRepository.save(food);
    }

    public Food update(Food food) {
        if(foodRepository.existsById(food.getId())) {
            return foodRepository.save(food);
        } else {
            throw new EntityNotFoundException(String.format("The food item with id = %s does not exist in the database.",food.getId().toString()));
        }
    }

    public Boolean existsByInventoryId(Long id) {
        return foodRepository.existsByInventoryId(id);
    }

    public Long findByInventoryId(Long id) {
        return foodRepository.findByInventoryId(id).getId();
    }
}
