package com.example.project.mapper;

import com.example.project.dto.MedicineDto;
import com.example.project.model.Medicine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {InventoryMapper.class})
public interface MedicineMapper extends EntityMapper<MedicineDto, Medicine>{
    @Mapping(target = "inventoryDto", source = "inventory")
    MedicineDto toDto(Medicine medicine);

    @Mapping(target = "inventory", source = "inventoryDto")
    Medicine toEntity(MedicineDto medicineDto);
}
