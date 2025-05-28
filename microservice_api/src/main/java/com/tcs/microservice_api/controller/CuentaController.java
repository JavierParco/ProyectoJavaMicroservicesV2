package com.tcs.microservice_api.controller;

import com.tcs.microservice_api.dto.CuentaDTO;
import com.tcs.microservice_api.service.ICuentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final ICuentaService cuentaService;

    @Autowired
    public CuentaController(ICuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    public ResponseEntity<CuentaDTO> crearCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO nuevaCuenta = cuentaService.crearCuenta(cuentaDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{numeroCuenta}")
                .buildAndExpand(nuevaCuenta.getNumeroCuenta())
                .toUri();
        return ResponseEntity.created(location).body(nuevaCuenta);
    }

    @GetMapping
    public ResponseEntity<List<CuentaDTO>> obtenerTodasLasCuentas() {
        List<CuentaDTO> cuentas = cuentaService.obtenerTodasLasCuentas();
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaDTO>> obtenerCuentasPorClienteId(@PathVariable String clienteId) {
        List<CuentaDTO> cuentas = cuentaService.obtenerCuentasPorClienteId(clienteId);
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> obtenerCuentaPorNumero(@PathVariable String numeroCuenta) {
        return cuentaService.obtenerCuentaPorNumero(numeroCuenta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> actualizarCuenta(@PathVariable String numeroCuenta, @Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO cuentaActualizada = cuentaService.actualizarCuenta(numeroCuenta, cuentaDTO);
        return ResponseEntity.ok(cuentaActualizada);
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable String numeroCuenta) {
        cuentaService.eliminarCuenta(numeroCuenta);
        return ResponseEntity.noContent().build();
    }
}

