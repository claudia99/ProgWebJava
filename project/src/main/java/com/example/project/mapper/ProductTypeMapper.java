package com.example.project.mapper;

import com.example.project.dto.ProductTypeDto;
import com.example.project.model.ProductType;
import org.mapstruct.Mapper;

@Mapper
public interface ProductTypeMapper extends EntityMapper<ProductTypeDto, ProductType>{
}
