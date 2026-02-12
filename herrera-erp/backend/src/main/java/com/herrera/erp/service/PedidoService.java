package com.herrera.erp.service;

import com.herrera.erp.model.*;
import com.herrera.erp.repository.*;
import com.herrera.erp.util.FolioGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Pedidos (Folios)
 * Ubicación: backend/src/main/java/com/herrera/erp/service/PedidoService.java
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FolioGenerator folioGenerator;

    // Orden de tallas predefinido
    private static final List<String> ORDEN_TALLAS = Arrays.asList(
            "3", "4", "6", "8", "10", "12", "14", "16",
            "CH", "M", "L", "XL", "XXL", "3XL", "4XL");

    // ============================================
    // CRUD DE PEDIDOS
    // ============================================

    public List<Pedido> obtenerTodosPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> obtenerPedidosActivos() {
        return pedidoRepository.findPedidosActivos();
    }

    public List<Pedido> obtenerPedidosRetrasados() {
        return pedidoRepository.findPedidosRetrasados();
    }

    public List<Pedido> obtenerPedidosEntregarHoy() {
        return pedidoRepository.findPedidosEntregarHoy();
    }

    public List<Pedido> obtenerPedidosPreferenciales() {
        return pedidoRepository.findPedidosPreferencialesPendientes();
    }

    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public Pedido obtenerPedidoPorFolio(String folio) {
        return pedidoRepository.findByFolio(folio)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    // ============================================
    // CREACIÓN DE PEDIDO
    // ============================================

    @Transactional
    public Pedido crearPedido(Pedido pedido, List<PedidoItem> items, Long usuarioId) {
        log.info("Creando pedido para cliente: {}", pedido.getClienteNombre());

        // Generar folio único
        String folio = folioGenerator.generarFolio();
        while (pedidoRepository.existsByFolio(folio)) {
            folio = folioGenerator.generarFolio();
        }
        pedido.setFolio(folio);

        // Asignar usuario creador
        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            pedido.setUsuarioCreador(usuario);
        }

        // Validar producto
        if (pedido.getProducto() != null && pedido.getProducto().getId() != null) {
            Producto producto = productoRepository.findById(pedido.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            pedido.setProducto(producto);
        }

        // Ordenar items por talla
        List<PedidoItem> itemsOrdenados = ordenarItemsPorTalla(items);

        // Calcular totales
        pedido.setTotalPiezas(itemsOrdenados.size());

        if (pedido.getProducto() != null) {
            BigDecimal telaTotal = calcularTelaTotal(pedido.getProducto(), itemsOrdenados);
            pedido.setTotalTelaEstimada(telaTotal);
        }

        // Guardar pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Guardar items asociados
        for (int i = 0; i < itemsOrdenados.size(); i++) {
            PedidoItem item = itemsOrdenados.get(i);
            item.setPedido(pedidoGuardado);
            item.setOrdenTalla(i + 1);
        }
        pedidoGuardado.setItems(itemsOrdenados);
        pedidoRepository.save(pedidoGuardado);

        log.info("Pedido creado exitosamente - Folio: {} - {} piezas",
                folio, pedidoGuardado.getTotalPiezas());

        return pedidoGuardado;
    }

    // ============================================
    // ACTUALIZACIÓN DE ESTADO
    // ============================================

    @Transactional
    public Pedido actualizarEstado(Long pedidoId, Pedido.Estado nuevoEstado, Long usuarioId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);

        Pedido.Estado estadoAnterior = pedido.getEstado();
        pedido.setEstado(nuevoEstado);

        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        log.info("Estado de pedido {} actualizado: {} → {}",
                pedido.getFolio(), estadoAnterior, nuevoEstado);

        return pedidoActualizado;
    }

    @Transactional
    public Pedido marcarComoEntregado(Long pedidoId, Long usuarioId) {
        return actualizarEstado(pedidoId, Pedido.Estado.ENTREGADO, usuarioId);
    }

    @Transactional
    public Pedido cancelarPedido(Long pedidoId, String motivo, Long usuarioId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);

        pedido.setEstado(Pedido.Estado.CANCELADO);
        pedido.setObservaciones(
                (pedido.getObservaciones() != null ? pedido.getObservaciones() + "\n" : "") +
                        "CANCELADO: " + motivo);

        log.info("Pedido {} cancelado. Motivo: {}", pedido.getFolio(), motivo);

        return pedidoRepository.save(pedido);
    }

    // ============================================
    // CÁLCULOS Y UTILIDADES
    // ============================================

    /**
     * Calcula el total de tela necesaria para un pedido
     */
    public BigDecimal calcularTelaTotal(Producto producto, List<PedidoItem> items) {
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoItem item : items) {
            BigDecimal consumoPorPieza = producto.calcularConsumoParaTalla(item.getTalla());
            total = total.add(consumoPorPieza);
        }

        return total;
    }

    /**
     * Ordena los items del pedido según el orden de tallas predefinido
     */
    private List<PedidoItem> ordenarItemsPorTalla(List<PedidoItem> items) {
        return items.stream()
                .sorted(Comparator.comparingInt(item -> {
                    int index = ORDEN_TALLAS.indexOf(item.getTalla().toUpperCase());
                    return index == -1 ? 999 : index;
                }))
                .collect(Collectors.toList());
    }

    /**
     * Verifica si un pedido está retrasado
     */
    public boolean estaRetrasado(Long pedidoId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);
        return pedido.estaRetrasado();
    }

    /**
     * Obtiene pedidos próximos a entregar (próximos N días)
     */
    public List<Pedido> obtenerPedidosProximosAEntregar(int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return pedidoRepository.findPedidosProximosAEntregar(fechaLimite);
    }

    /**
     * Cuenta pedidos por estado
     */
    public long contarPedidosPorEstado(Pedido.Estado estado) {
        return pedidoRepository.countByEstado(estado);
    }

    /**
     * Obtiene estadísticas del dashboard
     */
    public DashboardStats obtenerEstadisticas() {
        return DashboardStats.builder()
                .pedidosActivos(pedidoRepository.findPedidosActivos().size())
                .pedidosRetrasados(pedidoRepository.findPedidosRetrasados().size())
                .pedidosEntregarHoy(pedidoRepository.findPedidosEntregarHoy().size())
                .pedidosPreferenciales(pedidoRepository.findPedidosPreferencialesPendientes().size())
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class DashboardStats {
        private int pedidosActivos;
        private int pedidosRetrasados;
        private int pedidosEntregarHoy;
        private int pedidosPreferenciales;
    }
}