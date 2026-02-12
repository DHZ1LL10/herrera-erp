package com.herrera.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para reportes de utilidad y rentabilidad
 * Contiene estadísticas agregadas de un periodo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteUtilidadDTO {

    // Rango del reporte
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Totales agregados
    private BigDecimal totalVentas;
    private BigDecimal totalCostos;
    private BigDecimal utilidadTotal;
    private BigDecimal margenPromedio;

    // Estadísticas de pedidos (contadores)
    private Integer totalPedidos;
    private Integer pedidosRentables; // Contador de pedidos con utilidad positiva
    private Integer pedidosConPerdidaCount; // Contador de pedidos con pérdida
    private Integer pedidosSinCostos;

    // Detalle de pedidos destacados (listas)
    private List<CostoPedidoDTO> topPedidosRentables;
    private List<CostoPedidoDTO> listaPedidosConPerdida; // Lista de los pedidos con pérdida

    // Métricas adicionales
    private BigDecimal utilidadMasAlta;
    private BigDecimal perdidaMasAlta;
    private BigDecimal margenMasAlto;
}
