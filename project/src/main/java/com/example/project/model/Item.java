package com.example.project.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderedQuantity;

    @ManyToOne
    private Inventory inventory;

    @ManyToOne
    private Purchase purchase;
}
