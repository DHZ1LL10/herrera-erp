package com.herrera.erp.repository;

import com.herrera.erp.model.ProductoAjusteTalla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para ProductoAjusteTalla
 */
@Repository
public interface ProductoAjusteTallaRepository extends JpaRepository<ProductoAjusteTalla, Long> {

    /**
     * Buscar ajustes por producto ID
     */
    List<ProductoAjusteTalla> findByProductoId(Long productoId);

    /**
     * Buscar ajuste específico por producto y talla
     */
    ProductoAjusteTalla findByProductoIdAndTalla(Long productoId, String talla);
}
