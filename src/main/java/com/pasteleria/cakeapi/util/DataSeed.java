package com.pasteleria.cakeapi.util;

import com.pasteleria.cakeapi.entities.Usuario;
import com.pasteleria.cakeapi.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeed implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public DataSeed(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            // Aquí pones la clave que quieras, se guardará encriptada
            admin.setPassword(new BCryptPasswordEncoder().encode("12345"));
            admin.setNombreCompleto("Jorge Admin");
            admin.setRol("ADMIN");
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: admin / 12345");
        }
    }
}