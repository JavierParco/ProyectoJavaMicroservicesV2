package com.tcs.microservice_api.repository;

import com.tcs.microservice_api.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByClienteId(String clienteId);
    Optional<Cliente> findByIdentificacion(String identificacion); // Heredado de Persona, útil para buscar
}
