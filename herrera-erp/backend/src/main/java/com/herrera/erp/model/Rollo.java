package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Rollo - Control específico de rollos de tela con destino
 */
@Entity
@Table(name = "rollos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rollo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id")
    private Material material;

    @Column(name = "codigo_rollo", unique = true, nullable = false, length = 50)
    private String codigoRollo;

    @Column(name = "metros_iniciales", precision = 10, scale = 2, nullable = false)
    private BigDecimal metrosIniciales;

    @Column(name = "metros_actuales", precision = 10, scale = 2, nullable = false)
    private BigDecimal metrosActuales;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Destino destino;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(length = 100)
    private String proveedor;

    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Builder.Default
    private Boolean activo = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ============================================
    // ENUMS
    // ============================================

    public enum Destino {
        CORTE, // Solo para producción
        VENTA, // Solo para venta directa
        MIXTO // Puede usarse para ambos
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    public BigDecimal getPorcentajeRestante() {
        if (metrosIniciales.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return metrosActuales
                .multiply(BigDecimal.valueOf(100))
                .divide(metrosIniciales, 2, RoundingMode.HALF_UP);
    }

    public boolean estaVacio() {
        return metrosActuales.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean puedeUsarseParaCorte() {
        return destino == Destino.CORTE || destino == Destino.MIXTO;
    }

    public boolean puedeUsarseParaVenta() {
        return destino == Destino.VENTA || destino == Destino.MIXTO;
    }

    // ============================================
    // LIFECYCLE CALLBACKS
    // ============================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (metrosActuales == null) {
            metrosActuales = metrosIniciales;
        }
    }

    @Override
    public String toString() {
        return "Rollo{codigo='" + codigoRollo + "', metros=" + metrosActuales +
                "/" + metrosIniciales + ", destino=" + destino + "}";
    }
}