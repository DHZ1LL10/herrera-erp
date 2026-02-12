package com.herrera.erp.repository;

import com.herrera.erp.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Pedidos
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

        Optional<Pedido> findByFolio(String folio);

        List<Pedido> findByEstado(Pedido.Estado estado);

        List<Pedido> findByPrioridad(Pedido.Prioridad prioridad);

        // Pedidos activos (no entregados ni cancelados)
        @Query("SELECT p FROM Pedido p WHERE p.estado NOT IN ('ENTREGADO', 'CANCELADO') " +
                        "ORDER BY p.fechaEntrega ASC")
        List<Pedido> findPedidosActivos();

        // Pedidos retrasados
        @Query("SELECT p FROM Pedido p WHERE p.estado NOT IN ('ENTREGADO', 'CANCELADO') " +
                        "AND p.fechaEntrega < CURRENT_DATE ORDER BY p.fechaEntrega ASC")
        List<Pedido> findPedidosRetrasados();

        // Pedidos por entregar hoy
        @Query("SELECT p FROM Pedido p WHERE p.estado NOT IN ('ENTREGADO', 'CANCELADO') " +
                        "AND p.fechaEntrega = CURRENT_DATE")
        List<Pedido> findPedidosEntregarHoy();

        // Alias para compatibilidad
        default List<Pedido> findPedidosPorEntregarHoy() {
                return findPedidosEntregarHoy();
        }

        // Pedidos por entregar en próximos N días
        @Query("SELECT p FROM Pedido p WHERE p.estado NOT IN ('ENTREGADO', 'CANCELADO') " +
                        "AND p.fechaEntrega BETWEEN CURRENT_DATE AND :fechaLimite " +
                        "ORDER BY p.fechaEntrega ASC")
        List<Pedido> findPedidosProximosAEntregar(LocalDate fechaLimite);

        // Pedidos por cliente
        List<Pedido> findByClienteNombreContainingIgnoreCase(String nombreCliente);

        // Pedidos por ubicación
        List<Pedido> findByUbicacionOrigen(Pedido.UbicacionOrigen ubicacion);

        // Pedidos preferenciales pendientes
        @Query("SELECT p FROM Pedido p WHERE p.prioridad = 'PREFERENCIAL' " +
                        "AND p.estado NOT IN ('ENTREGADO', 'CANCELADO') " +
                        "ORDER BY p.fechaEntrega ASC")
        List<Pedido> findPedidosPreferencialesPendientes();

        boolean existsByFolio(String folio);

        // Contar pedidos por estado
        long countByEstado(Pedido.Estado estado);

        // Query para costos - obtener pedidos por rango de fechas
        List<Pedido> findByFechaPedidoBetween(LocalDate inicio, LocalDate fin);

        // Query para stats usando estado
        @Query("SELECT p FROM Pedido p WHERE p.estado IN :estados ORDER BY p.fechaEntrega ASC")
        List<Pedido> findByEstadoIn(@Param("estados") List<Pedido.Estado> estados);
}