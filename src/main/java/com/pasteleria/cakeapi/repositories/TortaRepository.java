package com.pasteleria.cakeapi.repositories;

import com.pasteleria.cakeapi.entities.Torta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // Importante para evitar null pointers

@Repository
public interface TortaRepository extends JpaRepository<Torta, Long> {

    // Buscar tortas de vitrina por su código de 5 dígitos
    Optional<Torta> findByCodigoBarrasBase(String codigo);
}