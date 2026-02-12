-- ============================================
-- HERRERA ERP - SCHEMA BASE DE DATOS
-- Sistema de Gestión para Maquiladora Deportiva
-- Versión: 1.0 MVP Local
-- ============================================

-- ============================================
-- 1. MÓDULO DE USUARIOS Y AUTENTICACIÓN
-- ============================================

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(20),
    rol_id INTEGER REFERENCES roles(id),
    activo BOOLEAN DEFAULT true,
    ultimo_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE permisos (
    id SERIAL PRIMARY KEY,
    rol_id INTEGER REFERENCES roles(id),
    modulo VARCHAR(50) NOT NULL,
    puede_crear BOOLEAN DEFAULT false,
    puede_leer BOOLEAN DEFAULT false,
    puede_editar BOOLEAN DEFAULT false,
    puede_eliminar BOOLEAN DEFAULT false,
    UNIQUE(rol_id, modulo)
);

-- ============================================
-- 2. MÓDULO DE INVENTARIO
-- ============================================

CREATE TABLE tipos_material (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT,
    unidad_medida VARCHAR(20) NOT NULL CHECK (unidad_medida IN ('METROS', 'PIEZAS', 'KILOS', 'CONOS'))
);

CREATE TABLE materiales (
    id SERIAL PRIMARY KEY,
    tipo_material_id INTEGER REFERENCES tipos_material(id),
    nombre VARCHAR(100) NOT NULL,
    color VARCHAR(50),
    talla VARCHAR(10),
    stock_actual DECIMAL(10,2) DEFAULT 0,
    stock_minimo DECIMAL(10,2) DEFAULT 0,
    stock_critico DECIMAL(10,2) DEFAULT 0,
    prioridad VARCHAR(10) CHECK (prioridad IN ('ALTA', 'MEDIA', 'BAJA')),
    precio_unitario DECIMAL(10,2),
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rollos (
    id SERIAL PRIMARY KEY,
    material_id INTEGER REFERENCES materiales(id),
    codigo_rollo VARCHAR(50) UNIQUE NOT NULL,
    metros_iniciales DECIMAL(10,2) NOT NULL,
    metros_actuales DECIMAL(10,2) NOT NULL,
    destino VARCHAR(10) NOT NULL CHECK (destino IN ('CORTE', 'VENTA', 'MIXTO')),
    fecha_entrada DATE NOT NULL,
    proveedor VARCHAR(100),
    precio_compra DECIMAL(10,2),
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movimientos_inventario (
    id SERIAL PRIMARY KEY,
    material_id INTEGER REFERENCES materiales(id),
    rollo_id INTEGER REFERENCES rollos(id),
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA_CORTE', 'SALIDA_VENTA', 'AJUSTE', 'MERMA')),
    cantidad DECIMAL(10,2) NOT NULL,
    stock_anterior DECIMAL(10,2),
    stock_nuevo DECIMAL(10,2),
    motivo TEXT NOT NULL,
    pedido_id INTEGER,
    usuario_id INTEGER REFERENCES usuarios(id),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 3. MÓDULO DE PRODUCTOS Y CONFIGURACIÓN
-- ============================================

CREATE TABLE tipos_corte (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    tallas_disponibles TEXT[],
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo_corte_id INTEGER REFERENCES tipos_corte(id),
    consumo_base_metros DECIMAL(10,2) NOT NULL,
    incluye_mangas BOOLEAN DEFAULT false,
    consumo_mangas_metros DECIMAL(10,2),
    incluye_otro BOOLEAN DEFAULT false,
    consumo_otro_metros DECIMAL(10,2),
    descripcion_otro TEXT,
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE producto_ajustes_talla (
    id SERIAL PRIMARY KEY,
    producto_id INTEGER REFERENCES productos(id) ON DELETE CASCADE,
    talla VARCHAR(10) NOT NULL,
    ajuste_metros DECIMAL(10,2) DEFAULT 0,
    UNIQUE(producto_id, talla)
);

-- ============================================
-- 4. MÓDULO DE PEDIDOS (FOLIOS)
-- ============================================

CREATE TABLE pedidos (
    id SERIAL PRIMARY KEY,
    folio VARCHAR(50) UNIQUE NOT NULL,
    nombre_pedido VARCHAR(200) NOT NULL,
    cliente_nombre VARCHAR(200) NOT NULL,
    cliente_telefono VARCHAR(20),
    cliente_email VARCHAR(100),
    fecha_pedido DATE NOT NULL,
    fecha_entrega DATE NOT NULL,
    prioridad VARCHAR(15) CHECK (prioridad IN ('ESTANDAR', 'PREFERENCIAL')) DEFAULT 'ESTANDAR',
    tipo VARCHAR(10) CHECK (tipo IN ('SENCILLO', 'DOBLE')) DEFAULT 'SENCILLO',
    producto_id INTEGER REFERENCES productos(id),
    color_principal VARCHAR(50),
    color_hex_principal VARCHAR(7),
    total_piezas INTEGER DEFAULT 0,
    total_tela_estimada DECIMAL(10,2),
    observaciones TEXT,
    estado VARCHAR(20) CHECK (estado IN ('PENDIENTE', 'EN_CORTE', 'EN_COSTURA', 'EN_ACABADOS', 'LISTO', 'ENTREGADO', 'CANCELADO')) DEFAULT 'PENDIENTE',
    usuario_creador_id INTEGER REFERENCES usuarios(id),
    ubicacion_origen VARCHAR(10) CHECK (ubicacion_origen IN ('TALLER', 'LOCAL')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pedido_items (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES pedidos(id) ON DELETE CASCADE,
    talla VARCHAR(10) NOT NULL,
    nombre_jugador VARCHAR(100),
    numero_espalda VARCHAR(10),
    color_especial VARCHAR(50),
    color_hex_especial VARCHAR(7),
    tiene_color_especial BOOLEAN DEFAULT false,
    orden_talla INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pedido_imagenes (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES pedidos(id) ON DELETE CASCADE,
    nombre_archivo VARCHAR(200) NOT NULL,
    url_cloudinary TEXT NOT NULL,
    public_id_cloudinary VARCHAR(200),
    tipo VARCHAR(20) CHECK (tipo IN ('DISEÑO_FINAL', 'LOGO', 'REFERENCIA', 'OTRO')),
    descripcion TEXT,
    es_principal BOOLEAN DEFAULT false,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 5. MÓDULO DE PRODUCCIÓN
-- ============================================

CREATE TABLE estados_produccion (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES pedidos(id) ON DELETE CASCADE,
    estado_anterior VARCHAR(20),
    estado_nuevo VARCHAR(20) NOT NULL,
    usuario_id INTEGER REFERENCES usuarios(id),
    notas TEXT,
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cortes_realizados (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES pedidos(id),
    talla VARCHAR(10) NOT NULL,
    cantidad_cortada INTEGER NOT NULL,
    tela_consumida_metros DECIMAL(10,2) NOT NULL,
    rollo_id INTEGER REFERENCES rollos(id),
    usuario_id INTEGER REFERENCES usuarios(id),
    fecha_corte TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE salidas_costura (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES pedidos(id),
    piezas_enviadas INTEGER NOT NULL,
    paquetes_enviados INTEGER,
    fecha_salida DATE NOT NULL,
    responsable_entrega VARCHAR(100),
    fecha_retorno DATE,
    piezas_retornadas INTEGER,
    merma INTEGER,
    notas TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 6. MÓDULO DE PUNTO DE VENTA
-- ============================================

CREATE TABLE ventas (
    id SERIAL PRIMARY KEY,
    folio_venta VARCHAR(50) UNIQUE NOT NULL,
    tipo_venta VARCHAR(20) CHECK (tipo_venta IN ('CLON', 'TELA_METROS', 'VINIL', 'OTRO')),
    cliente_nombre VARCHAR(200),
    cliente_telefono VARCHAR(20),
    total DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(20) CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA')),
    usuario_vendedor_id INTEGER REFERENCES usuarios(id),
    ubicacion VARCHAR(10) CHECK (ubicacion IN ('TALLER', 'LOCAL')),
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE venta_items (
    id SERIAL PRIMARY KEY,
    venta_id INTEGER REFERENCES ventas(id) ON DELETE CASCADE,
    material_id INTEGER REFERENCES materiales(id),
    cantidad DECIMAL(10,2) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

-- ============================================
-- 7. ÍNDICES PARA OPTIMIZACIÓN
-- ============================================

CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_rol ON usuarios(rol_id);
CREATE INDEX idx_materiales_tipo ON materiales(tipo_material_id);
CREATE INDEX idx_materiales_activo ON materiales(activo);
CREATE INDEX idx_rollos_destino ON rollos(destino);
CREATE INDEX idx_rollos_activo ON rollos(activo);
CREATE INDEX idx_movimientos_fecha ON movimientos_inventario(fecha DESC);
CREATE INDEX idx_movimientos_material ON movimientos_inventario(material_id);
CREATE INDEX idx_pedidos_folio ON pedidos(folio);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_pedidos_fecha_entrega ON pedidos(fecha_entrega);
CREATE INDEX idx_pedidos_prioridad ON pedidos(prioridad);
CREATE INDEX idx_pedido_items_pedido ON pedido_items(pedido_id);
CREATE INDEX idx_ventas_fecha ON ventas(fecha_venta DESC);
CREATE INDEX idx_ventas_usuario ON ventas(usuario_vendedor_id);

-- ============================================
-- 8. TRIGGERS PARA ACTUALIZACIÓN AUTOMÁTICA
-- ============================================

-- Trigger para actualizar stock de materiales al registrar movimientos
CREATE OR REPLACE FUNCTION actualizar_stock_material()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE materiales 
    SET stock_actual = stock_actual + NEW.cantidad,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.material_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_actualizar_stock
AFTER INSERT ON movimientos_inventario
FOR EACH ROW
EXECUTE FUNCTION actualizar_stock_material();

-- Trigger para actualizar metros de rollo
CREATE OR REPLACE FUNCTION actualizar_metros_rollo()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.rollo_id IS NOT NULL THEN
        UPDATE rollos
        SET metros_actuales = metros_actuales + NEW.cantidad
        WHERE id = NEW.rollo_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_actualizar_rollo
AFTER INSERT ON movimientos_inventario
FOR EACH ROW
EXECUTE FUNCTION actualizar_metros_rollo();

-- Trigger para contar piezas totales en pedido
CREATE OR REPLACE FUNCTION actualizar_total_piezas_pedido()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE pedidos
    SET total_piezas = (
        SELECT COUNT(*) FROM pedido_items WHERE pedido_id = NEW.pedido_id
    )
    WHERE id = NEW.pedido_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_total_piezas
AFTER INSERT OR DELETE ON pedido_items
FOR EACH ROW
EXECUTE FUNCTION actualizar_total_piezas_pedido();

-- ============================================
-- 9. VISTAS ÚTILES PARA REPORTES
-- ============================================

-- Vista de materiales con alertas de stock
CREATE VIEW v_materiales_con_alertas AS
SELECT 
    m.*,
    tm.nombre as tipo_material_nombre,
    tm.unidad_medida,
    CASE 
        WHEN m.stock_actual <= m.stock_critico THEN 'CRITICO'
        WHEN m.stock_actual <= m.stock_minimo THEN 'BAJO'
        ELSE 'NORMAL'
    END as nivel_alerta
FROM materiales m
JOIN tipos_material tm ON m.tipo_material_id = tm.id
WHERE m.activo = true;

-- Vista de pedidos con información completa
CREATE VIEW v_pedidos_completos AS
SELECT 
    p.*,
    pr.nombre as producto_nombre,
    tc.nombre as tipo_corte_nombre,
    u.nombre_completo as usuario_creador,
    (SELECT COUNT(*) FROM pedido_items WHERE pedido_id = p.id) as total_items,
    (SELECT COUNT(*) FROM pedido_imagenes WHERE pedido_id = p.id) as total_imagenes
FROM pedidos p
LEFT JOIN productos pr ON p.producto_id = pr.id
LEFT JOIN tipos_corte tc ON pr.tipo_corte_id = tc.id
LEFT JOIN usuarios u ON p.usuario_creador_id = u.id;

-- Vista de rollos disponibles
CREATE VIEW v_rollos_disponibles AS
SELECT 
    r.*,
    m.nombre as material_nombre,
    m.color,
    tm.nombre as tipo_material,
    r.metros_actuales as metros_disponibles,
    ROUND((r.metros_actuales / r.metros_iniciales) * 100, 2) as porcentaje_restante
FROM rollos r
JOIN materiales m ON r.material_id = m.id
JOIN tipos_material tm ON m.tipo_material_id = tm.id
WHERE r.activo = true AND r.metros_actuales > 0;

-- ============================================
-- 10. COMENTARIOS EN TABLAS
-- ============================================

COMMENT ON TABLE usuarios IS 'Usuarios del sistema con sus roles y permisos';
COMMENT ON TABLE materiales IS 'Inventario de materiales (telas, vinil, hilos, clones)';
COMMENT ON TABLE rollos IS 'Control de rollos de tela con destino específico';
COMMENT ON TABLE movimientos_inventario IS 'Historial completo de movimientos de inventario';
COMMENT ON TABLE productos IS 'Plantillas configurables de productos con consumo de tela';
COMMENT ON TABLE pedidos IS 'Pedidos principales (folios)';
COMMENT ON TABLE pedido_items IS 'Detalle de cada pedido (tallas, nombres, números)';
COMMENT ON TABLE pedido_imagenes IS 'Imágenes asociadas a pedidos (Cloudinary)';
COMMENT ON TABLE ventas IS 'Ventas directas en punto de venta';

-- ============================================
-- FIN DEL SCHEMA
-- ============================================