package com.herrera.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para movimientos de inventario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {

    private Long id;
    private Long materialId;
    private String materialNombre;
    private Long rolloId;
    private String codigoRollo;
    private String tipoMovimiento; // ENTRADA, SALIDA_CORTE, SALIDA_VENTA, AJUSTE, MERMA
    private BigDecimal cantidad;
    private BigDecimal stockAnterior;
    private BigDecimal stockNuevo;
    private String motivo;
    private Long pedidoId;
    private String folioPedido;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fecha;
}
