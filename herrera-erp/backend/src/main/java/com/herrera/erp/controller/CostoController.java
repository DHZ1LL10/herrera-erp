package com.herrera.erp.controller;

import com.herrera.erp.dto.CostoPedidoDTO;
import com.herrera.erp.dto.RegistrarCostoRequest;
import com.herrera.erp.dto.ReporteUtilidadDTO;
import com.herrera.erp.service.CostoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para gestión de costos
 * @PreAuthorize("hasRole('ADMIN')") - Solo accesible para administradores
 */
@RestController
@RequestMapping("/api/costos")
@PreAuthorize("hasRole('ADMIN')") // ← CRÍTICO: Solo ADMIN puede acceder
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CostoController {

    private final CostoService costoService;

    /**
     * Obtener costos de un pedido específico
     * GET /api/costos/pedido/{pedidoId}
     */
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<CostoPedidoDTO> obtenerCostosPedido(@PathVariable Long pedidoId) {
        log.info("GET /api/costos/pedido/{} - Usuario ADMIN consultando costos", pedidoId);
        CostoPedidoDTO costo = costoService.obtenerCostosPorPedido(pedidoId);
        return ResponseEntity.ok(costo);
    }

    /**
     * Listar todos los costos con paginación y ordenamiento
     * GET /api/costos?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<CostoPedidoDTO>> listarCostos(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/costos - Listando costos con paginación");
        Page<CostoPedidoDTO> costos = costoService.listarTodos(pageable);
        return ResponseEntity.ok(costos);
    }

    /**
     * Registrar costos de un pedido (crear o actualizar)
     * POST /api/costos
     */
    @PostMapping
    public ResponseEntity<CostoPedidoDTO> registrarCostos(
            @Valid @RequestBody RegistrarCostoRequest request) {
        log.info("POST /api/costos - Registrando costos para pedido ID: {}", request.getPedidoId());
        CostoPedidoDTO costo = costoService.registrarCostos(request);

        // Si es un registro nuevo, retornar 201 CREATED
        // Si es una actualización, retornar 200 OK
        HttpStatus status = costoService.tieneCostosRegistrados(request.getPedidoId())
                ? HttpStatus.OK
                : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(costo);
    }

    /**
     * Actualizar costos existentes
     * PUT /api/costos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CostoPedidoDTO> actualizarCostos(
            @PathVariable Long id,
            @Valid @RequestBody RegistrarCostoRequest request) {
        log.info("PUT /api/costos/{} - Actualizando costos", id);

        // El service maneja tanto creación como actualización
        CostoPedidoDTO costo = costoService.registrarCostos(request);
        return ResponseEntity.ok(costo);
    }

    /**
     * Eliminar costos de un pedido
     * DELETE /api/costos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCostos(@PathVariable Long id) {
        log.info("DELETE /api/costos/{} - Eliminando costos", id);
        costoService.eliminarCostos(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Generar reporte de utilidades por periodo
     * GET /api/costos/reporte?fechaInicio=2026-01-01&fechaFin=2026-01-31
     */
    @GetMapping("/reporte")
    public ResponseEntity<ReporteUtilidadDTO> generarReporte(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        log.info("GET /api/costos/reporte - Generando reporte del {} al {}",
                fechaInicio, fechaFin);

        ReporteUtilidadDTO reporte = costoService.generarReportePeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    /**
     * Obtener pedidos más rentables
     * GET /api/costos/top-rentables?limite=10
     */
    @GetMapping("/top-rentables")
    public ResponseEntity<List<CostoPedidoDTO>> obtenerTopRentables(
            @RequestParam(defaultValue = "10") int limite) {
        log.info("GET /api/costos/top-rentables - Obteniendo top {} pedidos rentables", limite);

        // Validar límite razonable
        if (limite < 1 || limite > 100) {
            limite = 10;
        }

        List<CostoPedidoDTO> topRentables = costoService.obtenerPedidosMasRentables(limite);
        return ResponseEntity.ok(topRentables);
    }

    /**
     * Obtener pedidos con pérdida
     * GET /api/costos/con-perdida
     */
    @GetMapping("/con-perdida")
    public ResponseEntity<List<CostoPedidoDTO>> obtenerPedidosConPerdida() {
        log.info("GET /api/costos/con-perdida - Obteniendo pedidos con utilidad negativa");
        List<CostoPedidoDTO> conPerdida = costoService.obtenerPedidosConPerdida();
        return ResponseEntity.ok(conPerdida);
    }

    /**
     * Verificar si un pedido tiene costos registrados
     * GET /api/costos/existe/pedido/{pedidoId}
     */
    @GetMapping("/existe/pedido/{pedidoId}")
    public ResponseEntity<Boolean> existeCostosPedido(@PathVariable Long pedidoId) {
        log.debug("GET /api/costos/existe/pedido/{} - Verificando existencia", pedidoId);
        boolean existe = costoService.tieneCostosRegistrados(pedidoId);
        return ResponseEntity.ok(existe);
    }
}
