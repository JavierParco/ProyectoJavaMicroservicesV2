package com.tcs.microservice_api.service.Impl;

import com.tcs.microservice_api.exception.InvalidInputException;
import com.tcs.microservice_api.exception.ResourceNotFoundException;
import com.tcs.microservice_api.exception.SaldoInsuficienteException;
import com.tcs.microservice_api.dto.MovimientoDTO;
import com.tcs.microservice_api.dto.MovimientoRequestDTO;
import com.tcs.microservice_api.dto.ReporteMovimientosDTO;
import com.tcs.microservice_api.entity.*;
import com.tcs.microservice_api.repository.ClienteRepository;
import com.tcs.microservice_api.repository.CuentaRepository;
import com.tcs.microservice_api.repository.MovimientoRepository;
import com.tcs.microservice_api.service.IMovimientoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovimientoServiceImpl implements IMovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    private static final BigDecimal LIMITE_DEBITO_DIARIO = new BigDecimal("1000.00");

    @Autowired
    public MovimientoServiceImpl(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository, ClienteRepository clienteRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public MovimientoDTO registrarMovimiento(MovimientoRequestDTO requestDTO) {
        log.info("Registrando movimiento tipo {} de valor {} para cuenta {}", requestDTO.getTipoMovimiento(), requestDTO.getValor(), requestDTO.getNumeroCuenta());

        if (requestDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Valor de movimiento no positivo: {}", requestDTO.getValor());
            throw new InvalidInputException("El valor del movimiento debe ser positivo.");
        }

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(requestDTO.getNumeroCuenta())
                .orElseThrow(() -> {
                    log.warn("Cuenta {} no encontrada para registrar movimiento.", requestDTO.getNumeroCuenta());
                    return new ResourceNotFoundException("Cuenta con número '" + requestDTO.getNumeroCuenta() + "' no encontrada.");
                });

        if (!cuenta.getEstado()) {
            log.warn("Intento de movimiento en cuenta {} inactiva.", cuenta.getNumeroCuenta());
            throw new InvalidInputException("La cuenta '" + cuenta.getNumeroCuenta() + "' está inactiva.");
        }

        BigDecimal valorMovimiento = requestDTO.getValor();
        BigDecimal saldoActual = cuenta.getSaldoActual();
        BigDecimal nuevoSaldo;

        if (requestDTO.getTipoMovimiento() == TipoMovimiento.DEBITO) {
            BigDecimal totalDebitadoHoy = calcularTotalDebitadoHoy(cuenta.getId(), LocalDate.now());
            if (totalDebitadoHoy.add(valorMovimiento).compareTo(LIMITE_DEBITO_DIARIO) > 0) {
                log.warn("Límite de débito diario excedido para cuenta {}. Total debitado hoy: {}, intento de debitar: {}", cuenta.getNumeroCuenta(), totalDebitadoHoy, valorMovimiento);
                throw new SaldoInsuficienteException("Cupo diario Excedido. Límite: " + LIMITE_DEBITO_DIARIO + ", ya debitado hoy: " + totalDebitadoHoy);
            }

            if (saldoActual.compareTo(valorMovimiento) < 0) {
                log.warn("Saldo no disponible en cuenta {}. Saldo actual: {}, intento de débito: {}", cuenta.getNumeroCuenta(), saldoActual, valorMovimiento);
                throw new SaldoInsuficienteException("Saldo no disponible");
            }
            nuevoSaldo = saldoActual.subtract(valorMovimiento);
        } else { // CREDITO
            nuevoSaldo = saldoActual.add(valorMovimiento);
        }

        cuenta.setSaldoActual(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(requestDTO.getTipoMovimiento())
                .valor(valorMovimiento)
                .saldoPostMovimiento(nuevoSaldo)
                .cuenta(cuenta)
                .build();

        Movimiento nuevoMovimiento = movimientoRepository.save(movimiento);
        log.info("Movimiento ID {} registrado exitosamente para cuenta {}. Nuevo saldo: {}", nuevoMovimiento.getId(), cuenta.getNumeroCuenta(), nuevoSaldo);
        return mapToDTO(nuevoMovimiento);
    }

    private BigDecimal calcularTotalDebitadoHoy(Long cuentaId, LocalDate fecha) {
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = fecha.atTime(LocalTime.MAX);

        List<Movimiento> debitosHoy = movimientoRepository.findAll().stream()
                .filter(m -> m.getCuenta().getId().equals(cuentaId) &&
                        m.getTipoMovimiento() == TipoMovimiento.DEBITO &&
                        !m.getFecha().isBefore(inicioDelDia) &&
                        !m.getFecha().isAfter(finDelDia))
                .collect(Collectors.toList());

        return debitosHoy.stream()
                .map(Movimiento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<MovimientoDTO> obtenerMovimientoPorId(Long id) {
        log.debug("Buscando movimiento por ID: {}", id);
        return movimientoRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerMovimientosPorNumeroCuenta(String numeroCuenta) {
        log.debug("Buscando movimientos para cuenta: {}", numeroCuenta);
        if (!cuentaRepository.findByNumeroCuenta(numeroCuenta).isPresent()){
            throw new ResourceNotFoundException("Cuenta con número '" + numeroCuenta + "' no encontrada.");
        }
        return movimientoRepository.findByCuentaNumeroCuenta(numeroCuenta).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteMovimientosDTO> generarReporteMovimientos(String clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Generando reporte de movimientos para clienteId: {} entre {} y {}", clienteId, fechaInicio, fechaFin);

        clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con clienteId '" + clienteId + "' no encontrado."));

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Movimiento> movimientos = movimientoRepository.findMovimientosByClienteAndFechaRange(clienteId, inicio, fin);

        if (movimientos.isEmpty()) {
            log.info("No se encontraron movimientos para el reporte del cliente {} en el rango de fechas especificado.", clienteId);
        }

        return movimientos.stream()
                .map(this::mapToReporteDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerTodosLosMovimientos() { // Implementación del nuevo método
        log.debug("Obteniendo todos los movimientos");
        return movimientoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Mappers (sin cambios)
    private MovimientoDTO mapToDTO(Movimiento movimiento) {
        if (movimiento == null) return null;
        return MovimientoDTO.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldoPostMovimiento(movimiento.getSaldoPostMovimiento())
                .numeroCuenta(movimiento.getCuenta() != null ? movimiento.getCuenta().getNumeroCuenta() : null)
                .build();
    }

    private ReporteMovimientosDTO mapToReporteDTO(Movimiento movimiento) {
        if (movimiento == null || movimiento.getCuenta() == null || movimiento.getCuenta().getCliente() == null) {
            return null;
        }
        Cuenta cuenta = movimiento.getCuenta();
        Cliente cliente = cuenta.getCliente();

        BigDecimal saldoInicialParaEsteMovimiento;
        if (movimiento.getTipoMovimiento() == TipoMovimiento.CREDITO) {
            saldoInicialParaEsteMovimiento = movimiento.getSaldoPostMovimiento().subtract(movimiento.getValor());
        } else { // DEBITO
            saldoInicialParaEsteMovimiento = movimiento.getSaldoPostMovimiento().add(movimiento.getValor());
        }

        return ReporteMovimientosDTO.builder()
                .fecha(movimiento.getFecha())
                .cliente(cliente.getNombre())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta().getDescripcion())
                .saldoInicialCuenta(saldoInicialParaEsteMovimiento)
                .estadoCuenta(cuenta.getEstado())
                .tipoMovimiento(movimiento.getTipoMovimiento().getDescripcion())
                .valorMovimiento(movimiento.getValor())
                .saldoDisponibleCuenta(movimiento.getSaldoPostMovimiento())
                .build();
    }
}
