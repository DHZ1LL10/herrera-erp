-- ============================================
-- HERRERA ERP - MIGRACIÓN V4
-- Módulo de Control de Costos y Rentabilidad
-- ============================================

-- ============================================
-- TABLA: costos_pedido
-- Almacena los costos detallados de cada pedido
-- para calcular rentabilidad
-- ============================================

CREATE TABLE costos_pedido (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER UNIQUE NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    
    -- Costos individuales
    costo_tela DECIMAL(10,2) DEFAULT 0.00 CHECK (costo_tela >= 0),
    costo_vinil DECIMAL(10,2) DEFAULT 0.00 CHECK (costo_vinil >= 0),
    costo_hilo DECIMAL(10,2) DEFAULT 0.00 CHECK (costo_hilo >= 0),
    costo_maquila DECIMAL(10,2) DEFAULT 0.00 CHECK (costo_maquila >= 0),
    costo_varios DECIMAL(10,2) DEFAULT 0.00 CHECK (costo_varios >= 0),
    
    -- Totales calculados automáticamente
    total_costo DECIMAL(10,2) DEFAULT 0.00,
    
    -- Precio de venta y utilidad
    precio_venta DECIMAL(10,2) DEFAULT 0.00 CHECK (precio_venta >= 0),
    utilidad DECIMAL(10,2) DEFAULT 0.00,
    margen_porcentaje DECIMAL(5,2) DEFAULT 0.00,
    
    -- Notas adicionales
    notas TEXT,
    
    -- Auditoría
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TRIGGER: Calcular totales automáticamente
-- ============================================

CREATE OR REPLACE FUNCTION calcular_totales_costo()
RETURNS TRIGGER AS $$
BEGIN
    -- Calcular total de costos
    NEW.total_costo = COALESCE(NEW.costo_tela, 0) + 
                      COALESCE(NEW.costo_vinil, 0) + 
                      COALESCE(NEW.costo_hilo, 0) + 
                      COALESCE(NEW.costo_maquila, 0) + 
                      COALESCE(NEW.costo_varios, 0);
    
    -- Calcular utilidad
    NEW.utilidad = COALESCE(NEW.precio_venta, 0) - NEW.total_costo;
    
    -- Calcular margen porcentaje (evitar división por cero)
    IF NEW.precio_venta > 0 THEN
        NEW.margen_porcentaje = ROUND(((NEW.utilidad / NEW.precio_venta) * 100)::NUMERIC, 2);
    ELSE
        NEW.margen_porcentaje = 0;
    END IF;
    
    -- Actualizar timestamp de modificación
    NEW.updated_at = CURRENT_TIMESTAMP;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_calcular_totales_costo
BEFORE INSERT OR UPDATE ON costos_pedido
FOR EACH ROW
EXECUTE FUNCTION calcular_totales_costo();

-- ============================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- ============================================

CREATE INDEX idx_costos_pedido_id ON costos_pedido(pedido_id);
CREATE INDEX idx_costos_utilidad ON costos_pedido(utilidad DESC);
CREATE INDEX idx_costos_margen ON costos_pedido(margen_porcentaje DESC);

-- ============================================
-- COMENTARIOS
-- ============================================

COMMENT ON TABLE costos_pedido IS 'Control de costos y rentabilidad por pedido';
COMMENT ON COLUMN costos_pedido.costo_tela IS 'Costo de tela utilizada en el pedido';
COMMENT ON COLUMN costos_pedido.costo_vinil IS 'Costo de vinil utilizado';
COMMENT ON COLUMN costos_pedido.costo_hilo IS 'Costo de hilo utilizado';
COMMENT ON COLUMN costos_pedido.costo_maquila IS 'Costo de mano de obra externa (costura)';
COMMENT ON COLUMN costos_pedido.costo_varios IS 'Otros costos (empaque, transporte, etc.)';
COMMENT ON COLUMN costos_pedido.total_costo IS 'Suma automática de todos los costos';
COMMENT ON COLUMN costos_pedido.precio_venta IS 'Precio total de venta al cliente';
COMMENT ON COLUMN costos_pedido.utilidad IS 'Ganancia neta (precio_venta - total_costo)';
COMMENT ON COLUMN costos_pedido.margen_porcentaje IS 'Porcentaje de ganancia sobre el precio de venta';

-- ============================================
-- FIN DE LA MIGRACIÓN
-- ============================================
