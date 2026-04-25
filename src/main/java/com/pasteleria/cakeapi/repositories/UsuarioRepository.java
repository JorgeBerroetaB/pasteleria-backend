package com.pasteleria.cakeapi.repositories;

import com.pasteleria.cakeapi.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Esto sirve para buscar al empleado por su nombre al hacer login
    Optional<Usuario> findByUsername(String username);
}