package com.herrera.erp.service;

import com.herrera.erp.model.*;
import com.herrera.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de Inventario
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/service/InventarioService.java
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final MaterialRepository materialRepository;
    private final RolloRepository rolloRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    // ============================================
    // GESTIÓN DE MATERIALES
    // ============================================

    public List<Material> obtenerTodosMateriales() {
        return materialRepository.findByActivoTrue();
    }

    public Material obtenerMaterialPorId(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));
    }

    public List<Material> obtenerMaterialesConAlerta() {
        return materialRepository.findMaterialesStockBajo();
    }

    public List<Material> obtenerMaterialesCriticos() {
        return materialRepository.findMaterialesStockCritico();
    }

    @Transactional
    public Material crearMaterial(Material material) {
        log.info("Creando material: {}", material.getNombre());
        return materialRepository.save(material);
    }

    // ============================================
    // GESTIÓN DE ROLLOS
    // ============================================

    public List<Rollo> obtenerRollosDisponibles() {
        return rolloRepository.findRollosDisponibles();
    }

    public List<Rollo> obtenerRollosParaCorte() {
        return rolloRepository.findRollosDisponiblesParaCorte();
    }

    public List<Rollo> obtenerRollosParaVenta() {
        return rolloRepository.findRollosDisponiblesParaVenta();
    }

    @Transactional
    public Rollo registrarRollo(Rollo rollo, Long usuarioId) {
        log.info("Registrando rollo: {} - {} metros - Destino: {}",
                rollo.getCodigoRollo(), rollo.getMetrosIniciales(), rollo.getDestino());

        // Verificar que no exista el código
        if (rolloRepository.existsByCodigoRollo(rollo.getCodigoRollo())) {
            throw new RuntimeException("Ya existe un rollo con ese código");
        }

        // Guardar rollo
        Rollo rolloGuardado = rolloRepository.save(rollo);

        // Registrar movimiento de entrada
        registrarMovimiento(
                rollo.getMaterial().getId(),
                rollo.getId(),
                MovimientoInventario.TipoMovimiento.ENTRADA,
                rollo.getMetrosIniciales(),
                "Entrada de rollo nuevo: " + rollo.getCodigoRollo(),
                null,
                usuarioId);

        log.info("Rollo registrado exitosamente: {}", rollo.getCodigoRollo());
        return rolloGuardado;
    }

    // ============================================
    // MOVIMIENTOS DE INVENTARIO
    // ============================================

    @Transactional
    public MovimientoInventario registrarMovimiento(
            Long materialId,
            Long rolloId,
            MovimientoInventario.TipoMovimiento tipo,
            BigDecimal cantidad,
            String motivo,
            Long pedidoId,
            Long usuarioId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));

        Rollo rollo = null;
        if (rolloId != null) {
            rollo = rolloRepository.findById(rolloId)
                    .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));
        }

        // Guardar stock anterior
        BigDecimal stockAnterior = material.getStockActual();

        // Calcular cantidad con signo correcto
        BigDecimal cantidadFinal = cantidad;
        if (tipo == MovimientoInventario.TipoMovimiento.SALIDA_CORTE ||
                tipo == MovimientoInventario.TipoMovimiento.SALIDA_VENTA ||
                tipo == MovimientoInventario.TipoMovimiento.MERMA) {
            cantidadFinal = cantidad.negate();
        }

        // Actualizar stock del material
        BigDecimal nuevoStock = material.getStockActual().add(cantidadFinal);
        if (nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + material.getStockActual());
        }
        material.setStockActual(nuevoStock);
        materialRepository.save(material);

        // Actualizar metros del rollo si aplica
        if (rollo != null) {
            BigDecimal nuevosMetros = rollo.getMetrosActuales().add(cantidadFinal);
            if (nuevosMetros.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Rollo sin metros suficientes");
            }
            rollo.setMetrosActuales(nuevosMetros);
            rolloRepository.save(rollo);
        }

        // Crear movimiento
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .material(material)
                .rollo(rollo)
                .tipoMovimiento(tipo)
                .cantidad(cantidadFinal)
                .stockAnterior(stockAnterior)
                .stockNuevo(nuevoStock)
                .motivo(motivo)
                .pedidoId(pedidoId)
                .usuario(usuarioId != null ? new Usuario() : null)
                .fecha(LocalDateTime.now())
                .build();

        if (usuarioId != null) {
            movimiento.getUsuario().setId(usuarioId);
        }

        MovimientoInventario movimientoGuardado = movimientoRepository.save(movimiento);

        log.info("Movimiento registrado: {} - Material: {} - Cantidad: {}",
                tipo, material.getNombre(), cantidadFinal);

        return movimientoGuardado;
    }

    @Transactional
    public void registrarSalidaParaCorte(
            Long rolloId,
            BigDecimal metrosRequeridos,
            Long pedidoId,
            Long usuarioId) {
        Rollo rollo = rolloRepository.findById(rolloId)
                .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));

        if (!rollo.puedeUsarseParaCorte()) {
            throw new RuntimeException("Este rollo no está destinado para corte");
        }

        registrarMovimiento(
                rollo.getMaterial().getId(),
                rolloId,
                MovimientoInventario.TipoMovimiento.SALIDA_CORTE,
                metrosRequeridos,
                "Salida para corte - Pedido #" + pedidoId,
                pedidoId,
                usuarioId);
    }

    @Transactional
    public void registrarSalidaParaVenta(
            Long rolloId,
            BigDecimal metrosVendidos,
            String motivoVenta,
            Long usuarioId) {
        Rollo rollo = rolloRepository.findById(rolloId)
                .orElseThrow(() -> new RuntimeException("Rollo no encontrado"));

        if (!rollo.puedeUsarseParaVenta()) {
            throw new RuntimeException("Este rollo no está destinado para venta");
        }

        registrarMovimiento(
                rollo.getMaterial().getId(),
                rolloId,
                MovimientoInventario.TipoMovimiento.SALIDA_VENTA,
                metrosVendidos,
                motivoVenta,
                null,
                usuarioId);
    }

    // ============================================
    // CONSULTAS Y REPORTES
    // ============================================

    public List<MovimientoInventario> obtenerUltimosMovimientos(int limite) {
        return movimientoRepository.findTop20ByOrderByFechaDesc();
    }

    public List<MovimientoInventario> obtenerMovimientosPorMaterial(Long materialId) {
        return movimientoRepository.findByMaterialIdOrderByFechaDesc(materialId);
    }

    public List<MovimientoInventario> obtenerMovimientosDelDia() {
        return movimientoRepository.findMovimientosDelDia();
    }

    /**
     * Verificar si hay stock suficiente
     */
    public boolean hayStockSuficiente(Long materialId, BigDecimal cantidadRequerida) {
        Material material = obtenerMaterialPorId(materialId);
        return material.getStockActual().compareTo(cantidadRequerida) >= 0;
    }

    /**
     * Obtener rollo con más metros disponibles (para optimizar corte)
     */
    public Rollo obtenerRolloConMasMetros(Long materialId, Rollo.Destino destino) {
        List<Rollo> rollos = rolloRepository.findByMaterialId(materialId);

        return rollos.stream()
                .filter(r -> r.getActivo() && r.getMetrosActuales().compareTo(BigDecimal.ZERO) > 0)
                .filter(r -> destino == Rollo.Destino.CORTE ? r.puedeUsarseParaCorte() : r.puedeUsarseParaVenta())
                .max((r1, r2) -> r1.getMetrosActuales().compareTo(r2.getMetrosActuales()))
                .orElse(null);
    }
}