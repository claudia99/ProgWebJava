package com.example.project.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="inventory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private String type;

    private Long availableQuantity;

    @OneToMany(mappedBy = "inventory",  cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Item> itemList;

    @OneToOne
    private Food food;

    @OneToOne
    private Toy toy;

    @OneToOne
    private  Medicine medicine;
}
