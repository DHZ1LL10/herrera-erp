package com.herrera.erp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para crear o actualizar costos de un pedido
 * Incluye validaciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarCostoRequest {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @Min(value = 0, message = "El costo de tela no puede ser negativo")
    @Builder.Default
    private BigDecimal costoTela = BigDecimal.ZERO;

    @Min(value = 0, message = "El costo de vinil no puede ser negativo")
    @Builder.Default
    private BigDecimal costoVinil = BigDecimal.ZERO;

    @Min(value = 0, message = "El costo de hilo no puede ser negativo")
    @Builder.Default
    private BigDecimal costoHilo = BigDecimal.ZERO;

    @Min(value = 0, message = "El costo de maquila no puede ser negativo")
    @Builder.Default
    private BigDecimal costoMaquila = BigDecimal.ZERO;

    @Min(value = 0, message = "El costo de varios no puede ser negativo")
    @Builder.Default
    private BigDecimal costoVarios = BigDecimal.ZERO;

    @NotNull(message = "El precio de venta es obligatorio")
    @Min(value = 0, message = "El precio de venta no puede ser negativo")
    private BigDecimal precioVenta;

    private String notas;
}
