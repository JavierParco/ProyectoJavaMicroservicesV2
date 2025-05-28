package com.tcs.microservice_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de cuenta no puede estar vacío")
    @Size(max = 50, message = "El número de cuenta no puede exceder los 50 caracteres")
    @Column(name = "numero_cuenta", unique = true, nullable = false, length = 50)
    private String numeroCuenta;

    @NotNull(message = "El tipo de cuenta no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false, length = 20)
    private TipoCuenta tipoCuenta;

    @NotNull(message = "El saldo inicial no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo inicial no puede ser negativo")
    @Column(name = "saldo_inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoInicial;

    @NotNull(message = "El saldo actual no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El saldo actual debe ser mayor o igual a cero, a menos que se permitan sobregiros (no implementado).")
    @Column(name = "saldo_actual", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoActual;


    @NotNull(message = "El estado no puede ser nulo")
    @Column(nullable = false)
    private Boolean estado = true; // Por defecto true

    // Relación muchos a uno con Cliente
    // varias cuentas pueden pertenecer a un cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id_fk", referencedColumnName = "id", nullable = false)
    @ToString.Exclude // Evitar recursión en toString con Cliente
    private Cliente cliente;

    // Relación uno a muchos con Movimiento
    // Una cuenta puede tener muchos movimientos
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude // Evitar recursión en toString con Movimiento
    private List<Movimiento> movimientos;
}