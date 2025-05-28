package com.tcs.microservice_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha no puede ser nula")
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fecha;

    @NotNull(message = "El tipo de movimiento no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 10)
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "El valor del movimiento no puede ser nulo")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor; // Puede ser positivo (crédito) o negativo (débito) en la lógica de negocio, pero aquí se almacena el valor absoluto y el tipo indica la operación.

    @NotNull(message = "El saldo post movimiento no puede ser nulo")
    @Column(name = "saldo_post_movimiento", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoPostMovimiento; // Saldo de la cuenta DESPUÉS de este movimiento

    // Relación muchos a uno con Cuenta
    // Muchos movimientos pueden pertenecer a una cuenta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id_fk", referencedColumnName = "id", nullable = false)
    @ToString.Exclude // Evitar recursión en toString con Cuenta
    private Cuenta cuenta;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}