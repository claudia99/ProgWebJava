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
public class MedicineDto {
    private Long id;
    @NotEmpty
    private String animal;
    @NotEmpty
    private String purpose;
    @NotNull
    @Min(0)
    private Float price;
    @NotNull
    private InventoryDto inventoryDto;
}
