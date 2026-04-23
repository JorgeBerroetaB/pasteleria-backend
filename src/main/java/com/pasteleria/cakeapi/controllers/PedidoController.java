package com.pasteleria.cakeapi.controllers;

import com.pasteleria.cakeapi.entities.Pedido;
import com.pasteleria.cakeapi.repositories.PedidoRepository;
import com.pasteleria.cakeapi.repositories.TortaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Inyectamos el repo de Tortas para poder buscar los códigos de 5 dígitos
    @Autowired
    private TortaRepository tortaRepository;

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/mes")
    public List<LocalDate> obtenerFechasOcupadas(
            @RequestParam int anio,
            @RequestParam int mes) {
        return pedidoRepository.findFechasOcupadasDelMes(anio, mes);
    }

    @GetMapping("/fecha/{fecha}")
    public List<Pedido> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return pedidoRepository.findByFechaEntrega(fecha);
    }

    // ==========================================
    // NUEVO: ENDPOINT PARA ESCANEAR EN LA CAJA
    // ==========================================
    @GetMapping("/escanear/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {

        // CASO 1: Es un pedido agendado (Ej: PED-12)
        if (codigo.toUpperCase().startsWith("PED-")) {
            return pedidoRepository.findByCodigoBarrasPedido(codigo.toUpperCase())
                    .map(pedido -> {
                        if ("PAGADO".equals(pedido.getEstado())) {
                            return ResponseEntity.badRequest().body("Este pedido ya fue pagado.");
                        }

                        Map<String, Object> response = new HashMap<>();
                        response.put("nombre", (pedido.getTorta() != null ? pedido.getTorta().getNombre() : "Pedido") + " (SALDO)");
                        response.put("precio", pedido.getSaldoPendiente());
                        response.put("codigo", pedido.getCodigoBarrasPedido());

                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.notFound().build());
        }

        // CASO 2: Es una torta normal (Ej: 10001)
        return tortaRepository.findByCodigoBarrasBase(codigo)
                .map(torta -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("nombre", torta.getNombre());

                    // Tomamos el precio del primer tamaño si existe
                    Double precioBase = (torta.getTamanos() != null && !torta.getTamanos().isEmpty())
                            ? torta.getTamanos().get(0).getPrecio() : 0.0;

                    response.put("precio", precioBase);
                    response.put("codigo", torta.getCodigoBarrasBase());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // ACTUALIZADO: GENERAR "PED-ID" AL GUARDAR
    // ==========================================
    @PostMapping
    public Pedido agendarPedido(@RequestBody Pedido pedido) {
        if (pedido.getEstado() == null) {
            pedido.setEstado("PENDIENTE");
        }

        // 1. Guardamos primero para que la base de datos le asigne un ID numérico (Ej: 12)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 2. Si no tiene código de barras, le generamos uno usando su nuevo ID y actualizamos
        if (pedidoGuardado.getCodigoBarrasPedido() == null || pedidoGuardado.getCodigoBarrasPedido().isEmpty()) {
            pedidoGuardado.setCodigoBarrasPedido("PED-" + pedidoGuardado.getId());
            pedidoGuardado = pedidoRepository.save(pedidoGuardado);
        }

        return pedidoGuardado;
    }

    @DeleteMapping("/{id}")
    public void eliminarPedido(@PathVariable Long id) {
        pedidoRepository.deleteById(id);
    }

    // ==========================================
    // ACTUALIZADO: INCLUIR CAMPOS DE DINERO
    // ==========================================
    @PutMapping("/{id}")
    public Pedido actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedidoActualizado) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setNombreCliente(pedidoActualizado.getNombreCliente());
            pedido.setTelefono(pedidoActualizado.getTelefono());
            pedido.setNotas(pedidoActualizado.getNotas());
            pedido.setFechaEntrega(pedidoActualizado.getFechaEntrega());
            pedido.setBloqueHorario(pedidoActualizado.getBloqueHorario());
            pedido.setEstado(pedidoActualizado.getEstado());
            pedido.setTorta(pedidoActualizado.getTorta());
            pedido.setDetalleTamano(pedidoActualizado.getDetalleTamano());

            // Actualizamos los campos financieros
            pedido.setPrecioTotal(pedidoActualizado.getPrecioTotal());
            pedido.setMontoAbonado(pedidoActualizado.getMontoAbonado());
            // Nota: saldoPendiente se calcula solo gracias al @PreUpdate de la entidad Pedido.java

            return pedidoRepository.save(pedido);
        }).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }
}