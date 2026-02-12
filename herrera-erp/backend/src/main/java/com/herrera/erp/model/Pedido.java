package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Pedido - Folio principal del pedido
 * Ubicación: backend/src/main/java/com/herrera/erp/model/Pedido.java
 */
@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String folio;

    @Column(name = "nombre_pedido", nullable = false, length = 200)
    private String nombrePedido;

    @Column(name = "cliente_nombre", nullable = false, length = 200)
    private String clienteNombre;

    @Column(name = "cliente_telefono", length = 20)
    private String clienteTelefono;

    @Column(name = "cliente_email", length = 100)
    private String clienteEmail;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDate fechaPedido;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    @Builder.Default
    private Prioridad prioridad = Prioridad.ESTANDAR;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private Tipo tipo = Tipo.SENCILLO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "color_principal", length = 50)
    private String colorPrincipal;

    @Column(name = "color_hex_principal", length = 7)
    private String colorHexPrincipal;

    @Column(name = "total_piezas")
    @Builder.Default
    private Integer totalPiezas = 0;

    @Column(name = "total_tela_estimada", precision = 10, scale = 2)
    private BigDecimal totalTelaEstimada;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Estado estado = Estado.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id")
    private Usuario usuarioCreador;

    @Enumerated(EnumType.STRING)
    @Column(name = "ubicacion_origen", length = 10)
    private UbicacionOrigen ubicacionOrigen;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relaciones
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PedidoItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PedidoImagen> imagenes = new ArrayList<>();

    // ============================================
    // ENUMS
    // ============================================

    public enum Prioridad {
        ESTANDAR,
        PREFERENCIAL
    }

    public enum Tipo {
        SENCILLO,
        DOBLE
    }

    public enum Estado {
        PENDIENTE,
        EN_CORTE,
        EN_COSTURA,
        EN_ACABADOS,
        LISTO,
        ENTREGADO,
        CANCELADO
    }

    public enum UbicacionOrigen {
        TALLER,
        LOCAL
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    public boolean estaRetrasado() {
        return estado != Estado.ENTREGADO &&
                estado != Estado.CANCELADO &&
                fechaEntrega.isBefore(LocalDate.now());
    }

    public boolean esUrgente() {
        return prioridad == Prioridad.PREFERENCIAL ||
                fechaEntrega.isBefore(LocalDate.now().plusDays(3));
    }

    public long diasParaEntrega() {
        return LocalDate.now().until(fechaEntrega).getDays();
    }

    // ============================================
    // LIFECYCLE CALLBACKS
    // ============================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaPedido == null) {
            fechaPedido = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Pedido{folio='" + folio + "', cliente='" + clienteNombre +
                "', estado=" + estado + ", piezas=" + totalPiezas + "}";
    }
}