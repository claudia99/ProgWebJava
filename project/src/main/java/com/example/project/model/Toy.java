package com.example.project.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="toy")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Toy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String animal;
    private Float price;
    private String brand;

    @OneToOne(cascade = CascadeType.ALL)
    private Inventory inventory;
}
