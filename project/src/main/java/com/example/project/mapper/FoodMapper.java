package com.example.project.mapper;


import com.example.project.dto.FoodDto;
import com.example.project.model.Food;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {InventoryMapper.class})
public interface FoodMapper extends EntityMapper<FoodDto, Food>{

    @Mapping(target = "inventoryDto", source = "inventory")
    FoodDto toDto(Food food);

    @Mapping(target = "inventory", source = "inventoryDto")
    Food toEntity(FoodDto foodDto);
}
