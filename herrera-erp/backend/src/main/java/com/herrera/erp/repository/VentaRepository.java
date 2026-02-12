package com.herrera.erp.repository;

import com.herrera.erp.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Ventas
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/repository/VentaRepository.java
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    Optional<Venta> findByFolioVenta(String folioVenta);

    List<Venta> findByUbicacion(Venta.UbicacionVenta ubicacion);

    List<Venta> findByUsuarioVendedorId(Long usuarioId);

    // Ventas del día
    @Query("SELECT v FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasDelDia();

    // Ventas en rango de fechas
    List<Venta> findByFechaVentaBetweenOrderByFechaVentaDesc(
            LocalDateTime inicio,
            LocalDateTime fin);

    // Total de ventas del día
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    BigDecimal calcularTotalVentasDelDia();

    // Total de ventas por ubicación
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.ubicacion = :ubicacion " +
            "AND DATE(v.fechaVenta) = CURRENT_DATE")
    BigDecimal calcularTotalVentasDelDiaPorUbicacion(Venta.UbicacionVenta ubicacion);

    boolean existsByFolioVenta(String folioVenta);
}
