package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entidad VentaItem - Detalle de cada ítem vendido
 * Ubicación: backend/src/main/java/com/herrera/erp/model/VentaItem.java
 */
@Entity
@Table(name = "venta_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id")
    private Material material;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Override
    public String toString() {
        return "VentaItem{material=" + material.getNombre() + ", cantidad=" + cantidad + "}";
    }
}