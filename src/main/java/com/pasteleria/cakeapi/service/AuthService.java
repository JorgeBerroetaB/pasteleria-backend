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

    public Usuario registrar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }

    // --- NUEVO MÉTODO PARA CAMBIAR CONTRASEÑA ---
    public boolean cambiarPassword(Long usuarioId, String nuevaPassword) {
        return usuarioRepository.findById(usuarioId).map(usuario -> {
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            usuarioRepository.save(usuario);
            return true;
        }).orElse(false);
    }
}