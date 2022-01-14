package com.example.project.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="animal")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="animal_id")
    private Long id;

    @Column(name="animal_name")
    private String name;

    @Column(name="species")
    private String species;

    private String breed;

    @Column(name="animal_birth_date")
    private LocalDate birth_date;


    @ManyToOne
    private Client owner;
}
