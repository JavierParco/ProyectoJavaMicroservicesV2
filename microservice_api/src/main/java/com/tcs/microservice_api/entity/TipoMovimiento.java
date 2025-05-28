package com.tcs.microservice_api.entity;

public enum TipoMovimiento {
    DEBITO("Debito"),   // Representa un retiro o un gasto
    CREDITO("Credito"); // Representa un depósito o un ingreso

    private final String descripcion;

    TipoMovimiento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
