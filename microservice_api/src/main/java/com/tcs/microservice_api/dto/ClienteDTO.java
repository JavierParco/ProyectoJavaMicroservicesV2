package com.tcs.microservice_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private Long id; // ID de Persona

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    private String genero;

    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @NotBlank(message = "La identificación no puede estar vacía")
    private String identificacion;

    private String direccion;

    private String telefono;

    @NotBlank(message = "El clienteId no puede estar vacío")
    private String clienteId; // ID de negocio del cliente

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String contrasena;

    @NotNull(message = "El estado no puede ser nulo")
    private Boolean estado;
}
