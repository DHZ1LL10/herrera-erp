package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad TipoMaterial - TELA, VINIL, HILO, CLON, ACCESORIO
 */
@Entity
@Table(name = "tipos_material")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private UnidadMedida unidadMedida;

    public enum UnidadMedida {
        METROS,
        PIEZAS,
        KILOS,
        CONOS
    }

    @Override
    public String toString() {
        return "TipoMaterial{id=" + id + ", nombre='" + nombre + "', unidad=" + unidadMedida + "}";
    }
}