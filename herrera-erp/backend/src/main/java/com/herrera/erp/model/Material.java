package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Material - Inventario unificado (telas, vinil, hilos, clones)
 */
@Entity
@Table(name = "materiales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_material_id")
    private TipoMaterial tipoMaterial;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 50)
    private String color;

    @Column(length = 10)
    private String talla;

    @Column(name = "stock_actual", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal stockActual = BigDecimal.ZERO;

    @Column(name = "stock_minimo", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Column(name = "stock_critico", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal stockCritico = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Prioridad prioridad;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Builder.Default
    private Boolean activo = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============================================
    // ENUMS
    // ============================================

    public enum Prioridad {
        ALTA,
        MEDIA,
        BAJA
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    public String getNivelAlerta() {
        if (stockActual.compareTo(stockCritico) <= 0) {
            return "CRITICO";
        } else if (stockActual.compareTo(stockMinimo) <= 0) {
            return "BAJO";
        }
        return "NORMAL";
    }

    public boolean tieneStockBajo() {
        return stockActual.compareTo(stockMinimo) <= 0;
    }

    public boolean tieneStockCritico() {
        return stockActual.compareTo(stockCritico) <= 0;
    }

    // ============================================
    // LIFECYCLE CALLBACKS
    // ============================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Material{id=" + id + ", nombre='" + nombre + "', color='" + color +
                "', stock=" + stockActual + "}";
    }
}