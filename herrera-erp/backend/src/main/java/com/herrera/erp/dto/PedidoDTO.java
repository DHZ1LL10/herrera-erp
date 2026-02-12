package com.herrera.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para pedidos (folios)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long id;
    private String folio;

    @NotBlank(message = "El nombre del pedido es requerido")
    private String nombrePedido;

    @NotBlank(message = "El nombre del cliente es requerido")
    private String clienteNombre;

    private String clienteTelefono;
    private String clienteEmail;

    @NotNull(message = "La fecha de pedido es requerida")
    private LocalDate fechaPedido;

    @NotNull(message = "La fecha de entrega es requerida")
    private LocalDate fechaEntrega;

    private String prioridad; // ESTANDAR, PREFERENCIAL
    private String tipo; // SENCILLO, DOBLE

    @NotNull(message = "El producto es requerido")
    private Long productoId;
    private String productoNombre;

    private String colorPrincipal;
    private String colorHexPrincipal;

    private Integer totalPiezas;
    private BigDecimal totalTelaEstimada;
    private String observaciones;
    private String estado; // PENDIENTE, EN_CORTE, EN_COSTURA, EN_ACABADOS, LISTO, ENTREGADO, CANCELADO

    private Long usuarioCreadorId;
    private String usuarioCreadorNombre;
    private String ubicacionOrigen; // TALLER, LOCAL

    // Items del pedido
    private List<PedidoItemDTO> items;

    // Imágenes del pedido
    private List<String> imagenesUrls;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PedidoItemDTO {
        private Long id;
        private String talla;
        private String nombreJugador;
        private String numeroEspalda;
        private String colorEspecial;
        private String colorHexEspecial;
        private Boolean tieneColorEspecial;
        private Integer ordenTalla;
    }
}
