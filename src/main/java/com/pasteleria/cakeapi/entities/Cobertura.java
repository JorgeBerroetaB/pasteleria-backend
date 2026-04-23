package com.pasteleria.cakeapi.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "coberturas")
@Data
public class Cobertura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Agregué 'fetch = FetchType.EAGER' para que traiga los precios de una
    @OneToMany(mappedBy = "cobertura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PrecioCobertura> precios = new ArrayList<>();

    // --- EL TRUCO ESTÁ AQUÍ ---
    // Sobrescribimos el setter que crea Lombok para obligar a que
    // cada precio sepa a qué cobertura pertenece antes de guardarse.
    public void setPrecios(List<PrecioCobertura> precios) {
        this.precios.clear();
        if (precios != null) {
            for (PrecioCobertura p : precios) {
                p.setCobertura(this); // "Yo soy tu padre" (Cobertura vincula al Precio)
                this.precios.add(p);
            }
        }
    }
}