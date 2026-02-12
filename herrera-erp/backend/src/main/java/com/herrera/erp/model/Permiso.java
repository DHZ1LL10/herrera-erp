package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad Permiso - Define qué puede hacer cada rol en cada módulo
 */
@Entity
@Table(name = "permisos", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "rol_id", "modulo" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(nullable = false, length = 50)
    private String modulo;

    @Column(name = "puede_crear")
    @Builder.Default
    private Boolean puedeCrear = false;

    @Column(name = "puede_leer")
    @Builder.Default
    private Boolean puedeLeer = false;

    @Column(name = "puede_editar")
    @Builder.Default
    private Boolean puedeEditar = false;

    @Column(name = "puede_eliminar")
    @Builder.Default
    private Boolean puedeEliminar = false;

    @Override
    public String toString() {
        return "Permiso{modulo='" + modulo + "', rol=" + rol.getNombre() + "}";
    }
}