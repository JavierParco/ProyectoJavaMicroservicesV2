package com.tcs.microservice_api.controller;

import com.tcs.microservice_api.dto.MovimientoDTO;
import com.tcs.microservice_api.dto.MovimientoRequestDTO;
import com.tcs.microservice_api.service.IMovimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final IMovimientoService movimientoService;

    @Autowired
    public MovimientoController(IMovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> registrarMovimiento(@Valid @RequestBody MovimientoRequestDTO movimientoRequestDTO) {
        MovimientoDTO nuevoMovimiento = movimientoService.registrarMovimiento(movimientoRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoMovimiento.getId())
                .toUri();
        return ResponseEntity.created(location).body(nuevoMovimiento);
    }

    // Nuevo método para GET /movimientos
    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> obtenerTodosLosMovimientos() {
        List<MovimientoDTO> movimientos = movimientoService.obtenerTodosLosMovimientos();
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtenerMovimientoPorId(@PathVariable Long id) {
        return movimientoService.obtenerMovimientoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    public ResponseEntity<List<MovimientoDTO>> obtenerMovimientosPorNumeroCuenta(@PathVariable String numeroCuenta) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerMovimientosPorNumeroCuenta(numeroCuenta);
        return ResponseEntity.ok(movimientos);
    }
}
