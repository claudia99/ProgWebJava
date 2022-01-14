package com.example.project.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String type;
    private Float price;
    private Long quantityPerUnit;
    private String animal;

    @OneToOne(cascade = CascadeType.ALL) // se propaga si delete-urile
    private Inventory inventory;
}
