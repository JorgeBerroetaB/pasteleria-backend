package com.pasteleria.cakeapi.repositories;

import com.pasteleria.cakeapi.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Importante

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(String estado);

    List<Pedido> findByFechaEntrega(LocalDate fecha);

    @Query("SELECT DISTINCT p.fechaEntrega FROM Pedido p WHERE YEAR(p.fechaEntrega) = :anio AND MONTH(p.fechaEntrega) = :mes")
    List<LocalDate> findFechasOcupadasDelMes(@Param("anio") int anio, @Param("mes") int mes);

    // NUEVO: Buscar pedidos agendados por su código escaneable (Ej: "PED-12")
    Optional<Pedido> findByCodigoBarrasPedido(String codigo);
}