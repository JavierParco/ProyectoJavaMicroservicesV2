package com.tcs.microservice_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteMovimientosDTO {
    private LocalDateTime fecha;
    private String cliente; // Nombre del cliente
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldoInicialCuenta;
    private Boolean estadoCuenta;
    private String tipoMovimiento; // Debito o Credito
    private BigDecimal valorMovimiento;
    private BigDecimal saldoDisponibleCuenta;
}
