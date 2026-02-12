package com.herrera.erp.controller;

import com.herrera.erp.model.Pedido;
import com.herrera.erp.model.PedidoItem;
import com.herrera.erp.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller de Pedidos (Folios)
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/controller/PedidoController.java
 */
@RestController
@RequestMapping("/api/pedidos")

@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // ============================================
    // CONSULTAS
    // ============================================

    /**
     * GET /api/pedidos
     * Obtener todos los pedidos
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerPedidos() {
        return ResponseEntity.ok(pedidoService.obtenerTodosPedidos());
    }

    /**
     * GET /api/pedidos/activos
     * Obtener pedidos activos (no entregados ni cancelados)
     */
    @GetMapping("/activos")
    public ResponseEntity<List<Pedido>> obtenerActivos() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosActivos());
    }

    /**
     * GET /api/pedidos/retrasados
     * Obtener pedidos retrasados
     */
    @GetMapping("/retrasados")
    public ResponseEntity<List<Pedido>> obtenerRetrasados() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosRetrasados());
    }

    /**
     * GET /api/pedidos/hoy
     * Obtener pedidos a entregar hoy
     */
    @GetMapping("/hoy")
    public ResponseEntity<List<Pedido>> obtenerEntregarHoy() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosEntregarHoy());
    }

    /**
     * GET /api/pedidos/preferenciales
     * Obtener pedidos preferenciales pendientes
     */
    @GetMapping("/preferenciales")
    public ResponseEntity<List<Pedido>> obtenerPreferenciales() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPreferenciales());
    }

    /**
     * GET /api/pedidos/proximos
     * Obtener pedidos próximos a entregar (próximos 7 días)
     */
    @GetMapping("/proximos")
    public ResponseEntity<List<Pedido>> obtenerProximos(
            @RequestParam(defaultValue = "7") int dias) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosProximosAEntregar(dias));
    }

    /**
     * GET /api/pedidos/{id}
     * Obtener pedido por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(id));
    }

    /**
     * GET /api/pedidos/folio/{folio}
     * Obtener pedido por folio
     */
    @GetMapping("/folio/{folio}")
    public ResponseEntity<Pedido> obtenerPorFolio(@PathVariable String folio) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorFolio(folio));
    }

    // ============================================
    // CREACIÓN Y ACTUALIZACIÓN
    // ============================================

    /**
     * POST /api/pedidos
     * Crear nuevo pedido
     */
    @PostMapping
    public ResponseEntity<Pedido> crearPedido(
            @RequestBody CrearPedidoRequest request) {
        Pedido pedido = pedidoService.crearPedido(
                request.getPedido(),
                request.getItems(),
                request.getUsuarioId());
        return ResponseEntity.ok(pedido);
    }

    /**
     * PUT /api/pedidos/{id}/estado
     * Actualizar estado del pedido
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        String estadoStr = (String) request.get("estado");
        Long usuarioId = request.get("usuarioId") != null ? Long.parseLong(request.get("usuarioId").toString()) : null;

        Pedido.Estado nuevoEstado = Pedido.Estado.valueOf(estadoStr);

        return ResponseEntity.ok(
                pedidoService.actualizarEstado(id, nuevoEstado, usuarioId));
    }

    /**
     * PUT /api/pedidos/{id}/entregar
     * Marcar pedido como entregado
     */
    @PutMapping("/{id}/entregar")
    public ResponseEntity<Pedido> marcarEntregado(
            @PathVariable Long id,
            @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(
                pedidoService.marcarComoEntregado(id, usuarioId));
    }

    /**
     * PUT /api/pedidos/{id}/cancelar
     * Cancelar pedido
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelarPedido(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        String motivo = (String) request.get("motivo");
        Long usuarioId = request.get("usuarioId") != null ? Long.parseLong(request.get("usuarioId").toString()) : null;

        return ResponseEntity.ok(
                pedidoService.cancelarPedido(id, motivo, usuarioId));
    }

    // ============================================
    // ESTADÍSTICAS
    // ============================================

    /**
     * GET /api/pedidos/stats
     * Obtener estadísticas del dashboard
     */
    @GetMapping("/stats")
    public ResponseEntity<PedidoService.DashboardStats> obtenerEstadisticas() {
        return ResponseEntity.ok(pedidoService.obtenerEstadisticas());
    }

    // ============================================
    // DTOs
    // ============================================

    @lombok.Data
    static class CrearPedidoRequest {
        private Pedido pedido;
        private List<PedidoItem> items;
        private Long usuarioId;
    }
}