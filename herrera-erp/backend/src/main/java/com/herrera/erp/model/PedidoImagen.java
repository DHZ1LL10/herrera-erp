package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad PedidoImagen - Imágenes asociadas al pedido (Cloudinary)
 * Ubicación: backend/src/main/java/com/herrera/erp/model/PedidoImagen.java
 */
@Entity
@Table(name = "pedido_imagenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "nombre_archivo", nullable = false, length = 200)
    private String nombreArchivo;

    @Column(name = "url_cloudinary", nullable = false, columnDefinition = "TEXT")
    private String urlCloudinary;

    @Column(name = "public_id_cloudinary", length = 200)
    private String publicIdCloudinary;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoImagen tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "es_principal")
    @Builder.Default
    private Boolean esPrincipal = false;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    // ============================================
    // ENUMS
    // ============================================

    public enum TipoImagen {
        DISEÑO_FINAL,
        LOGO,
        REFERENCIA,
        OTRO
    }

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "PedidoImagen{nombre='" + nombreArchivo + "', tipo=" + tipo + "}";
    }
}