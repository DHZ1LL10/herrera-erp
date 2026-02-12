package com.herrera.erp.service;

import com.herrera.erp.dto.CostoPedidoDTO;
import com.herrera.erp.dto.RegistrarCostoRequest;
import com.herrera.erp.dto.ReporteUtilidadDTO;
import com.herrera.erp.exception.ResourceNotFoundException;
import com.herrera.erp.model.CostoPedido;
import com.herrera.erp.model.Pedido;
import com.herrera.erp.repository.CostoPedidoRepository;
import com.herrera.erp.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de costos de pedidos
 * Incluye lógica de negocio para cálculos y reportes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CostoService {

    private final CostoPedidoRepository costoRepository;
    private final PedidoRepository pedidoRepository;

    /**
     * Registrar o actualizar costos de un pedido
     */
    @Transactional
    public CostoPedidoDTO registrarCostos(RegistrarCostoRequest request) {
        log.info("Registrando costos para pedido ID: {}", request.getPedidoId());

        // Verificar que el pedido existe
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado con ID: " + request.getPedidoId()));

        // Verificar si ya existen costos para este pedido
        CostoPedido costo = costoRepository.findByPedidoId(request.getPedidoId())
                .orElse(new CostoPedido());

        // Si es nuevo, asociar el pedido
        if (costo.getId() == null) {
            costo.setPedido(pedido);
            log.info("Creando nuevo registro de costos para pedido: {}", pedido.getFolio());
        } else {
            log.info("Actualizando costos existentes para pedido: {}", pedido.getFolio());
        }

        // Actualizar valores
        costo.setCostoTela(request.getCostoTela());
        costo.setCostoVinil(request.getCostoVinil());
        costo.setCostoHilo(request.getCostoHilo());
        costo.setCostoMaquila(request.getCostoMaquila());
        costo.setCostoVarios(request.getCostoVarios());
        costo.setPrecioVenta(request.getPrecioVenta());
        costo.setNotas(request.getNotas());

        // Los totales se calculan automáticamente en el trigger de BD y en
        // @PrePersist/@PreUpdate
        CostoPedido savedCosto = costoRepository.save(costo);

        log.info("Costos guardados exitosamente. Utilidad: {}, Margen: {}%",
                savedCosto.getUtilidad(), savedCosto.getMargenPorcentaje());

        return convertToDTO(savedCosto);
    }

    /**
     * Obtener costos de un pedido específico
     */
    @Transactional(readOnly = true)
    public CostoPedidoDTO obtenerCostosPorPedido(Long pedidoId) {
        log.debug("Obteniendo costos para pedido ID: {}", pedidoId);

        CostoPedido costo = costoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontraron costos registrados para el pedido ID: " + pedidoId));

        return convertToDTO(costo);
    }

    /**
     * Listar todos los costos con paginación
     */
    @Transactional(readOnly = true)
    public Page<CostoPedidoDTO> listarTodos(Pageable pageable) {
        log.debug("Listando todos los costos con paginación");
        return costoRepository.findAllWithPagination(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Eliminar costos de un pedido
     */
    @Transactional
    public void eliminarCostos(Long id) {
        log.info("Eliminando costos ID: {}", id);

        if (!costoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Costos no encontrados con ID: " + id);
        }

        costoRepository.deleteById(id);
        log.info("Costos eliminados exitosamente");
    }

    /**
     * Obtener pedidos con pérdida (utilidad negativa)
     */
    @Transactional(readOnly = true)
    public List<CostoPedidoDTO> obtenerPedidosConPerdida() {
        log.debug("Obteniendo pedidos con pérdida");
        return costoRepository.findPedidosConPerdida().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener los pedidos más rentables
     */
    @Transactional(readOnly = true)
    public List<CostoPedidoDTO> obtenerPedidosMasRentables(int limite) {
        log.debug("Obteniendo top {} pedidos más rentables", limite);
        Pageable pageable = PageRequest.of(0, limite);
        return costoRepository.findPedidosMasRentables(pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Generar reporte de utilidades por periodo
     */
    @Transactional(readOnly = true)
    public ReporteUtilidadDTO generarReportePeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Generando reporte de utilidades del {} al {}", fechaInicio, fechaFin);

        // Obtener todos los costos del periodo
        List<CostoPedido> costosDelPeriodo = costoRepository.findByPeriodo(fechaInicio, fechaFin);

        // Calcular totales agregados
        BigDecimal totalVentas = costoRepository.sumVentasByPeriodo(fechaInicio, fechaFin);
        BigDecimal totalCostos = costoRepository.sumCostosByPeriodo(fechaInicio, fechaFin);
        BigDecimal utilidadTotal = costoRepository.sumUtilidadByPeriodo(fechaInicio, fechaFin);
        BigDecimal margenPromedio = costoRepository.avgMargenByPeriodo(fechaInicio, fechaFin);

        // Contar pedidos
        Long pedidosRentables = costoRepository.countPedidosRentables();
        Long pedidosConPerdida = costoRepository.countPedidosConPerdida();

        // Obtener top pedidos rentables (límite 10)
        List<CostoPedidoDTO> topRentables = obtenerPedidosMasRentables(10);

        // Obtener pedidos con pérdida
        List<CostoPedidoDTO> conPerdida = obtenerPedidosConPerdida();

        // Métricas adicionales
        BigDecimal utilidadMasAlta = costoRepository.findMaxUtilidad();
        BigDecimal perdidaMasAlta = costoRepository.findMaxPerdida();
        BigDecimal margenMasAlto = costoRepository.findMaxMargen();

        // Contar pedidos del periodo sin costos registrados
        int totalPedidosEnPeriodo = pedidoRepository
                .findByFechaPedidoBetween(fechaInicio, fechaFin).size();
        int pedidosSinCostos = totalPedidosEnPeriodo - costosDelPeriodo.size();

        return ReporteUtilidadDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .totalVentas(totalVentas)
                .totalCostos(totalCostos)
                .utilidadTotal(utilidadTotal)
                .margenPromedio(margenPromedio)
                .totalPedidos(totalPedidosEnPeriodo)
                .pedidosRentables(pedidosRentables.intValue())
                .pedidosConPerdidaCount(pedidosConPerdida.intValue()) // Nombre correcto
                .pedidosSinCostos(pedidosSinCostos)
                .topPedidosRentables(topRentables)
                .listaPedidosConPerdida(conPerdida)
                .utilidadMasAlta(utilidadMasAlta != null ? utilidadMasAlta : BigDecimal.ZERO)
                .perdidaMasAlta(perdidaMasAlta != null ? perdidaMasAlta : BigDecimal.ZERO)
                .margenMasAlto(margenMasAlto != null ? margenMasAlto : BigDecimal.ZERO)
                .build();
    }

    /**
     * Verificar si un pedido tiene costos registrados
     */
    @Transactional(readOnly = true)
    public boolean tieneCostosRegistrados(Long pedidoId) {
        return costoRepository.existsByPedidoId(pedidoId);
    }

    // ============================================
    // MÉTODOS AUXILIARES - MAPPERS
    // ============================================

    /**
     * Convertir entidad CostoPedido a DTO
     */
    private CostoPedidoDTO convertToDTO(CostoPedido costo) {
        Pedido pedido = costo.getPedido();

        return CostoPedidoDTO.builder()
                .id(costo.getId())
                .pedidoId(pedido.getId())
                .folioPedido(pedido.getFolio())
                .nombrePedido(pedido.getNombrePedido())
                .clienteNombre(pedido.getClienteNombre())
                .costoTela(costo.getCostoTela())
                .costoVinil(costo.getCostoVinil())
                .costoHilo(costo.getCostoHilo())
                .costoMaquila(costo.getCostoMaquila())
                .costoVarios(costo.getCostoVarios())
                .totalCosto(costo.getTotalCosto())
                .precioVenta(costo.getPrecioVenta())
                .utilidad(costo.getUtilidad())
                .margenPorcentaje(costo.getMargenPorcentaje())
                .esRentable(costo.esRentable())
                .nivelAlerta(costo.getNivelAlerta().name())
                .notas(costo.getNotas())
                .createdAt(costo.getCreatedAt())
                .updatedAt(costo.getUpdatedAt())
                .build();
    }
}
