package com.pasteleria.cakeapi.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasteleria.cakeapi.entities.Torta;
import com.pasteleria.cakeapi.entities.TamanoTorta;
import com.pasteleria.cakeapi.entities.Cobertura;
import com.pasteleria.cakeapi.repositories.TortaRepository;
import com.pasteleria.cakeapi.repositories.CoberturaRepository; // IMPORTANTE
import com.pasteleria.cakeapi.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tortas")
@CrossOrigin(origins = "*")
public class TortaController {

    @Autowired
    private TortaRepository tortaRepository;

    @Autowired
    private CoberturaRepository coberturaRepository; // NECESITAMOS ESTO PARA BUSCAR LOS IDs

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public List<Torta> listarTortas() {
        return tortaRepository.findAll();
    }

    @PostMapping
    public Torta crearTorta(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam("tamanosJson") String tamanosJson,
            @RequestParam(value = "coberturasIds", required = false) String coberturasIdsJson, // AHORA RECIBE IDs
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        Torta torta = new Torta();
        torta.setNombre(nombre);
        torta.setDescripcion(descripcion);
        torta.setCategoria(categoria);

        // Subida de imagen
        if (file != null && !file.isEmpty()) {
            try {
                Map result = cloudinaryService.upload(file);
                if (result != null && result.containsKey("secure_url")) {
                    torta.setImagenUrl((String) result.get("secure_url"));
                }
            } catch (Exception e) {
                System.err.println("Error al subir a Cloudinary: " + e.getMessage());
            }
        }

        ObjectMapper mapper = new ObjectMapper();

        // 1. Procesar Tamaños
        try {
            List<TamanoTorta> listaTamanos = mapper.readValue(tamanosJson, new TypeReference<List<TamanoTorta>>() {});
            if (listaTamanos != null) {
                for (TamanoTorta tamano : listaTamanos) {
                    tamano.setTorta(torta);
                }
                torta.setTamanos(listaTamanos);
            }
        } catch (Exception e) {
            System.err.println("Error al procesar los tamaños: " + e.getMessage());
        }

        // 2. Procesar Coberturas desde los IDs
        if (coberturasIdsJson != null && !coberturasIdsJson.isEmpty() && !coberturasIdsJson.equals("[]")) {
            try {
                // Convertir "[1, 2]" a una lista de Longs
                List<Long> ids = mapper.readValue(coberturasIdsJson, new TypeReference<List<Long>>() {});
                // Buscar las coberturas reales en la BD
                List<Cobertura> coberturasSeleccionadas = coberturaRepository.findAllById(ids);
                torta.setCoberturas(coberturasSeleccionadas);
            } catch (Exception e) {
                System.err.println("Error al buscar coberturas por ID: " + e.getMessage());
            }
        }

        return tortaRepository.save(torta);
    }

    @PutMapping("/{id}")
    public Torta actualizarTorta(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam("tamanosJson") String tamanosJson,
            @RequestParam(value = "coberturasIds", required = false) String coberturasIdsJson, // AHORA RECIBE IDs
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        return tortaRepository.findById(id)
                .map(torta -> {
                    torta.setNombre(nombre);
                    torta.setDescripcion(descripcion);
                    torta.setCategoria(categoria);

                    if (file != null && !file.isEmpty()) {
                        try {
                            Map result = cloudinaryService.upload(file);
                            if (result != null && result.containsKey("secure_url")) {
                                torta.setImagenUrl((String) result.get("secure_url"));
                            }
                        } catch (IOException e) {
                            System.err.println("Error al actualizar imagen: " + e.getMessage());
                        }
                    }

                    ObjectMapper mapper = new ObjectMapper();

                    // Actualizar tamaños
                    try {
                        List<TamanoTorta> nuevosTamanos = mapper.readValue(tamanosJson, new TypeReference<List<TamanoTorta>>() {});
                        if (nuevosTamanos != null) {
                            torta.getTamanos().clear();
                            for (TamanoTorta t : nuevosTamanos) {
                                t.setTorta(torta);
                                torta.getTamanos().add(t);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error al actualizar tamaños: " + e.getMessage());
                    }

                    // Actualizar coberturas desde IDs
                    if (coberturasIdsJson != null && !coberturasIdsJson.isEmpty() && !coberturasIdsJson.equals("[]")) {
                        try {
                            List<Long> ids = mapper.readValue(coberturasIdsJson, new TypeReference<List<Long>>() {});
                            List<Cobertura> nuevasCoberturas = coberturaRepository.findAllById(ids);
                            torta.setCoberturas(nuevasCoberturas);
                        } catch (Exception e) {
                            System.err.println("Error al actualizar coberturas por ID: " + e.getMessage());
                        }
                    } else {
                        torta.setCoberturas(new ArrayList<>()); // Si mandan vacío, vaciamos las coberturas
                    }

                    return tortaRepository.save(torta);
                })
                .orElseThrow(() -> new RuntimeException("Torta no encontrada"));
    }

    @DeleteMapping("/{id}")
    public void eliminarTorta(@PathVariable Long id) {
        tortaRepository.deleteById(id);
    }
}