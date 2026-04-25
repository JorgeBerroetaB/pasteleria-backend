package com.pasteleria.cakeapi.controllers;

import com.pasteleria.cakeapi.entities.Usuario;
import com.pasteleria.cakeapi.service.AuthService;
import com.pasteleria.cakeapi.repositories.UsuarioRepository; // Asegúrate de que esta ruta sea correcta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // Para encriptar
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // --- RUTA PARA LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<Usuario> usuario = authService.login(username, password);

        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            u.setPassword(null); // Borramos el hash antes de enviarlo a Flutter por seguridad
            return ResponseEntity.ok(u);
        } else {
            return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
        }
    }

    // --- RUTA PARA REGISTRAR EMPLEADOS ---
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            // Verificar si el username ya existe para no duplicar
            if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
                return ResponseEntity.status(400).body("El nombre de usuario ya está en uso");
            }

            // 1. Encriptamos la contraseña (pb10365 -> $2a$10$...)
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // 2. Valores por defecto
            usuario.setActivo(true);

            // Si el rol viene vacío, le ponemos USER por defecto
            if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
                usuario.setRol("USER");
            }

            // 3. Guardar
            Usuario nuevoUsuario = usuarioRepository.save(usuario);

            // Limpiamos la password en la respuesta
            nuevoUsuario.setPassword(null);

            return ResponseEntity.ok(nuevoUsuario);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar: " + e.getMessage());
        }
    }
}