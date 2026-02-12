package com.herrera.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para estadísticas del dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // Alertas críticas
    private Integer materialesCriticos;
    private Integer materialesAlerta;
    private List<MaterialAlertaDTO> alertasStock;

    // Pedidos
    private Integer pedidosActivos;
    private Integer pedidosPorEntregarHoy;
    private Integer pedidosRetrasados;
    private List<PedidoResumenDTO> pedidosProximos;

    // Inventario
    private BigDecimal stockTotalTelas;
    private Integer rollosDisponibles;

    // Ventas del día
    private BigDecimal ventasHoy;
    private Integer numeroVentasHoy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialAlertaDTO {
        private Long id;
        private String nombre;
        private String color;
        private BigDecimal stockActual;
        private BigDecimal stockMinimo;
        private String nivelAlerta; // CRITICO, BAJO
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PedidoResumenDTO {
        private Long id;
        private String folio;
        private String nombrePedido;
        private String clienteNombre;
        private String fechaEntrega;
        private String estado;
        private String prioridad;
    }
}
