package com.herrera.erp.controller;

import com.herrera.erp.dto.DashboardStatsDTO;
import com.herrera.erp.dto.ReporteDTO;
import com.herrera.erp.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller de Reportes
 */
@RestController
@RequestMapping("/api/reportes")

@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * GET /api/reportes/dashboard
     * Obtener estadísticas para el dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> obtenerDashboard() {
        DashboardStatsDTO stats = reporteService.obtenerEstadisticasDashboard();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/reportes/inventario
     * Reporte de inventario
     */
    @GetMapping("/inventario")
    public ResponseEntity<ReporteDTO> obtenerReporteInventario() {
        ReporteDTO reporte = reporteService.generarReporteInventario();
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /api/reportes/pedidos
     * Reporte de pedidos por rango de fechas
     */
    @GetMapping("/pedidos")
    public ResponseEntity<ReporteDTO> obtenerReportePedidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Valores por defecto: último mes
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        ReporteDTO reporte = reporteService.generarReportePedidos(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    /**
     * GET /api/reportes/ventas
     * Reporte de ventas por rango de fechas
     */
    @GetMapping("/ventas")
    public ResponseEntity<ReporteDTO> obtenerReporteVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Valores por defecto: último mes
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        ReporteDTO reporte = reporteService.generarReporteVentas(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}
