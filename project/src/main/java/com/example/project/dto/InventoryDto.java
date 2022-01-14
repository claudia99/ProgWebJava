package com.example.project.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDto {
    private Long id;
    //private String type;

    @NotNull
    @Min(0)
    private Long availableQuantity;
}
