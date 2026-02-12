package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Producto - Plantillas configurables con consumo de tela
 * Ubicación: backend/src/main/java/com/herrera/erp/model/Producto.java
 */
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // NOTA: TipoCorte está en la BD pero no se usa en el MVP
    // Descomentar cuando se necesite en futuras versiones
    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "tipo_corte_id")
    // private TipoCorte tipoCorte;

    @Column(name = "consumo_base_metros", precision = 10, scale = 2, nullable = false)
    private BigDecimal consumoBaseMetros;

    @Column(name = "incluye_mangas")
    @Builder.Default
    private Boolean incluyeMangas = false;

    @Column(name = "consumo_mangas_metros", precision = 10, scale = 2)
    private BigDecimal consumoMangasMetros;

    @Column(name = "incluye_otro")
    @Builder.Default
    private Boolean incluyeOtro = false;

    @Column(name = "consumo_otro_metros", precision = 10, scale = 2)
    private BigDecimal consumoOtroMetros;

    @Column(name = "descripcion_otro", columnDefinition = "TEXT")
    private String descripcionOtro;

    @Builder.Default
    private Boolean activo = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con ajustes por talla
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<ProductoAjusteTalla> ajustesTalla = new HashSet<>();

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    /**
     * Calcula el consumo total de tela para una talla específica
     */
    public BigDecimal calcularConsumoParaTalla(String talla) {
        BigDecimal consumoTotal = consumoBaseMetros;

        // Buscar ajuste de talla
        ProductoAjusteTalla ajuste = ajustesTalla.stream()
                .filter(a -> a.getTalla().equalsIgnoreCase(talla))
                .findFirst()
                .orElse(null);

        if (ajuste != null) {
            consumoTotal = consumoTotal.add(ajuste.getAjusteMetros());
        }

        // Agregar mangas si aplica
        if (Boolean.TRUE.equals(incluyeMangas) && consumoMangasMetros != null) {
            consumoTotal = consumoTotal.add(consumoMangasMetros);
        }

        // Agregar "otro" si aplica
        if (Boolean.TRUE.equals(incluyeOtro) && consumoOtroMetros != null) {
            consumoTotal = consumoTotal.add(consumoOtroMetros);
        }

        return consumoTotal;
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
        return "Producto{id=" + id + ", nombre='" + nombre + "', consumoBase=" + consumoBaseMetros + "m}";
    }
}