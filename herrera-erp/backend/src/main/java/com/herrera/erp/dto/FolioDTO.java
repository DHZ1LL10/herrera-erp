package com.herrera.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para impresión de folios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolioDTO {

    private String folio;
    private String nombrePedido;
    private String clienteNombre;
    private String clienteTelefono;
    private LocalDate fechaPedido;
    private LocalDate fechaEntrega;
    private String prioridad;
    private String tipo; // SENCILLO, DOBLE

    private String productoNombre;
    private String colorPrincipal;
    private String colorHexPrincipal;

    private Integer totalPiezas;
    private BigDecimal totalTelaEstimada;
    private String observaciones;

    private List<ItemFolioDTO> items;
    private List<String> imagenesUrls;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemFolioDTO {
        private String talla;
        private String nombreJugador;
        private String numeroEspalda;
        private String colorEspecial;
        private Boolean tieneColorEspecial;
    }
}
