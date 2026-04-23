package com.pasteleria.cakeapi.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class DiaBloqueado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private LocalDate fecha; // La fecha que el jefe decidió cerrar

    private String motivo; // Opcional: "Vacaciones", "Feriado"
}