package com.herrera.erp.controller;

import com.herrera.erp.dto.ProductoDTO;
import com.herrera.erp.model.Producto;
import com.herrera.erp.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller de Productos
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * GET /api/productos
     * Listar todos los productos activos
     */
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = productoService.obtenerProductosActivos();
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/{id}
     * Obtener producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * POST /api/productos
     * Crear nuevo producto
     */
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        Producto producto = productoService.crearProducto(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    /**
     * PUT /api/productos/{id}
     * Actualizar producto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDTO) {
        Producto producto = productoService.actualizarProducto(id, productoDTO);
        return ResponseEntity.ok(producto);
    }

    /**
     * DELETE /api/productos/{id}
     * Desactivar producto (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/productos/{id}/calcular-consumo
     * Calcular consumo de tela para un pedido
     * Body: { "tallas": { "M": 5, "L": 10, "XL": 3 } }
     */
    @PostMapping("/{id}/calcular-consumo")
    public ResponseEntity<Map<String, Object>> calcularConsumo(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> tallasConCantidades) {

        BigDecimal consumoTotal = productoService.calcularConsumoTela(id, tallasConCantidades);

        return ResponseEntity.ok(Map.of(
                "productoId", id,
                "consumoTotalMetros", consumoTotal,
                "tallas", tallasConCantidades));
    }

    /**
     * GET /api/productos/{id}/ajustes-talla
     * Obtener ajustes de talla de un producto
     */
    @GetMapping("/{id}/ajustes-talla")
    public ResponseEntity<Map<String, BigDecimal>> obtenerAjustesTalla(@PathVariable Long id) {
        Map<String, BigDecimal> ajustes = productoService.obtenerAjustesTalla(id);
        return ResponseEntity.ok(ajustes);
    }
}
