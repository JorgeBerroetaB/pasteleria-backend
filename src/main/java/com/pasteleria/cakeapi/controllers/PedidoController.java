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

    @GetMapping("/escanear/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
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

        return tortaRepository.findByCodigoBarrasBase(codigo)
                .map(torta -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("nombre", torta.getNombre());
                    Double precioBase = (torta.getTamanos() != null && !torta.getTamanos().isEmpty())
                            ? torta.getTamanos().get(0).getPrecio() : 0.0;
                    response.put("precio", precioBase);
                    response.put("codigo", torta.getCodigoBarrasBase());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pedido agendarPedido(@RequestBody Pedido pedido) {
        if (pedido.getEstado() == null) {
            pedido.setEstado("PENDIENTE");
        }
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
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

            // --- LÍNEA CORREGIDA PARA LA COBERTURA ---
            pedido.setTipoCobertura(pedidoActualizado.getTipoCobertura());

            pedido.setPrecioTotal(pedidoActualizado.getPrecioTotal());
            pedido.setMontoAbonado(pedidoActualizado.getMontoAbonado());

            return pedidoRepository.save(pedido);
        }).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }
}