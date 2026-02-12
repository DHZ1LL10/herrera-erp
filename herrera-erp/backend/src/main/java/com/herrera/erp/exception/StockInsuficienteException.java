package com.herrera.erp.exception;

import java.math.BigDecimal;

/**
 * Excepción para cuando no hay stock suficiente
 */
public class StockInsuficienteException extends RuntimeException {

    private final Long materialId;
    private final BigDecimal stockDisponible;
    private final BigDecimal cantidadRequerida;

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
        this.materialId = null;
        this.stockDisponible = null;
        this.cantidadRequerida = null;
    }

    public StockInsuficienteException(Long materialId, BigDecimal stockDisponible, BigDecimal cantidadRequerida) {
        super(String.format("Stock insuficiente. Disponible: %s, Requerido: %s",
                stockDisponible, cantidadRequerida));
        this.materialId = materialId;
        this.stockDisponible = stockDisponible;
        this.cantidadRequerida = cantidadRequerida;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public BigDecimal getStockDisponible() {
        return stockDisponible;
    }

    public BigDecimal getCantidadRequerida() {
        return cantidadRequerida;
    }
}
