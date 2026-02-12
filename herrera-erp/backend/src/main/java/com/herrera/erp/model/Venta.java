package com.herrera.erp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Venta - Ventas directas en punto de venta
 * Ubicación: backend/src/main/java/com/herrera/erp/model/Venta.java
 */
@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "folio_venta", unique = true, nullable = false, length = 50)
    private String folioVenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venta", length = 20)
    private TipoVenta tipoVenta;

    @Column(name = "cliente_nombre", length = 200)
    private String clienteNombre;

    @Column(name = "cliente_telefono", length = 20)
    private String clienteTelefono;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20)
    private MetodoPago metodoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_vendedor_id")
    private Usuario usuarioVendedor;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private UbicacionVenta ubicacion;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VentaItem> items = new ArrayList<>();

    // ============================================
    // ENUMS
    // ============================================

    public enum TipoVenta {
        CLON,
        TELA_METROS,
        VINIL,
        OTRO
    }

    public enum MetodoPago {
        EFECTIVO,
        TARJETA,
        TRANSFERENCIA
    }

    public enum UbicacionVenta {
        TALLER,
        LOCAL
    }

    @PrePersist
    protected void onCreate() {
        if (fechaVenta == null) {
            fechaVenta = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Venta{folio='" + folioVenta + "', total=" + total + ", ubicacion=" + ubicacion + "}";
    }
}