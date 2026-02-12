package com.herrera.erp.service;

import com.herrera.erp.exception.ResourceNotFoundException;
import com.herrera.erp.exception.StockInsuficienteException;
import com.herrera.erp.model.*;
import com.herrera.erp.repository.MaterialRepository;
import com.herrera.erp.repository.RolloRepository;
import com.herrera.erp.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de Ventas
 * Punto de venta y reportes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VentaService {

    private final VentaRepository ventaRepository;
    private final MaterialRepository materialRepository;
    private final RolloRepository rolloRepository;
    private final InventarioService inventarioService;

    /**
     * Obtener todas las ventas
     */
    public List<Venta> obtenerTodasVentas() {
        return ventaRepository.findAll();
    }

    /**
     * Obtener ventas del día
     */
    public List<Venta> obtenerVentasDelDia() {
        return ventaRepository.findVentasDelDia();
    }

    /**
     * Obtener ventas por rango de fechas
     */
    public List<Venta> obtenerVentasPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return ventaRepository.findVentasPorRango(
                fechaInicio.atStartOfDay(),
                fechaFin.atTime(23, 59, 59));
    }

    /**
     * Obtener ventas por ubicación
     */
    public List<Venta> obtenerVentasPorUbicacion(String ubicacion) {
        Venta.UbicacionVenta ubicacionEnum = Venta.UbicacionVenta.valueOf(ubicacion.toUpperCase());
        return ventaRepository.findByUbicacion(ubicacionEnum);
    }

    /**
     * Registrar venta de tela por metros
     */
    @Transactional
    public Venta registrarVentaTela(Long rolloId, BigDecimal metrosVendidos,
            String clienteNombre, String clienteTelefono,
            BigDecimal precioUnitario, String metodoPago,
            String ubicacion, Long usuarioVendedorId) {
        log.info("Registrando venta de tela: {} metros del rollo ID: {}", metrosVendidos, rolloId);

        // Verificar rollo
        Rollo rollo = rolloRepository.findById(rolloId)
                .orElseThrow(() -> new ResourceNotFoundException("Rollo", "id", rolloId));

        // Verificar que el rollo pueda usarse para venta
        if (!rollo.puedeUsarseParaVenta()) {
            throw new IllegalArgumentException("Este rollo no está destinado para venta");
        }

        // Verificar stock suficiente
        if (rollo.getMetrosActuales().compareTo(metrosVendidos) < 0) {
            throw new StockInsuficienteException(
                    rollo.getMaterial().getId(),
                    rollo.getMetrosActuales(),
                    metrosVendidos);
        }

        // Generar folio de venta
        String folio = generarFolioVenta();

        // Calcular total
        BigDecimal total = precioUnitario.multiply(metrosVendidos);

        // Crear venta
        Venta venta = Venta.builder()
                .folioVenta(folio)
                .tipoVenta(Venta.TipoVenta.TELA_METROS)
                .clienteNombre(clienteNombre)
                .clienteTelefono(clienteTelefono)
                .total(total)
                .metodoPago(Venta.MetodoPago.valueOf(metodoPago.toUpperCase()))
                .usuarioVendedor(new Usuario())
                .ubicacion(Venta.UbicacionVenta.valueOf(ubicacion.toUpperCase()))
                .build();

        venta.getUsuarioVendedor().setId(usuarioVendedorId);

        // Guardar venta
        Venta ventaGuardada = ventaRepository.save(venta);

        // Registrar salida de inventario
        inventarioService.registrarSalidaParaVenta(
                rolloId,
                metrosVendidos,
                "Venta - Folio: " + folio,
                usuarioVendedorId);

        log.info("Venta registrada exitosamente: Folio {}", folio);

        return ventaGuardada;
    }

    /**
     * Registrar venta de clone
     */
    @Transactional
    public Venta registrarVentaClone(Long materialId, Integer cantidad,
            String clienteNombre, String clienteTelefono,
            BigDecimal precioUnitario, String metodoPago,
            String ubicacion, Long usuarioVendedorId) {
        log.info("Registrando venta de clones: {} unidades del material ID: {}", cantidad, materialId);

        // Verificar material
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));

        // Verificar stock suficiente
        BigDecimal cantidadBD = new BigDecimal(cantidad);
        if (!inventarioService.hayStockSuficiente(materialId, cantidadBD)) {
            throw new StockInsuficienteException(
                    materialId,
                    material.getStockActual(),
                    cantidadBD);
        }

        // Generar folio de venta
        String folio = generarFolioVenta();

        // Calcular total
        BigDecimal total = precioUnitario.multiply(cantidadBD);

        // Crear venta
        Venta venta = Venta.builder()
                .folioVenta(folio)
                .tipoVenta(Venta.TipoVenta.CLON)
                .clienteNombre(clienteNombre)
                .clienteTelefono(clienteTelefono)
                .total(total)
                .metodoPago(Venta.MetodoPago.valueOf(metodoPago.toUpperCase()))
                .usuarioVendedor(new Usuario())
                .ubicacion(Venta.UbicacionVenta.valueOf(ubicacion.toUpperCase()))
                .build();

        venta.getUsuarioVendedor().setId(usuarioVendedorId);

        // Guardar venta
        Venta ventaGuardada = ventaRepository.save(venta);

        // Registrar salida de inventario
        inventarioService.registrarMovimiento(
                materialId,
                null, // No hay rollo específico para clones
                MovimientoInventario.TipoMovimiento.SALIDA_VENTA,
                cantidadBD,
                "Venta de clones - Folio: " + folio,
                null,
                usuarioVendedorId);

        log.info("Venta de clones registrada exitosamente: Folio {}", folio);

        return ventaGuardada;
    }

    /**
     * Calcular total de ventas del día
     */
    public BigDecimal calcularTotalVentasDelDia() {
        List<Venta> ventasHoy = obtenerVentasDelDia();

        return ventasHoy.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcular total de ventas por periodo
     */
    public BigDecimal calcularTotalVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Venta> ventas = obtenerVentasPorRango(fechaInicio, fechaFin);

        return ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Generar folio de venta único
     */
    private String generarFolioVenta() {
        int año = LocalDate.now().getYear();
        long numeroVentas = ventaRepository.count() + 1;

        return String.format("VTA-%d-%04d", año, numeroVentas);
    }
}
