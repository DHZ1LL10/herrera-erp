package com.herrera.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferir información de costos de pedidos
 * Incluye todos los datos calculados y relacionados
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostoPedidoDTO {

    private Long id;

    // Información del pedido relacionado
    private Long pedidoId;
    private String folioPedido;
    private String nombrePedido;
    private String clienteNombre;

    // Costos individuales
    private BigDecimal costoTela;
    private BigDecimal costoVinil;
    private BigDecimal costoHilo;
    private BigDecimal costoMaquila;
    private BigDecimal costoVarios;

    // Totales calculados
    private BigDecimal totalCosto;
    private BigDecimal precioVenta;
    private BigDecimal utilidad;
    private BigDecimal margenPorcentaje;

    // Indicadores
    private Boolean esRentable;
    private String nivelAlerta; // EXCELENTE, NORMAL, BAJO, PERDIDA, SIN_DATOS

    // Notas y metadatos
    private String notas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
