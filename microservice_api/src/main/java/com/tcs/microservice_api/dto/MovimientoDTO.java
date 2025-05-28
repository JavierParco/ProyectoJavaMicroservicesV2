package com.tcs.microservice_api.dto;

import com.tcs.microservice_api.entity.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoDTO {
    private Long id;
    private LocalDateTime fecha;

    @NotNull(message = "El tipo de movimiento no puede ser nulo")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "El valor del movimiento no puede ser nulo")
    private BigDecimal valor;

    private BigDecimal saldoPostMovimiento; // Saldo después del movimiento

    @NotBlank(message = "El número de cuenta para el movimiento no puede estar vacío")
    private String numeroCuenta; // Para identificar la cuenta a la que pertenece el movimiento
}