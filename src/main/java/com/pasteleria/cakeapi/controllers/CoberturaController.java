package com.pasteleria.cakeapi.controllers;

import com.pasteleria.cakeapi.entities.Cobertura;
import com.pasteleria.cakeapi.entities.PrecioCobertura;
import com.pasteleria.cakeapi.repositories.CoberturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coberturas")
@CrossOrigin(origins = "*")
public class CoberturaController {

    @Autowired
    private CoberturaRepository coberturaRepository;

    // 1. Obtener TODAS las coberturas (Esto es lo que usará la pantalla de "Nuevo Producto" para quitar el mensaje rojo)
    @GetMapping
    public List<Cobertura> listarCoberturas() {
        return coberturaRepository.findAll();
    }

    // 2. Crear una nueva cobertura (Desde el nuevo panel de administración)
    @PostMapping
    public Cobertura crearCobertura(@RequestBody Cobertura cobertura) {
        // Antes de guardar, vinculamos cada precio a esta cobertura
        if (cobertura.getPrecios() != null) {
            for (PrecioCobertura precio : cobertura.getPrecios()) {
                precio.setCobertura(cobertura);
            }
        }
        return coberturaRepository.save(cobertura);
    }

    // 3. Actualizar una cobertura existente
    @PutMapping("/{id}")
    public Cobertura actualizarCobertura(@PathVariable Long id, @RequestBody Cobertura coberturaActualizada) {
        return coberturaRepository.findById(id)
                .map(cobertura -> {
                    cobertura.setNombre(coberturaActualizada.getNombre());

                    // Limpiamos los precios antiguos y ponemos los nuevos
                    cobertura.getPrecios().clear();
                    if (coberturaActualizada.getPrecios() != null) {
                        for (PrecioCobertura precio : coberturaActualizada.getPrecios()) {
                            precio.setCobertura(cobertura);
                            cobertura.getPrecios().add(precio);
                        }
                    }
                    return coberturaRepository.save(cobertura);
                }).orElseThrow(() -> new RuntimeException("Cobertura no encontrada"));
    }

    // 4. Eliminar una cobertura del catálogo
    @DeleteMapping("/{id}")
    public void eliminarCobertura(@PathVariable Long id) {
        coberturaRepository.deleteById(id);
    }
}