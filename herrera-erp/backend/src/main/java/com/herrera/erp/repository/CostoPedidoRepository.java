package com.herrera.erp.repository;

import com.herrera.erp.model.CostoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para CostoPedido
 * Incluye queries personalizadas para reportes y estadísticas
 */
@Repository
public interface CostoPedidoRepository extends JpaRepository<CostoPedido, Long> {

    /**
     * Buscar costos por ID de pedido
     */
    Optional<CostoPedido> findByPedidoId(Long pedidoId);

    /**
     * Verificar si existen costos para un pedido
     */
    boolean existsByPedidoId(Long pedidoId);

    /**
     * Obtener costos de pedidos en un periodo
     */
    @Query("SELECT c FROM CostoPedido c " +
            "WHERE c.pedido.fechaPedido BETWEEN :inicio AND :fin " +
            "ORDER BY c.pedido.fechaPedido DESC")
    List<CostoPedido> findByPeriodo(@Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    /**
     * Obtener costos con paginación
     */
    @Query("SELECT c FROM CostoPedido c ORDER BY c.createdAt DESC")
    Page<CostoPedido> findAllWithPagination(Pageable pageable);

    /**
     * Obtener pedidos con pérdida (utilidad negativa)
     */
    @Query("SELECT c FROM CostoPedido c " +
            "WHERE c.utilidad < 0 " +
            "ORDER BY c.utilidad ASC")
    List<CostoPedido> findPedidosConPerdida();

    /**
     * Obtener pedidos más rentables ordenados por margen
     */
    @Query("SELECT c FROM CostoPedido c " +
            "WHERE c.utilidad > 0 " +
            "ORDER BY c.margenPorcentaje DESC")
    List<CostoPedido> findPedidosMasRentables(Pageable pageable);

    /**
     * Contar pedidos rentables (utilidad > 0)
     */
    @Query("SELECT COUNT(c) FROM CostoPedido c WHERE c.utilidad > 0")
    Long countPedidosRentables();

    /**
     * Contar pedidos con pérdida (utilidad < 0)
     */
    @Query("SELECT COUNT(c) FROM CostoPedido c WHERE c.utilidad < 0")
    Long countPedidosConPerdida();

    /**
     * Suma total de ventas en un periodo
     */
    @Query("SELECT COALESCE(SUM(c.precioVenta), 0) FROM CostoPedido c " +
            "WHERE c.pedido.fechaPedido BETWEEN :inicio AND :fin")
    BigDecimal sumVentasByPeriodo(@Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    /**
     * Suma total de costos en un periodo
     */
    @Query("SELECT COALESCE(SUM(c.totalCosto), 0) FROM CostoPedido c " +
            "WHERE c.pedido.fechaPedido BETWEEN :inicio AND :fin")
    BigDecimal sumCostosByPeriodo(@Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    /**
     * Suma total de utilidad en un periodo
     */
    @Query("SELECT COALESCE(SUM(c.utilidad), 0) FROM CostoPedido c " +
            "WHERE c.pedido.fechaPedido BETWEEN :inicio AND :fin")
    BigDecimal sumUtilidadByPeriodo(@Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    /**
     * Promedio de margen en un periodo
     */
    @Query("SELECT COALESCE(AVG(c.margenPorcentaje), 0) FROM CostoPedido c " +
            "WHERE c.pedido.fechaPedido BETWEEN :inicio AND :fin")
    BigDecimal avgMargenByPeriodo(@Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    /**
     * Obtener utilidad más alta
     */
    @Query("SELECT MAX(c.utilidad) FROM CostoPedido c")
    BigDecimal findMaxUtilidad();

    /**
     * Obtener pérdida más alta (utilidad más negativa)
     */
    @Query("SELECT MIN(c.utilidad) FROM CostoPedido c WHERE c.utilidad < 0")
    BigDecimal findMaxPerdida();

    /**
     * Obtener margen más alto
     */
    @Query("SELECT MAX(c.margenPorcentaje) FROM CostoPedido c")
    BigDecimal findMaxMargen();
}
