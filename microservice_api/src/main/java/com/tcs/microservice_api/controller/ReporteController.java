package com.tcs.microservice_api.controller;

import com.tcs.microservice_api.dto.ReporteMovimientosDTO;
import com.tcs.microservice_api.service.IMovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final IMovimientoService movimientoService;

    @Autowired
    public ReporteController(IMovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public ResponseEntity<List<ReporteMovimientosDTO>> generarReporte(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        if (fechaInicio.isAfter(fechaFin)) {
            throw new com.tcs.microservice_api.exception.InvalidInputException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        List<ReporteMovimientosDTO> reporte = movimientoService.generarReporteMovimientos(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}
