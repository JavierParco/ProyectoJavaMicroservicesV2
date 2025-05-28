package com.tcs.microservice_api.service.Impl;

import com.tcs.microservice_api.exception.DuplicateResourceException;
import com.tcs.microservice_api.exception.ResourceNotFoundException;
import com.tcs.microservice_api.dto.ClienteDTO;
import com.tcs.microservice_api.entity.Cliente;
import com.tcs.microservice_api.repository.ClienteRepository;
import com.tcs.microservice_api.repository.PersonaRepository;
import com.tcs.microservice_api.service.IClienteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository, PersonaRepository personaRepository) {
        this.clienteRepository = clienteRepository;
        this.personaRepository = personaRepository;
    }

    @Override
    @Transactional
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        log.info("Intentando crear cliente con clienteId: {} e identificación: {}", clienteDTO.getClienteId(), clienteDTO.getIdentificacion());

        if (clienteRepository.findByClienteId(clienteDTO.getClienteId()).isPresent()) {
            log.warn("ClienteId {} ya existe.", clienteDTO.getClienteId());
            throw new DuplicateResourceException("Cliente con clienteId '" + clienteDTO.getClienteId() + "' ya existe.");
        }

        if (personaRepository.findByIdentificacion(clienteDTO.getIdentificacion()).isPresent()) {
            log.warn("Identificación {} ya existe.", clienteDTO.getIdentificacion());
            throw new DuplicateResourceException("Persona con identificación '" + clienteDTO.getIdentificacion() + "' ya existe.");
        }

        Cliente cliente = mapToEntity(clienteDTO);
        Cliente nuevoCliente = clienteRepository.save(cliente);
        log.info("Cliente creado exitosamente con ID: {}", nuevoCliente.getId());
        return mapToDTO(nuevoCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> obtenerClientePorId(Long id) {
        log.debug("Buscando cliente por ID de entidad: {}", id);
        return clienteRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> obtenerClientePorClienteId(String clienteId) {
        log.debug("Buscando cliente por clienteId: {}", clienteId);
        return clienteRepository.findByClienteId(clienteId).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerTodosLosClientes() {
        log.debug("Obteniendo todos los clientes");
        return clienteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClienteDTO actualizarCliente(String clienteIdNegocio, ClienteDTO clienteDTO) {
        log.info("Intentando actualizar cliente con clienteId: {}", clienteIdNegocio);
        Cliente clienteExistente = clienteRepository.findByClienteId(clienteIdNegocio)
                .orElseThrow(() -> {
                    log.warn("Cliente con clienteId {} no encontrado para actualizar.", clienteIdNegocio);
                    return new ResourceNotFoundException("Cliente con clienteId '" + clienteIdNegocio + "' no encontrado.");
                });

        if (clienteDTO.getIdentificacion() != null && !clienteDTO.getIdentificacion().equals(clienteExistente.getIdentificacion())) {
            personaRepository.findByIdentificacion(clienteDTO.getIdentificacion()).ifPresent(p -> {
                if (!p.getId().equals(clienteExistente.getId())) {
                    log.warn("Intento de actualizar a una identificación {} que ya existe para otra persona.", clienteDTO.getIdentificacion());
                    throw new DuplicateResourceException("Identificación '" + clienteDTO.getIdentificacion() + "' ya está en uso por otra persona.");
                }
            });
            clienteExistente.setIdentificacion(clienteDTO.getIdentificacion());
        }

        clienteExistente.setNombre(clienteDTO.getNombre() != null ? clienteDTO.getNombre() : clienteExistente.getNombre());
        clienteExistente.setGenero(clienteDTO.getGenero() != null ? clienteDTO.getGenero() : clienteExistente.getGenero());
        clienteExistente.setEdad(clienteDTO.getEdad() != null ? clienteDTO.getEdad() : clienteExistente.getEdad());
        clienteExistente.setDireccion(clienteDTO.getDireccion() != null ? clienteDTO.getDireccion() : clienteExistente.getDireccion());
        clienteExistente.setTelefono(clienteDTO.getTelefono() != null ? clienteDTO.getTelefono() : clienteExistente.getTelefono());

        if (clienteDTO.getContrasena() != null && !clienteDTO.getContrasena().isEmpty()) {
            clienteExistente.setContrasena(clienteDTO.getContrasena());
        }
        clienteExistente.setEstado(clienteDTO.getEstado() != null ? clienteDTO.getEstado() : clienteExistente.getEstado());

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        log.info("Cliente {} actualizado exitosamente.", clienteIdNegocio);
        return mapToDTO(clienteActualizado);
    }

    @Override
    @Transactional
    public void eliminarCliente(String clienteId) {
        log.info("Intentando eliminar cliente con clienteId: {}", clienteId);
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> {
                    log.warn("Cliente con clienteId {} no encontrado para eliminar.", clienteId);
                    return new ResourceNotFoundException("Cliente con clienteId '" + clienteId + "' no encontrado.");
                });
        clienteRepository.delete(cliente);
        log.info("Cliente {} eliminado exitosamente.", clienteId);
    }

    private ClienteDTO mapToDTO(Cliente cliente) {
        if (cliente == null) return null;
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .clienteId(cliente.getClienteId())
                .contrasena("********")
                .estado(cliente.getEstado())
                .build();
    }

    private Cliente mapToEntity(ClienteDTO dto) {
        if (dto == null) return null;
        return Cliente.builder()
                .nombre(dto.getNombre())
                .genero(dto.getGenero())
                .edad(dto.getEdad())
                .identificacion(dto.getIdentificacion())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .clienteId(dto.getClienteId())
                .contrasena(dto.getContrasena())
                .estado(dto.getEstado() != null ? dto.getEstado() : true)
                .build();
    }
}
