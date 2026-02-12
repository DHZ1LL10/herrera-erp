package com.herrera.erp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generador de folios únicos para pedidos y ventas
 * Formato: 2026-0001, 2026-0002, etc.
 * Ubicación: backend/src/main/java/com/herrera/erp/util/FolioGenerator.java
 */
@Component
public class FolioGenerator {

    @Value("${folio.prefix:2026}")
    private String prefix;

    @Value("${folio.start-number:1}")
    private int startNumber;

    private final AtomicInteger contador = new AtomicInteger(startNumber);

    /**
     * Genera un folio único para pedidos
     * Formato: 2026-0001
     */
    public String generarFolio() {
        int numero = contador.getAndIncrement();
        return String.format("%s-%04d", prefix, numero);
    }

    /**
     * Genera un folio para ventas
     * Formato: V-2026-0001
     */
    public String generarFolioVenta() {
        int numero = contador.getAndIncrement();
        return String.format("V-%s-%04d", prefix, numero);
    }

    /**
     * Genera código de rollo
     * Formato: R-2026-001-BLA (R-AÑO-NÚMERO-COLOR)
     */
    public String generarCodigoRollo(String color) {
        int numero = contador.getAndIncrement();
        String colorAbrev = color.substring(0, Math.min(3, color.length())).toUpperCase();
        return String.format("R-%s-%03d-%s", prefix, numero, colorAbrev);
    }

    /**
     * Reiniciar contador (útil para inicio de año)
     */
    public void reiniciarContador(int nuevoInicio) {
        contador.set(nuevoInicio);
    }

    /**
     * Obtener siguiente número sin incrementar
     */
    public int obtenerSiguienteNumero() {
        return contador.get();
    }
}