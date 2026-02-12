package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entidad ProductoAjusteTalla - Ajustes de consumo por talla
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/model/ProductoAjusteTalla.java
 */
@Entity
@Table(name = "producto_ajustes_talla", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "producto_id", "talla" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoAjusteTalla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, length = 10)
    private String talla;

    @Column(name = "ajuste_metros", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal ajusteMetros = BigDecimal.ZERO;

    @Override
    public String toString() {
        return "AjusteTalla{talla='" + talla + "', ajuste=" + ajusteMetros + "m}";
    }
}