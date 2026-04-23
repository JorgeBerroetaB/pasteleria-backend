package com.pasteleria.cakeapi.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único para el scanner (Ej: "PED-00123")
    // Se puede generar automáticamente uniendo "PED-" + id
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

    private String detalleTamano;
    private Integer cantidad;

    // LÓGICA DE DINERO
    private Double precioTotal;    // Precio total de la torta
    private Double montoAbonado;   // Lo que dejó el cliente al agendar
    private Double saldoPendiente; // Lo que la caja debe cobrar (PrecioTotal - Abono)

    @Column(length = 500)
    private String notas;

    // Método para calcular el saldo automáticamente antes de guardar
    @PrePersist
    @PreUpdate
    public void calcularSaldo() {
        if (this.precioTotal != null) {
            double abono = (this.montoAbonado != null) ? this.montoAbonado : 0.0;
            this.saldoPendiente = this.precioTotal - abono;

            // Si el saldo es 0, podríamos marcarlo como pagado automáticamente
            if (this.saldoPendiente <= 0) {
                this.estado = "PAGADO";
            }
        }
    }
}