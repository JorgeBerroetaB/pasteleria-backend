package com.pasteleria.cakeapi.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único para el scanner
    private String codigoBarrasPedido;

    private String nombreCliente;
    private String telefono;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEntrega;

    private String bloqueHorario; // "TARDE" o "NOCHE"

    private String estado; // "PENDIENTE", "ENTREGADO", "PAGADO", "CANCELADO"

    @ManyToOne
    @JoinColumn(name = "torta_id")
    private Torta torta;

    // ESTO ES LO QUE NECESITABAS: Quién hizo el pedido
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario empleado;

    private String detalleTamano;
    private Integer cantidad;

    // LÓGICA DE DINERO
    private Double precioTotal;
    private Double montoAbonado;
    private Double saldoPendiente;

    @Column(length = 500)
    private String notas;

    // AUDITORÍA: Para saber exactamente cuándo se creó el movimiento
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        // Generar código de barras automático si no existe
        if (this.codigoBarrasPedido == null) {
            this.codigoBarrasPedido = "PED-" + System.currentTimeMillis();
        }
        calcularSaldoYEstado();
    }

    @PreUpdate
    public void preUpdate() {
        calcularSaldoYEstado();
    }

    private void calcularSaldoYEstado() {
        if (this.precioTotal != null) {
            double abono = (this.montoAbonado != null) ? this.montoAbonado : 0.0;
            this.saldoPendiente = this.precioTotal - abono;

            // Si el saldo es 0 y el estado era pendiente, lo pasamos a PAGADO
            if (this.saldoPendiente <= 0 && "PENDIENTE".equals(this.estado)) {
                this.estado = "PAGADO";
            }
        }
    }
}