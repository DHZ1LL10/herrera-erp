package com.herrera.erp.repository;

import com.herrera.erp.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de Movimientos de Inventario
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/repository/MovimientoInventarioRepository.java
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByMaterialIdOrderByFechaDesc(Long materialId);

    List<MovimientoInventario> findByRolloIdOrderByFechaDesc(Long rolloId);

    List<MovimientoInventario> findByTipoMovimiento(MovimientoInventario.TipoMovimiento tipo);

    List<MovimientoInventario> findByPedidoId(Long pedidoId);

    // Últimos N movimientos
    List<MovimientoInventario> findTop20ByOrderByFechaDesc();

    // Movimientos en rango de fechas
    List<MovimientoInventario> findByFechaBetweenOrderByFechaDesc(
            LocalDateTime inicio,
            LocalDateTime fin);

    // Movimientos del día
    @Query("SELECT m FROM MovimientoInventario m WHERE DATE(m.fecha) = CURRENT_DATE ORDER BY m.fecha DESC")
    List<MovimientoInventario> findMovimientosDelDia();
}