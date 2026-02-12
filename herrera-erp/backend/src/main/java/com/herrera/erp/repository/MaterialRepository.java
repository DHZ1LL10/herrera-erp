package com.herrera.erp.repository;

import com.herrera.erp.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio de Materiales
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/repository/MaterialRepository.java
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByActivoTrue();

    List<Material> findByTipoMaterialId(Long tipoMaterialId);

    List<Material> findByNombreContainingIgnoreCase(String nombre);

    List<Material> findByColorIgnoreCase(String color);

    // Materiales con stock bajo
    @Query("SELECT m FROM Material m WHERE m.activo = true AND m.stockActual <= m.stockMinimo")
    List<Material> findMaterialesStockBajo();

    // Materiales con stock crítico
    @Query("SELECT m FROM Material m WHERE m.activo = true AND m.stockActual <= m.stockCritico")
    List<Material> findMaterialesStockCritico();

    // Buscar por tipo y color
    List<Material> findByTipoMaterialIdAndColorIgnoreCase(Long tipoMaterialId, String color);

    // Contar materiales activos
    long countByActivoTrue();
}