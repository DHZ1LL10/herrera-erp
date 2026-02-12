package com.herrera.erp.repository;

import com.herrera.erp.model.PedidoImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para PedidoImagen
 */
@Repository
public interface PedidoImagenRepository extends JpaRepository<PedidoImagen, Long> {

    /**
     * Buscar imágenes por pedido ID
     */
    List<PedidoImagen> findByPedidoId(Long pedidoId);

    /**
     * Buscar imagen principal del pedido
     */
    PedidoImagen findByPedidoIdAndEsPrincipalTrue(Long pedidoId);

    /**
     * Contar imágenes de un pedido
     */
    long countByPedidoId(Long pedidoId);
}
