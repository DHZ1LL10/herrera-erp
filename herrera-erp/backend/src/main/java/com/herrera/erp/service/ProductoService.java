package com.herrera.erp.service;

import com.herrera.erp.dto.ProductoDTO;
import com.herrera.erp.exception.ResourceNotFoundException;
import com.herrera.erp.model.Producto;
import com.herrera.erp.model.ProductoAjusteTalla;
import com.herrera.erp.repository.ProductoAjusteTallaRepository;
import com.herrera.erp.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de Productos
 * Gestión de plantillas de productos y cálculo de tela
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoAjusteTallaRepository ajusteTallaRepository;

    /**
     * Obtener todos los productos activos
     */
    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findByActivoTrue();
    }

    /**
     * Obtener producto por ID
     */
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    /**
     * Crear nuevo producto
     */
    @Transactional
    public Producto crearProducto(ProductoDTO dto) {
        log.info("Creando producto: {}", dto.getNombre());

        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .consumoBaseMetros(dto.getConsumoBaseMetros())
                .incluyeMangas(dto.getIncluyeMangas())
                .consumoMangasMetros(dto.getConsumoMangasMetros())
                .incluyeOtro(dto.getIncluyeOtro())
                .consumoOtroMetros(dto.getConsumoOtroMetros())
                .descripcionOtro(dto.getDescripcionOtro())
                .activo(true)
                .build();

        Producto productoGuardado = productoRepository.save(producto);

        // Guardar ajustes de talla si existen
        if (dto.getAjustesTalla() != null && !dto.getAjustesTalla().isEmpty()) {
            Set<ProductoAjusteTalla> ajustes = new HashSet<>();

            for (ProductoDTO.AjusteTallaDTO ajusteDTO : dto.getAjustesTalla()) {
                ProductoAjusteTalla ajuste = ProductoAjusteTalla.builder()
                        .producto(productoGuardado)
                        .talla(ajusteDTO.getTalla())
                        .ajusteMetros(ajusteDTO.getAjusteMetros())
                        .build();
                ajustes.add(ajuste);
            }

            ajusteTallaRepository.saveAll(ajustes);
            productoGuardado.setAjustesTalla(ajustes);
        }

        log.info("Producto creado exitosamente: ID {}", productoGuardado.getId());
        return productoGuardado;
    }

    /**
     * Actualizar producto existente
     */
    @Transactional
    public Producto actualizarProducto(Long id, ProductoDTO dto) {
        log.info("Actualizando producto ID: {}", id);

        Producto producto = obtenerProductoPorId(id);

        producto.setNombre(dto.getNombre());
        producto.setConsumoBaseMetros(dto.getConsumoBaseMetros());
        producto.setIncluyeMangas(dto.getIncluyeMangas());
        producto.setConsumoMangasMetros(dto.getConsumoMangasMetros());
        producto.setIncluyeOtro(dto.getIncluyeOtro());
        producto.setConsumoOtroMetros(dto.getConsumoOtroMetros());
        producto.setDescripcionOtro(dto.getDescripcionOtro());

        // Actualizar ajustes de talla
        if (dto.getAjustesTalla() != null) {
            // Eliminar ajustes anteriores
            if (producto.getAjustesTalla() != null) {
                ajusteTallaRepository.deleteAll(producto.getAjustesTalla());
            }

            // Crear nuevos ajustes
            Set<ProductoAjusteTalla> nuevosAjustes = new HashSet<>();
            for (ProductoDTO.AjusteTallaDTO ajusteDTO : dto.getAjustesTalla()) {
                ProductoAjusteTalla ajuste = ProductoAjusteTalla.builder()
                        .producto(producto)
                        .talla(ajusteDTO.getTalla())
                        .ajusteMetros(ajusteDTO.getAjusteMetros())
                        .build();
                nuevosAjustes.add(ajuste);
            }

            ajusteTallaRepository.saveAll(nuevosAjustes);
            producto.setAjustesTalla(nuevosAjustes);
        }

        Producto productoActualizado = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente: ID {}", id);

        return productoActualizado;
    }

    /**
     * Desactivar producto (soft delete)
     */
    @Transactional
    public void desactivarProducto(Long id) {
        log.info("Desactivando producto ID: {}", id);

        Producto producto = obtenerProductoPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);

        log.info("Producto desactivado: {}", producto.getNombre());
    }

    /**
     * Calcular consumo total de tela para un pedido
     */
    public BigDecimal calcularConsumoTela(Long productoId, Map<String, Integer> tallasConCantidades) {
        Producto producto = obtenerProductoPorId(productoId);
        BigDecimal consumoTotal = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : tallasConCantidades.entrySet()) {
            String talla = entry.getKey();
            Integer cantidad = entry.getValue();

            // Consumo base del producto
            BigDecimal consumoPorPrenda = producto.getConsumoBaseMetros();

            // Agregar mangas si aplica
            if (Boolean.TRUE.equals(producto.getIncluyeMangas()) && producto.getConsumoMangasMetros() != null) {
                consumoPorPrenda = consumoPorPrenda.add(producto.getConsumoMangasMetros());
            }

            // Agregar "otro" si aplica
            if (Boolean.TRUE.equals(producto.getIncluyeOtro()) && producto.getConsumoOtroMetros() != null) {
                consumoPorPrenda = consumoPorPrenda.add(producto.getConsumoOtroMetros());
            }

            // Aplicar ajuste por talla si existe
            BigDecimal ajuste = obtenerAjusteParaTalla(producto, talla);
            consumoPorPrenda = consumoPorPrenda.add(ajuste);

            // Multiplicar por cantidad
            BigDecimal consumoPorTalla = consumoPorPrenda.multiply(new BigDecimal(cantidad));
            consumoTotal = consumoTotal.add(consumoPorTalla);
        }

        return consumoTotal;
    }

    /**
     * Obtener ajuste de metros para una talla específica
     */
    private BigDecimal obtenerAjusteParaTalla(Producto producto, String talla) {
        if (producto.getAjustesTalla() == null || producto.getAjustesTalla().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return producto.getAjustesTalla().stream()
                .filter(ajuste -> ajuste.getTalla().equalsIgnoreCase(talla))
                .map(ProductoAjusteTalla::getAjusteMetros)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Obtener todos los ajustes de talla de un producto
     */
    public Map<String, BigDecimal> obtenerAjustesTalla(Long productoId) {
        Producto producto = obtenerProductoPorId(productoId);

        if (producto.getAjustesTalla() == null) {
            return Collections.emptyMap();
        }

        return producto.getAjustesTalla().stream()
                .collect(Collectors.toMap(
                        ProductoAjusteTalla::getTalla,
                        ProductoAjusteTalla::getAjusteMetros));
    }
}
