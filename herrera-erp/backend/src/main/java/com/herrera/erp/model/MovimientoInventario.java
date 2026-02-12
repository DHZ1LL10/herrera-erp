package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad MovimientoInventario - Historial completo de entradas/salidas
 */
@Entity
@Table(name = "movimientos_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rollo_id")
    private Rollo rollo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "stock_anterior", precision = 10, scale = 2)
    private BigDecimal stockAnterior;

    @Column(name = "stock_nuevo", precision = 10, scale = 2)
    private BigDecimal stockNuevo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String motivo;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();

    // ============================================
    // ENUMS
    // ============================================

    public enum TipoMovimiento {
        ENTRADA, // Compra de material nuevo
        SALIDA_CORTE, // Salida para cortar pedido
        SALIDA_VENTA, // Venta directa al público
        AJUSTE, // Ajuste manual de inventario
        MERMA // Pérdida de material
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    public boolean esEntrada() {
        return tipoMovimiento == TipoMovimiento.ENTRADA;
    }

    public boolean esSalida() {
        return tipoMovimiento == TipoMovimiento.SALIDA_CORTE ||
                tipoMovimiento == TipoMovimiento.SALIDA_VENTA ||
                tipoMovimiento == TipoMovimiento.MERMA;
    }

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "MovimientoInventario{id=" + id + ", tipo=" + tipoMovimiento +
                ", cantidad=" + cantidad + ", material=" + material.getNombre() + "}";
    }
}