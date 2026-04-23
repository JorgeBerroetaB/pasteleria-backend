package com.pasteleria.cakeapi.repositories;

import com.pasteleria.cakeapi.entities.DiaBloqueado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DiaBloqueadoRepository extends JpaRepository<DiaBloqueado, Long> {
    Optional<DiaBloqueado> findByFecha(LocalDate fecha);
    boolean existsByFecha(LocalDate fecha);
}