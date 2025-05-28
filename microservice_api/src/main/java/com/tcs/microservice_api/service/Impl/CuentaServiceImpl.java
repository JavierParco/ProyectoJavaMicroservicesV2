package com.tcs.microservice_api.service.Impl;

import com.tcs.microservice_api.exception.DuplicateResourceException;
import com.tcs.microservice_api.exception.InvalidInputException;
import com.tcs.microservice_api.exception.ResourceNotFoundException;
import com.tcs.microservice_api.dto.CuentaDTO;
import com.tcs.microservice_api.entity.Cliente;
import com.tcs.microservice_api.entity.Cuenta;
import com.tcs.microservice_api.repository.ClienteRepository;
import com.tcs.microservice_api.repository.CuentaRepository;
import com.tcs.microservice_api.service.ICuentaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CuentaServiceImpl implements ICuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public CuentaServiceImpl(CuentaRepository cuentaRepository, ClienteRepository clienteRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {
        log.info("Intentando crear cuenta con número: {} para clienteId: {}", cuentaDTO.getNumeroCuenta(), cuentaDTO.getClienteId());
        if (cuentaDTO.getClienteId() == null || cuentaDTO.getClienteId().isBlank()) {
            log.warn("Intento de crear cuenta sin clienteId.");
            throw new InvalidInputException("El clienteId es obligatorio para crear una cuenta.");
        }

        Cliente cliente = clienteRepository.findByClienteId(cuentaDTO.getClienteId())
                .orElseThrow(() -> {
                    log.warn("Cliente con clienteId {} no encontrado al intentar crear cuenta.", cuentaDTO.getClienteId());
                    return new ResourceNotFoundException("Cliente con clienteId '" + cuentaDTO.getClienteId() + "' no encontrado.");
                });

        if (cuentaRepository.findByNumeroCuenta(cuentaDTO.getNumeroCuenta()).isPresent()) {
            log.warn("Número de cuenta {} ya existe.", cuentaDTO.getNumeroCuenta());
            throw new DuplicateResourceException("Cuenta con número '" + cuentaDTO.getNumeroCuenta() + "' ya existe.");
        }

        Cuenta cuenta = mapToEntity(cuentaDTO, cliente);
        cuenta.setCliente(cliente);
        cuenta.setSaldoActual(cuenta.getSaldoInicial());

        Cuenta nuevaCuenta = cuentaRepository.save(cuenta);
        log.info("Cuenta {} creada exitosamente para el cliente {}.", nuevaCuenta.getNumeroCuenta(), cliente.getClienteId());
        return mapToDTO(nuevaCuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CuentaDTO> obtenerCuentaPorNumero(String numeroCuenta) {
        log.debug("Buscando cuenta por número: {}", numeroCuenta);
        return cuentaRepository.findByNumeroCuenta(numeroCuenta).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerCuentasPorClienteId(String clienteId) {
        log.debug("Buscando cuentas para clienteId: {}", clienteId);
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con clienteId '" + clienteId + "' no encontrado."));

        return cuentaRepository.findByClienteId(cliente.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerTodasLasCuentas() {
        log.debug("Obteniendo todas las cuentas");
        return cuentaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CuentaDTO actualizarCuenta(String numeroCuenta, CuentaDTO cuentaDTO) {
        log.info("Intentando actualizar cuenta con número: {}", numeroCuenta);
        Cuenta cuentaExistente = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> {
                    log.warn("Cuenta con número {} no encontrada para actualizar.", numeroCuenta);
                    return new ResourceNotFoundException("Cuenta con número '" + numeroCuenta + "' no encontrada.");
                });

        if (cuentaDTO.getTipoCuenta() != null) {
            cuentaExistente.setTipoCuenta(cuentaDTO.getTipoCuenta());
        }
        if (cuentaDTO.getEstado() != null) {
            cuentaExistente.setEstado(cuentaDTO.getEstado());
        }

        Cuenta cuentaActualizada = cuentaRepository.save(cuentaExistente);
        log.info("Cuenta {} actualizada exitosamente.", numeroCuenta);
        return mapToDTO(cuentaActualizada);
    }

    @Override
    @Transactional
    public void eliminarCuenta(String numeroCuenta) {
        log.info("Intentando eliminar cuenta con número: {}", numeroCuenta);
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> {
                    log.warn("Cuenta con número {} no encontrada para eliminar.", numeroCuenta);
                    return new ResourceNotFoundException("Cuenta con número '" + numeroCuenta + "' no encontrada.");
                });

        cuentaRepository.delete(cuenta);
        log.info("Cuenta {} eliminada exitosamente.", numeroCuenta);
    }

    private CuentaDTO mapToDTO(Cuenta cuenta) {
        if (cuenta == null) return null;
        return CuentaDTO.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .saldoActual(cuenta.getSaldoActual())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getCliente() != null ? cuenta.getCliente().getClienteId() : null)
                .build();
    }

    private Cuenta mapToEntity(CuentaDTO dto, Cliente cliente) {
        if (dto == null) return null;
        return Cuenta.builder()
                .numeroCuenta(dto.getNumeroCuenta())
                .tipoCuenta(dto.getTipoCuenta())
                .saldoInicial(dto.getSaldoInicial())
                .estado(dto.getEstado() != null ? dto.getEstado() : true)
                .cliente(cliente)
                .build();
    }
}
