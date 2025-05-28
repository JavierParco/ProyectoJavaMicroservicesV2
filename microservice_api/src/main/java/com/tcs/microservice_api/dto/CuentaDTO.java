package com.tcs.microservice_api.dto;

import com.tcs.microservice_api.entity.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaDTO {
    private Long id;

    @NotBlank(message = "El número de cuenta no puede estar vacío")
    private String numeroCuenta;

    @NotNull(message = "El tipo de cuenta no puede ser nulo")
    private TipoCuenta tipoCuenta;

    @NotNull(message = "El saldo inicial no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo inicial no puede ser negativo")
    private BigDecimal saldoInicial;

    private BigDecimal saldoActual; // Se incluirá en respuestas, no necesariamente en requests de creación

    @NotNull(message = "El estado no puede ser nulo")
    private Boolean estado;

    @NotNull(message = "El ID del cliente (dueño de la cuenta) no puede ser nulo para crear una cuenta")
    private String clienteId; // Se usará para asociar la cuenta al cliente (usando el clienteId de negocio)
}
