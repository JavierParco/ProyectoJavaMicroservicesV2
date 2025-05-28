package com.tcs.microservice_api.entity;

public enum TipoCuenta {
    AHORRO("Ahorro"),
    CORRIENTE("Corriente");

    private final String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
