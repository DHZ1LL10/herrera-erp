package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Entidad CostoPedido - Control de costos y rentabilidad
 * Ubicación: backend/src/main/java/com/herrera/erp/model/CostoPedido.java
 */
@Entity
@Table(name = "costos_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", unique = true, nullable = false)
    private Pedido pedido;

    // ============================================
    // COSTOS INDIVIDUALES
    // ============================================

    @Column(name = "costo_tela", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoTela = BigDecimal.ZERO;

    @Column(name = "costo_vinil", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoVinil = BigDecimal.ZERO;

    @Column(name = "costo_hilo", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoHilo = BigDecimal.ZERO;

    @Column(name = "costo_maquila", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoMaquila = BigDecimal.ZERO;

    @Column(name = "costo_varios", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoVarios = BigDecimal.ZERO;

    // ============================================
    // TOTALES CALCULADOS
    // ============================================

    @Column(name = "total_costo", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalCosto = BigDecimal.ZERO;

    @Column(name = "precio_venta", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal precioVenta = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal utilidad = BigDecimal.ZERO;

    @Column(name = "margen_porcentaje", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal margenPorcentaje = BigDecimal.ZERO;

    // ============================================
    // NOTAS Y AUDITORÍA
    // ============================================

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============================================
    // MÉTODOS CALCULADOS
    // ============================================

    /**
     * Calcula el total de costos sumando todos los componentes
     */
    public BigDecimal calcularTotalCosto() {
        return (costoTela != null ? costoTela : BigDecimal.ZERO)
                .add(costoVinil != null ? costoVinil : BigDecimal.ZERO)
                .add(costoHilo != null ? costoHilo : BigDecimal.ZERO)
                .add(costoMaquila != null ? costoMaquila : BigDecimal.ZERO)
                .add(costoVarios != null ? costoVarios : BigDecimal.ZERO);
    }

    /**
     * Calcula la utilidad: precio_venta - total_costo
     */
    public BigDecimal calcularUtilidad() {
        BigDecimal precio = precioVenta != null ? precioVenta : BigDecimal.ZERO;
        BigDecimal costo = totalCosto != null ? totalCosto : BigDecimal.ZERO;
        return precio.subtract(costo);
    }

    /**
     * Calcula el margen de ganancia en porcentaje
     */
    public BigDecimal calcularMargen() {
        if (precioVenta == null || precioVenta.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal util = utilidad != null ? utilidad : BigDecimal.ZERO;
        return util.divide(precioVenta, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Verifica si el pedido es rentable (utilidad > 0)
     */
    public boolean esRentable() {
        return utilidad != null && utilidad.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Obtiene el nivel de alerta según el margen
     */
    public NivelAlerta getNivelAlerta() {
        if (utilidad != null && utilidad.compareTo(BigDecimal.ZERO) < 0) {
            return NivelAlerta.PERDIDA;
        }

        if (margenPorcentaje == null) {
            return NivelAlerta.SIN_DATOS;
        }

        if (margenPorcentaje.compareTo(BigDecimal.valueOf(25)) > 0) {
            return NivelAlerta.EXCELENTE;
        } else if (margenPorcentaje.compareTo(BigDecimal.valueOf(10)) >= 0) {
            return NivelAlerta.NORMAL;
        } else {
            return NivelAlerta.BAJO;
        }
    }

    // ============================================
    // LIFECYCLE CALLBACKS
    // ============================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        recalcularTotales();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        recalcularTotales();
    }

    /**
     * Recalcula automáticamente todos los totales
     * NOTA: En producción los triggers de BD harán esto,
     * pero es útil tenerlo también en la entidad para testing
     */
    private void recalcularTotales() {
        this.totalCosto = calcularTotalCosto();
        this.utilidad = calcularUtilidad();
        this.margenPorcentaje = calcularMargen();
    }

    // ============================================
    // ENUMS
    // ============================================

    public enum NivelAlerta {
        EXCELENTE, // Margen > 25%
        NORMAL, // Margen 10-25%
        BAJO, // Margen < 10%
        PERDIDA, // Utilidad negativa
        SIN_DATOS // No hay información suficiente
    }

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================

    @Override
    public String toString() {
        return "CostoPedido{" +
                "id=" + id +
                ", pedidoId=" + (pedido != null ? pedido.getId() : null) +
                ", totalCosto=" + totalCosto +
                ", precioVenta=" + precioVenta +
                ", utilidad=" + utilidad +
                ", margen=" + margenPorcentaje + "%" +
                '}';
    }
}
