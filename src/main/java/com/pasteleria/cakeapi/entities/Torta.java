package com.pasteleria.cakeapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "tortas")
@Getter
@Setter
public class Torta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    // El código de 5 dígitos para ventas rápidas (Ej: 10001)
    @Column(unique = true)
    private String codigoBarrasBase;

    private String imagenUrl;
    private String imagenNombre;

    private String categoria; // "Torta", "Tarta", "Pastelito"

    @OneToMany(mappedBy = "torta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TamanoTorta> tamanos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "torta_cobertura",
            joinColumns = @JoinColumn(name = "torta_id"),
            inverseJoinColumns = @JoinColumn(name = "cobertura_id")
    )
    private List<Cobertura> coberturas = new ArrayList<>();
}