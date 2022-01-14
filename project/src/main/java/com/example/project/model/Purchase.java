package com.example.project.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="purchase")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float price;
    private LocalDateTime time;

    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "purchase",  cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Item> products;
}
