package com.example.project.mapper;

import java.util.List;

public interface EntityMapper<D, E>{
    E toEntity(D dto);
    D toDto(E entity);
    List<E> toEntity(List<D> dto);
    List<D> toDto(List<E> entity);
}
