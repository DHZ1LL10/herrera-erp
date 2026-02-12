package com.herrera.erp.repository;

import com.herrera.erp.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de Productos
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/repository/ProductoRepository.java
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    // NOTA: Comentado porque tipoCorte no se usa en MVP
    // List<Producto> findByTipoCorteId(Long tipoCorteId);

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    long countByActivoTrue();
}