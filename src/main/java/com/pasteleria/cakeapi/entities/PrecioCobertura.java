package com.pasteleria.cakeapi.entities;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "precios_cobertura")
@Data
public class PrecioCobertura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String capacidad; // Ej: "10", "20", "30"
    private Double precioAdicional;

    @ManyToOne
    @JoinColumn(name = "cobertura_id")
    @JsonIgnore
    private Cobertura cobertura;
}