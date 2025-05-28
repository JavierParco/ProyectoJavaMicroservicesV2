package com.tcs.microservice_api.service;

import com.tcs.microservice_api.dto.ClienteDTO;
import java.util.List;
import java.util.Optional;

public interface IClienteService {
    ClienteDTO crearCliente(ClienteDTO clienteDTO);
    Optional<ClienteDTO> obtenerClientePorId(Long id); // ID de la entidad Persona/Cliente
    Optional<ClienteDTO> obtenerClientePorClienteId(String clienteId); // ID de negocio
    List<ClienteDTO> obtenerTodosLosClientes();
    ClienteDTO actualizarCliente(String clienteId, ClienteDTO clienteDTO);
    void eliminarCliente(String clienteId);
}
