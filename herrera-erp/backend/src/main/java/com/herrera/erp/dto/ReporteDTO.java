package com.herrera.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO genérico para reportes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {

    private String titulo;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Object datos; // Flexible para diferentes tipos de reportes

    // Específicos para reportes de inventario
    private Integer totalMateriales;
    private Integer materialesCriticos;
    private Integer materialesAlerta;
    private BigDecimal valorTotalInventario;

    // Específicos para reportes de pedidos
    private Integer totalPedidos;
    private Integer pedidosPendientes;
    private Integer pedidosRetrasados;
    private Integer pedidosEntregados;

    // Específicos para reportes de ventas
    private BigDecimal totalVentas;
    private Integer numeroVentas;
    private BigDecimal promedioVenta;
}
