package com.pasteleria.cakeapi.controllers;

import com.pasteleria.cakeapi.entities.Usuario;
import com.pasteleria.cakeapi.service.AuthService;
import com.pasteleria.cakeapi.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<Usuario> usuario = authService.login(username, password);

        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            u.setPassword(null);
            return ResponseEntity.ok(u);
        } else {
            return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
                return ResponseEntity.status(400).body("El nombre de usuario ya está en uso");
            }
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setActivo(true);
            if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
                usuario.setRol("USER");
            }
            Usuario nuevoUsuario = usuarioRepository.save(usuario);
            nuevoUsuario.setPassword(null);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar: " + e.getMessage());
        }
    }

    // --- NUEVA RUTA PARA CAMBIAR CONTRASEÑA ---
    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody Map<String, Object> request) {
        try {
            Long usuarioId = Long.valueOf(request.get("usuarioId").toString());
            String nuevaPassword = request.get("nuevaPassword").toString();

            boolean actualizado = authService.cambiarPassword(usuarioId, nuevaPassword);

            if (actualizado) {
                return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
            } else {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al cambiar contraseña: " + e.getMessage());
        }
    }
}