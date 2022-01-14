package com.example.project.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodDto {

    private Long id;

    @NotEmpty
    private String brand;

    @NotEmpty
    @Pattern(regexp = "^[A-Za-z ,-]*$")
    private String type;

    @NotNull
    @Min(0)
    private Float price;

    @NotNull
    @Min(0)
    private Long quantityPerUnit;

    @NotEmpty
    @Size(min = 3)
    @Pattern(regexp = "^[A-Za-z ,-]*$")
    private String animal;

    @NotNull
    private InventoryDto inventoryDto;

}
