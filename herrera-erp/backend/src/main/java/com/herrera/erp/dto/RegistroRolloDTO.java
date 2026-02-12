package com.herrera.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para registro de nuevos rollos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRolloDTO {

    @NotNull(message = "El material es requerido")
    private Long materialId;

    @NotBlank(message = "El código del rollo es requerido")
    private String codigoRollo;

    @NotNull(message = "Los metros iniciales son requeridos")
    @Positive(message = "Los metros deben ser positivos")
    private BigDecimal metrosIniciales;

    @NotBlank(message = "El destino es requerido")
    private String destino; // CORTE, VENTA, MIXTO

    @NotNull(message = "La fecha de entrada es requerida")
    private LocalDate fechaEntrada;

    private String proveedor;

    private BigDecimal precioCompra;
}
