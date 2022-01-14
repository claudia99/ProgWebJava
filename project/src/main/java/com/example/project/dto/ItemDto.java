package com.example.project.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private Long orderedQuantity;

    private InventoryDto inventoryDto;
}
