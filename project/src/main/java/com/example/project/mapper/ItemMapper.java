package com.example.project.mapper;

import com.example.project.dto.ItemDto;
import com.example.project.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {InventoryMapper.class})
public interface ItemMapper extends EntityMapper<ItemDto, Item>{
    @Mapping(target = "inventoryDto", source = "inventory")
    ItemDto toDto(Item item);

    @Mapping(target = "inventory", source = "inventoryDto")
    Item toEntity(ItemDto itemDto);
}
