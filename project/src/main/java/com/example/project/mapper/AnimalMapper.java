package com.example.project.mapper;

import com.example.project.dto.AnimalDto;
import com.example.project.model.Animal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(uses = {ClientMapper.class})
public interface AnimalMapper extends EntityMapper<AnimalDto, Animal> {

    @Mapping(target = "ownerDto", source = "owner")
    AnimalDto toDto(Animal animal);

    @Mapping(target="owner", source = "ownerDto")
    Animal toEntity(AnimalDto animalDto);
}
