package com.tcs.microservice_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder; // Para constructores en herencia

@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia de herencia
@Data
@NoArgsConstructor
@SuperBuilder // Permite usar el patrón builder en clases hijas
public abstract class Persona { // Clase abstracta base

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    @Column(nullable = false)
    private String nombre;

    @Size(max = 50, message = "El género no puede exceder los 50 caracteres")
    private String genero;

    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @NotBlank(message = "La identificación no puede estar vacía")
    @Size(max = 20, message = "La identificación no puede exceder los 20 caracteres")
    @Column(unique = true, nullable = false, length = 20)
    private String identificacion;

    @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String telefono;
}
