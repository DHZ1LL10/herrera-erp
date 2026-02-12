package com.herrera.erp.service;

import com.herrera.erp.dto.DashboardStatsDTO;
import com.herrera.erp.dto.ReporteDTO;
import com.herrera.erp.model.Material;
import com.herrera.erp.model.Pedido;
import com.herrera.erp.repository.MaterialRepository;
import com.herrera.erp.repository.PedidoRepository;
import com.herrera.erp.repository.RolloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Reportes
 * Estadísticas y reportes del sistema
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteService {

        private final MaterialRepository materialRepository;
        private final PedidoRepository pedidoRepository;
        private final RolloRepository rolloRepository;
        private final VentaService ventaService;

        /**
         * Obtener estadísticas para el dashboard
         */
        public DashboardStatsDTO obtenerEstadisticasDashboard() {
                log.info("Generando estadísticas del dashboard");

                // Alertas de inventario
                List<Material> materialesCriticos = materialRepository.findMaterialesStockCritico();
                List<Material> materialesAlerta = materialRepository.findMaterialesStockBajo();

                List<DashboardStatsDTO.MaterialAlertaDTO> alertasStock = materialesCriticos.stream()
                                .map(m -> DashboardStatsDTO.MaterialAlertaDTO.builder()
                                                .id(m.getId())
                                                .nombre(m.getNombre())
                                                .color(m.getColor())
                                                .stockActual(m.getStockActual())
                                                .stockMinimo(m.getStockMinimo())
                                                .nivelAlerta("CRITICO")
                                                .build())
                                .collect(Collectors.toList());

                // Agregar materiales en alerta (no críticos)
                materialesAlerta.stream()
                                .filter(m -> !materialesCriticos.contains(m))
                                .forEach(m -> alertasStock.add(
                                                DashboardStatsDTO.MaterialAlertaDTO.builder()
                                                                .id(m.getId())
                                                                .nombre(m.getNombre())
                                                                .color(m.getColor())
                                                                .stockActual(m.getStockActual())
                                                                .stockMinimo(m.getStockMinimo())
                                                                .nivelAlerta("BAJO")
                                                                .build()));

                // Pedidos
                List<Pedido> pedidosActivos = pedidoRepository.findPedidosActivos();
                List<Pedido> pedidosHoy = pedidoRepository.findPedidosPorEntregarHoy();
                List<Pedido> pedidosRetrasados = pedidoRepository.findPedidosRetrasados();

                List<DashboardStatsDTO.PedidoResumenDTO> pedidosProximos = pedidosHoy.stream()
                                .limit(5)
                                .map(p -> DashboardStatsDTO.PedidoResumenDTO.builder()
                                                .id(p.getId())
                                                .folio(p.getFolio())
                                                .nombrePedido(p.getNombrePedido())
                                                .clienteNombre(p.getClienteNombre())
                                                .fechaEntrega(p.getFechaEntrega().toString())
                                                .estado(p.getEstado().name())
                                                .prioridad(p.getPrioridad().name())
                                                .build())
                                .collect(Collectors.toList());

                // Inventario
                BigDecimal stockTotalTelas = materialRepository.findByActivoTrue().stream()
                                .filter(m -> m.getTipoMaterial() != null &&
                                                "TELA".equalsIgnoreCase(m.getTipoMaterial().getNombre()))
                                .map(Material::getStockActual)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                int rollosDisponibles = rolloRepository.findRollosDisponibles().size();

                // Ventas del día
                BigDecimal ventasHoy = ventaService.calcularTotalVentasDelDia();
                int numeroVentasHoy = ventaService.obtenerVentasDelDia().size();

                return DashboardStatsDTO.builder()
                                .materialesCriticos(materialesCriticos.size())
                                .materialesAlerta(materialesAlerta.size())
                                .alertasStock(alertasStock)
                                .pedidosActivos(pedidosActivos.size())
                                .pedidosPorEntregarHoy(pedidosHoy.size())
                                .pedidosRetrasados(pedidosRetrasados.size())
                                .pedidosProximos(pedidosProximos)
                                .stockTotalTelas(stockTotalTelas)
                                .rollosDisponibles(rollosDisponibles)
                                .ventasHoy(ventasHoy)
                                .numeroVentasHoy(numeroVentasHoy)
                                .build();
        }

        /**
         * Generar reporte de inventario
         */
        public ReporteDTO generarReporteInventario() {
                log.info("Generando reporte de inventario");

                List<Material> materiales = materialRepository.findByActivoTrue();
                List<Material> criticos = materialRepository.findMaterialesStockCritico();
                List<Material> alertas = materialRepository.findMaterialesStockBajo();

                BigDecimal valorTotal = materiales.stream()
                                .filter(m -> m.getPrecioUnitario() != null)
                                .map(m -> m.getStockActual().multiply(m.getPrecioUnitario()))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return ReporteDTO.builder()
                                .titulo("Reporte de Inventario")
                                .descripcion("Estado actual del inventario de materiales")
                                .fechaInicio(LocalDate.now())
                                .fechaFin(LocalDate.now())
                                .totalMateriales(materiales.size())
                                .materialesCriticos(criticos.size())
                                .materialesAlerta(alertas.size())
                                .valorTotalInventario(valorTotal)
                                .datos(materiales)
                                .build();
        }

        /**
         * Generar reporte de pedidos
         */
        public ReporteDTO generarReportePedidos(LocalDate fechaInicio, LocalDate fechaFin) {
                log.info("Generando reporte de pedidos: {} a {}", fechaInicio, fechaFin);

                List<Pedido> todosPedidos = pedidoRepository.findAll();
                List<Pedido> pedidosActivos = pedidoRepository.findPedidosActivos();
                List<Pedido> pedidosRetrasados = pedidoRepository.findPedidosRetrasados();
                List<Pedido> pedidosEntregados = pedidoRepository.findByEstado(Pedido.Estado.ENTREGADO);

                return ReporteDTO.builder()
                                .titulo("Reporte de Pedidos")
                                .descripcion("Resumen de pedidos del periodo")
                                .fechaInicio(fechaInicio)
                                .fechaFin(fechaFin)
                                .totalPedidos(todosPedidos.size())
                                .pedidosPendientes(pedidosActivos.size())
                                .pedidosRetrasados(pedidosRetrasados.size())
                                .pedidosEntregados(pedidosEntregados.size())
                                .datos(todosPedidos)
                                .build();
        }

        /**
         * Generar reporte de ventas
         */
        public ReporteDTO generarReporteVentas(LocalDate fechaInicio, LocalDate fechaFin) {
                log.info("Generando reporte de ventas: {} a {}", fechaInicio, fechaFin);

                BigDecimal totalVentas = ventaService.calcularTotalVentasPorPeriodo(fechaInicio, fechaFin);
                var ventas = ventaService.obtenerVentasPorRango(fechaInicio, fechaFin);
                int numeroVentas = ventas.size();

                BigDecimal promedioVenta = numeroVentas > 0
                                ? totalVentas.divide(new BigDecimal(numeroVentas), 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;

                return ReporteDTO.builder()
                                .titulo("Reporte de Ventas")
                                .descripcion("Análisis de ventas del periodo")
                                .fechaInicio(fechaInicio)
                                .fechaFin(fechaFin)
                                .totalVentas(totalVentas)
                                .numeroVentas(numeroVentas)
                                .promedioVenta(promedioVenta)
                                .datos(ventas)
                                .build();
        }
}
