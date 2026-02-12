package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad PedidoItem - Detalle de cada línea del folio (talla, nombre, número)
 * Ubicación: backend/src/main/java/com/herrera/erp/model/PedidoItem.java
 */
@Entity
@Table(name = "pedido_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false, length = 10)
    private String talla;

    @Column(name = "nombre_jugador", length = 100)
    private String nombreJugador;

    @Column(name = "numero_espalda", length = 10)
    private String numeroEspalda;

    @Column(name = "color_especial", length = 50)
    private String colorEspecial;

    @Column(name = "color_hex_especial", length = 7)
    private String colorHexEspecial;

    @Column(name = "tiene_color_especial")
    @Builder.Default
    private Boolean tieneColorEspecial = false;

    @Column(name = "orden_talla")
    private Integer ordenTalla;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "PedidoItem{talla='" + talla + "', nombre='" + nombreJugador +
                "', numero='" + numeroEspalda + "'}";
    }
}