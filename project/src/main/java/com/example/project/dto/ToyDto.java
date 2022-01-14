package com.example.project.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToyDto {
    private Long id;

    private String animal;

    @NotNull
    @Min(0)
    private Float price;

    @NotEmpty
    private String brand;

    @NotNull
    private InventoryDto inventoryDto;
}
