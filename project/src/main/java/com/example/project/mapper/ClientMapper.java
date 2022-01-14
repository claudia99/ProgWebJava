package com.example.project.mapper;

import com.example.project.dto.ClientDto;
import com.example.project.model.Client;
import org.mapstruct.Mapper;

@Mapper
public interface ClientMapper extends EntityMapper<ClientDto, Client>{
}
