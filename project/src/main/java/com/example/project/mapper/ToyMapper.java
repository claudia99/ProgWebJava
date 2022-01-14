package com.example.project.mapper;

import com.example.project.dto.ToyDto;
import com.example.project.model.Toy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {InventoryMapper.class})
public interface ToyMapper  extends EntityMapper<ToyDto, Toy>{
    @Mapping(target = "inventoryDto", source = "inventory")
    ToyDto toDto(Toy toy);

    @Mapping(target = "inventory", source = "inventoryDto")
    Toy toEntity(ToyDto toyDto);
}
