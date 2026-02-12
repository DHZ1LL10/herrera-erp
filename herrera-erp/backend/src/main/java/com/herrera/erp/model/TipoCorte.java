package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad TipoCorte - Define categorías de productos (Futbol, Basquet, etc.)
 * Ubicación: backend/src/main/java/com/herrera/erp/model/TipoCorte.java
 */
@Entity
@Table(name = "tipos_corte")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoCorte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tallas_disponibles", columnDefinition = "TEXT[]")
    private String[] tallasDisponibles;

    @Builder.Default
    private Boolean activo = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TipoCorte{id=" + id + ", nombre='" + nombre + "'}";
    }
}