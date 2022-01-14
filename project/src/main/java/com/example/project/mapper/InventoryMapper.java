package com.example.project.mapper;
import com.example.project.dto.InventoryDto;
import com.example.project.model.Inventory;
import org.mapstruct.Mapper;

@Mapper
public interface InventoryMapper  extends EntityMapper<InventoryDto, Inventory>{
}
