package com.pasteleria.cakeapi.controllers;

import com.pasteleria.cakeapi.entities.Usuario;
import com.pasteleria.cakeapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Para que Flutter se conecte sin problemas
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // RUTA PARA LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<Usuario> usuario = authService.login(username, password);

        if (usuario.isPresent()) {
            // Si es correcto, devolvemos los datos del usuario (menos la clave por seguridad)
            Usuario u = usuario.get();
            u.setPassword(null);
            return ResponseEntity.ok(u);
        } else {
            return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
        }
    }

    // RUTA PARA QUE TÚ CREES EMPLEADOS (Úsala con cuidado o protégela después)
    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(authService.registrar(usuario));
    }
}