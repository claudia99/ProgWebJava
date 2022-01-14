package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Item;
import com.example.project.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item create(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> findByPurchaseId(Long id) { return itemRepository.findByPurchaseId(id);}

    public void deleteById(Long id) {
        if(itemRepository.existsById(id)){
            itemRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format("The item with id = %s does not exist in the database.",id.toString()));
        }
    }
}
