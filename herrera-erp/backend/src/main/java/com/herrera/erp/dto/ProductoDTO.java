package com.herrera.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para productos (plantillas configurables)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long id;

    @NotBlank(message = "El nombre del producto es requerido")
    private String nombre;

    @NotNull(message = "El tipo de corte es requerido")
    private Long tipoCorteId;
    private String tipoCorteNombre;

    @NotNull(message = "El consumo base es requerido")
    @Positive(message = "El consumo base debe ser positivo")
    private BigDecimal consumoBaseMetros;

    private Boolean incluyeMangas; // Cambio: de incluye_mangas a incluyeMangas
    private BigDecimal consumoMangasMetros;

    private Boolean incluyeOtro;
    private BigDecimal consumoOtroMetros;
    private String descripcionOtro;

    private Boolean activo;

    // Ajustes por talla
    private List<AjusteTallaDTO> ajustesTalla;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AjusteTallaDTO {
        private Long id;
        private String talla;
        private BigDecimal ajusteMetros;
    }
}
