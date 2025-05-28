package com.tcs.microservice_api.repository;

import com.tcs.microservice_api.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaId(Long cuentaId);
    List<Movimiento> findByCuentaNumeroCuenta(String numeroCuenta);

    // Para el reporte F4
    @Query("SELECT m FROM Movimiento m JOIN m.cuenta c JOIN c.cliente cl " +
            "WHERE cl.clienteId = :clienteId AND m.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY m.fecha ASC")
    List<Movimiento> findMovimientosByClienteAndFechaRange(
            @Param("clienteId") String clienteId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // Para obtener el último movimiento de una cuenta, útil para obtener el saldo más reciente si no se confía solo en Cuenta.saldoActual
    Optional<Movimiento> findTopByCuentaIdOrderByFechaDesc(Long cuentaId);
}