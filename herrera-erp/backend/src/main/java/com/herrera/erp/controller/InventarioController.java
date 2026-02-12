package com.herrera.erp.controller;

import com.herrera.erp.model.*;
import com.herrera.erp.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller de Inventario
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/controller/InventarioController.java
 */
@RestController
@RequestMapping("/api/inventario")

@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    // ============================================
    // MATERIALES
    // ============================================

    /**
     * GET /api/inventario/materiales
     * Obtener todos los materiales
     */
    @GetMapping("/materiales")
    public ResponseEntity<List<Material>> obtenerMateriales() {
        return ResponseEntity.ok(inventarioService.obtenerTodosMateriales());
    }

    /**
     * GET /api/inventario/materiales/{id}
     * Obtener material por ID
     */
    @GetMapping("/materiales/{id}")
    public ResponseEntity<Material> obtenerMaterial(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerMaterialPorId(id));
    }

    /**
     * GET /api/inventario/materiales/alertas
     * Obtener materiales con stock bajo
     */
    @GetMapping("/materiales/alertas")
    public ResponseEntity<List<Material>> obtenerAlerts() {
        return ResponseEntity.ok(inventarioService.obtenerMaterialesConAlerta());
    }

    /**
     * GET /api/inventario/materiales/criticos
     * Obtener materiales críticos
     */
    @GetMapping("/materiales/criticos")
    public ResponseEntity<List<Material>> obtenerCriticos() {
        return ResponseEntity.ok(inventarioService.obtenerMaterialesCriticos());
    }

    /**
     * POST /api/inventario/materiales
     * Crear nuevo material
     */
    @PostMapping("/materiales")
    public ResponseEntity<Material> crearMaterial(@RequestBody Material material) {
        return ResponseEntity.ok(inventarioService.crearMaterial(material));
    }

    // ============================================
    // ROLLOS
    // ============================================

    /**
     * GET /api/inventario/rollos
     * Obtener todos los rollos disponibles
     */
    @GetMapping("/rollos")
    public ResponseEntity<List<Rollo>> obtenerRollos() {
        return ResponseEntity.ok(inventarioService.obtenerRollosDisponibles());
    }

    /**
     * GET /api/inventario/rollos/corte
     * Obtener rollos disponibles para corte
     */
    @GetMapping("/rollos/corte")
    public ResponseEntity<List<Rollo>> obtenerRollosCorte() {
        return ResponseEntity.ok(inventarioService.obtenerRollosParaCorte());
    }

    /**
     * GET /api/inventario/rollos/venta
     * Obtener rollos disponibles para venta
     */
    @GetMapping("/rollos/venta")
    public ResponseEntity<List<Rollo>> obtenerRollosVenta() {
        return ResponseEntity.ok(inventarioService.obtenerRollosParaVenta());
    }

    /**
     * POST /api/inventario/rollos
     * Registrar nuevo rollo
     */
    @PostMapping("/rollos")
    public ResponseEntity<Rollo> registrarRollo(
            @RequestBody Rollo rollo,
            @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(inventarioService.registrarRollo(rollo, usuarioId));
    }

    // ============================================
    // MOVIMIENTOS
    // ============================================

    /**
     * GET /api/inventario/movimientos
     * Obtener últimos movimientos
     */
    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoInventario>> obtenerMovimientos(
            @RequestParam(defaultValue = "20") int limite) {
        return ResponseEntity.ok(inventarioService.obtenerUltimosMovimientos(limite));
    }

    /**
     * GET /api/inventario/movimientos/material/{materialId}
     * Obtener movimientos de un material específico
     */
    @GetMapping("/movimientos/material/{materialId}")
    public ResponseEntity<List<MovimientoInventario>> obtenerMovimientosMaterial(
            @PathVariable Long materialId) {
        return ResponseEntity.ok(inventarioService.obtenerMovimientosPorMaterial(materialId));
    }

    /**
     * GET /api/inventario/movimientos/hoy
     * Obtener movimientos del día
     */
    @GetMapping("/movimientos/hoy")
    public ResponseEntity<List<MovimientoInventario>> obtenerMovimientosHoy() {
        return ResponseEntity.ok(inventarioService.obtenerMovimientosDelDia());
    }

    /**
     * POST /api/inventario/movimientos
     * Registrar nuevo movimiento
     */
    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoInventario> registrarMovimiento(
            @RequestBody MovimientoRequest request) {
        return ResponseEntity.ok(inventarioService.registrarMovimiento(
                request.getMaterialId(),
                request.getRolloId(),
                request.getTipo(),
                request.getCantidad(),
                request.getMotivo(),
                request.getPedidoId(),
                request.getUsuarioId()));
    }

    /**
     * POST /api/inventario/salida-corte
     * Registrar salida para corte
     */
    @PostMapping("/salida-corte")
    public ResponseEntity<?> registrarSalidaCorte(@RequestBody SalidaCorteRequest request) {
        inventarioService.registrarSalidaParaCorte(
                request.getRolloId(),
                request.getMetros(),
                request.getPedidoId(),
                request.getUsuarioId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Salida registrada exitosamente");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/inventario/salida-venta
     * Registrar salida por venta
     */
    @PostMapping("/salida-venta")
    public ResponseEntity<?> registrarSalidaVenta(@RequestBody SalidaVentaRequest request) {
        inventarioService.registrarSalidaParaVenta(
                request.getRolloId(),
                request.getMetros(),
                request.getMotivo(),
                request.getUsuarioId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Venta registrada exitosamente");
        return ResponseEntity.ok(response);
    }

    // ============================================
    // DTOs INTERNOS
    // ============================================

    @lombok.Data
    static class MovimientoRequest {
        private Long materialId;
        private Long rolloId;
        private MovimientoInventario.TipoMovimiento tipo;
        private BigDecimal cantidad;
        private String motivo;
        private Long pedidoId;
        private Long usuarioId;
    }

    @lombok.Data
    static class SalidaCorteRequest {
        private Long rolloId;
        private BigDecimal metros;
        private Long pedidoId;
        private Long usuarioId;
    }

    @lombok.Data
    static class SalidaVentaRequest {
        private Long rolloId;
        private BigDecimal metros;
        private String motivo;
        private Long usuarioId;
    }
}