package com.herrera.erp.controller;

import com.herrera.erp.model.Venta;
import com.herrera.erp.service.VentaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller de Ventas
 */
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    /**
     * GET /api/ventas
     * Listar todas las ventas
     */
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas(
            @RequestParam(required = false) String ubicacion) {

        List<Venta> ventas = ubicacion != null
                ? ventaService.obtenerVentasPorUbicacion(ubicacion)
                : ventaService.obtenerTodasVentas();

        return ResponseEntity.ok(ventas);
    }

    /**
     * GET /api/ventas/hoy
     * Ventas del día
     */
    @GetMapping("/hoy")
    public ResponseEntity<Map<String, Object>> obtenerVentasHoy() {
        List<Venta> ventas = ventaService.obtenerVentasDelDia();
        BigDecimal total = ventaService.calcularTotalVentasDelDia();

        return ResponseEntity.ok(Map.of(
                "ventas", ventas,
                "total", total,
                "cantidad", ventas.size()));
    }

    /**
     * GET /api/ventas/reporte
     * Reporte de ventas por rango de fechas
     */
    @GetMapping("/reporte")
    public ResponseEntity<Map<String, Object>> obtenerReporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<Venta> ventas = ventaService.obtenerVentasPorRango(fechaInicio, fechaFin);
        BigDecimal total = ventaService.calcularTotalVentasPorPeriodo(fechaInicio, fechaFin);

        return ResponseEntity.ok(Map.of(
                "ventas", ventas,
                "total", total,
                "cantidad", ventas.size(),
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin));
    }

    /**
     * POST /api/ventas/tela
     * Registrar venta de tela por metros
     */
    @PostMapping("/tela")
    public ResponseEntity<Venta> registrarVentaTela(@RequestBody VentaTelaRequest request) {
        Venta venta = ventaService.registrarVentaTela(
                request.getRolloId(),
                request.getMetrosVendidos(),
                request.getClienteNombre(),
                request.getClienteTelefono(),
                request.getPrecioUnitario(),
                request.getMetodoPago(),
                request.getUbicacion(),
                request.getUsuarioVendedorId());

        return ResponseEntity.status(HttpStatus.CREATED).body(venta);
    }

    /**
     * POST /api/ventas/clone
     * Registrar venta de clones
     */
    @PostMapping("/clone")
    public ResponseEntity<Venta> registrarVentaClone(@RequestBody VentaCloneRequest request) {
        Venta venta = ventaService.registrarVentaClone(
                request.getMaterialId(),
                request.getCantidad(),
                request.getClienteNombre(),
                request.getClienteTelefono(),
                request.getPrecioUnitario(),
                request.getMetodoPago(),
                request.getUbicacion(),
                request.getUsuarioVendedorId());

        return ResponseEntity.status(HttpStatus.CREATED).body(venta);
    }

    // DTOs internos
    @Data
    static class VentaTelaRequest {
        private Long rolloId;
        private BigDecimal metrosVendidos;
        private String clienteNombre;
        private String clienteTelefono;
        private BigDecimal precioUnitario;
        private String metodoPago;
        private String ubicacion;
        private Long usuarioVendedorId;
    }

    @Data
    static class VentaCloneRequest {
        private Long materialId;
        private Integer cantidad;
        private String clienteNombre;
        private String clienteTelefono;
        private BigDecimal precioUnitario;
        private String metodoPago;
        private String ubicacion;
        private Long usuarioVendedorId;
    }
}
