package com.example.project.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="medicine")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String animal;
    private Float price;
    private String purpose;

    @OneToOne(cascade = CascadeType.ALL)
    private Inventory inventory;
}
