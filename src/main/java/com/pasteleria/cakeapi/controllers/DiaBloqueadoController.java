package com.pasteleria.cakeapi.controllers;

import com.pasteleria.cakeapi.entities.DiaBloqueado;
import com.pasteleria.cakeapi.repositories.DiaBloqueadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dias-bloqueados")
@CrossOrigin(origins = "*")
public class DiaBloqueadoController {

    @Autowired
    private DiaBloqueadoRepository repository;

    // Obtener todos los días bloqueados
    @GetMapping
    public List<DiaBloqueado> obtenerTodos() {
        return repository.findAll();
    }

    // Bloquear un día
    @PostMapping
    public DiaBloqueado bloquearDia(@RequestBody DiaBloqueado dia) {
        return repository.save(dia);
    }

    // Desbloquear un día (por fecha)
    @DeleteMapping("/{id}")
    public void desbloquearDia(@PathVariable Long id) {
        repository.deleteById(id);
    }
}