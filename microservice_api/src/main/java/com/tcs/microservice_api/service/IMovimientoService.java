package com.tcs.microservice_api.service;

import com.tcs.microservice_api.dto.MovimientoDTO;
import com.tcs.microservice_api.dto.MovimientoRequestDTO;
import com.tcs.microservice_api.dto.ReporteMovimientosDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Asegúrate de que este import esté presente

public interface IMovimientoService {
    MovimientoDTO registrarMovimiento(MovimientoRequestDTO movimientoRequestDTO);
    Optional<MovimientoDTO> obtenerMovimientoPorId(Long id);
    List<MovimientoDTO> obtenerMovimientosPorNumeroCuenta(String numeroCuenta);
    List<ReporteMovimientosDTO> generarReporteMovimientos(String clienteId, LocalDate fechaInicio, LocalDate fechaFin);
    List<MovimientoDTO> obtenerTodosLosMovimientos();
}