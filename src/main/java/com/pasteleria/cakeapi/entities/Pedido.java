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

    private String bloqueHorario; // "MAÑANA" o "TARDE"

    private String estado; // "PENDIENTE", "ENTREGADO", "PAGADO", "CANCELADO"

    @ManyToOne
    @JoinColumn(name = "torta_id")
    private Torta torta;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario empleado;

    private String detalleTamano;
    private Integer cantidad;

    // --- NUEVO CAMPO AGREGADO ---
    private String tipoCobertura;

    // LÓGICA DE DINERO
    private Double precioTotal;
    private Double montoAbonado;
    private Double saldoPendiente;

    @Column(length = 500)
    private String notas;

    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
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

            if (this.saldoPendiente <= 0 && "PENDIENTE".equals(this.estado)) {
                this.estado = "PAGADO";
            }
        }
    }
}