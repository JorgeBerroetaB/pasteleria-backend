package com.pasteleria.cakeapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter // Usamos Getter y Setter separados en lugar de @Data
public class TamanoTorta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String capacidad;
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "torta_id")
    @JsonBackReference
    private Torta torta;
}