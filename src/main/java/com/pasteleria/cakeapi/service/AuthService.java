package com.pasteleria.cakeapi.service;

import com.pasteleria.cakeapi.entities.Usuario;
import com.pasteleria.cakeapi.repositories.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Para que TÚ crees empleados con clave encriptada
    public Usuario registrar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    // Para verificar el login
    public Optional<Usuario> login(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }
}