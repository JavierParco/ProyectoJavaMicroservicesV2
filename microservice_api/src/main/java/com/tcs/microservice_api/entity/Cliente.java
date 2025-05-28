package com.tcs.microservice_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "id") // Especifica la columna de unión para la herencia
@Data
@EqualsAndHashCode(callSuper = true) // Incluye campos de la superclase en equals y hashCode
@ToString(callSuper = true) // Incluye campos de la superclase en toString
@NoArgsConstructor
@SuperBuilder // Para usar el patrón builder
public class Cliente extends Persona {

    @NotBlank(message = "El clienteId no puede estar vacío")
    @Size(max = 100, message = "El clienteId no puede exceder los 100 caracteres")
    @Column(name = "cliente_id", unique = true, nullable = false, length = 100)
    private String clienteId; // Este es el ID de negocio del cliente

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres") // Ajustar según política
    @Column(nullable = false)
    private String contrasena; // TODO: Hashear en un entorno real

    @NotNull(message = "El estado no puede ser nulo")
    @Column(nullable = false)
    private Boolean estado = true; // Por defecto true

    // Relación uno a muchos con Cuenta
    // Un cliente puede tener varias cuentas (Corriente, Ahorro)
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas;
}
