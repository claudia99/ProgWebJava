package com.example.project.mapper;

import com.example.project.dto.PurchaseDto;
import com.example.project.model.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses  = {ItemMapper.class, ClientMapper.class})
public interface PurchaseMapper extends EntityMapper<PurchaseDto, Purchase>{
    @Mappings({
            @Mapping(target = "productsDto", source = "products"),
            @Mapping(target = "clientDto", source = "client")
    })
    PurchaseDto toDto(Purchase purchase);

    @Mappings({
            @Mapping(target="products", source = "productsDto"),
            @Mapping(target = "client", source = "clientDto")
    })
    Purchase toEntity(PurchaseDto purchaseDto);
}
