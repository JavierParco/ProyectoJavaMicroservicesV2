package com.tcs.microservice_api.service;

import com.tcs.microservice_api.dto.CuentaDTO;
import java.util.List;
import java.util.Optional;

public interface ICuentaService {
    CuentaDTO crearCuenta(CuentaDTO cuentaDTO);
    Optional<CuentaDTO> obtenerCuentaPorNumero(String numeroCuenta);
    List<CuentaDTO> obtenerCuentasPorClienteId(String clienteId);
    List<CuentaDTO> obtenerTodasLasCuentas();
    CuentaDTO actualizarCuenta(String numeroCuenta, CuentaDTO cuentaDTO);
    void eliminarCuenta(String numeroCuenta);
}

