-- ============================================
-- HERRERA ERP - DATOS INICIALES
-- Seed Data para MVP Local
-- ============================================

-- ============================================
-- 1. ROLES DEL SISTEMA
-- ============================================

INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador con acceso total al sistema'),
('TALLER', 'Usuario del taller con permisos de producción'),
('LOCAL', 'Vendedor del local con permisos limitados');

-- ============================================
-- 2. PERMISOS POR ROL
-- ============================================

-- Permisos ADMIN (ID: 1)
INSERT INTO permisos (rol_id, modulo, puede_crear, puede_leer, puede_editar, puede_eliminar) VALUES
(1, 'inventario', true, true, true, true),
(1, 'pedidos', true, true, true, true),
(1, 'ventas', true, true, true, true),
(1, 'usuarios', true, true, true, true),
(1, 'productos', true, true, true, true),
(1, 'reportes', true, true, true, true);

-- Permisos TALLER (ID: 2)
INSERT INTO permisos (rol_id, modulo, puede_crear, puede_leer, puede_editar, puede_eliminar) VALUES
(2, 'inventario', false, true, false, false),
(2, 'pedidos', true, true, true, false),
(2, 'ventas', true, true, false, false),
(2, 'usuarios', false, true, false, false),
(2, 'productos', false, true, false, false),
(2, 'reportes', false, true, false, false);

-- Permisos LOCAL (ID: 3)
INSERT INTO permisos (rol_id, modulo, puede_crear, puede_leer, puede_editar, puede_eliminar) VALUES
(3, 'inventario', false, true, false, false),
(3, 'pedidos', true, true, false, false),
(3, 'ventas', true, true, false, false),
(3, 'usuarios', false, false, false, false),
(3, 'productos', false, true, false, false),
(3, 'reportes', false, false, false, false);

-- ============================================
-- 3. USUARIOS INICIALES
-- Password default: "herrera2026" (hash BCrypt)
-- ============================================

INSERT INTO usuarios (username, password_hash, nombre_completo, email, rol_id, activo) VALUES
('admin', '$2a$10$xZ5qY8rHKx3JqN9w5L6Pfu8mQ2vT1nE4cW7sR0aF6gH8jK9lM3pO4', 'Administrador Principal', 'admin@deportesherrera.com', 1, true),
('primo', '$2a$10$xZ5qY8rHKx3JqN9w5L6Pfu8mQ2vT1nE4cW7sR0aF6gH8jK9lM3pO4', 'Jafet (Primo)', 'primo@deportesherrera.com', 1, true),
('tio', '$2a$10$xZ5qY8rHKx3JqN9w5L6Pfu8mQ2vT1nE4cW7sR0aF6gH8jK9lM3pO4', 'Tío', 'tio@deportesherrera.com', 1, true),
('trabajadora', '$2a$10$xZ5qY8rHKx3JqN9w5L6Pfu8mQ2vT1nE4cW7sR0aF6gH8jK9lM3pO4', 'Trabajadora Taller', null, 2, true),
('vendedor_local', '$2a$10$xZ5qY8rHKx3JqN9w5L6Pfu8mQ2vT1nE4cW7sR0aF6gH8jK9lM3pO4', 'Vendedor Local', null, 3, true);

-- ============================================
-- 4. TIPOS DE MATERIAL
-- ============================================

INSERT INTO tipos_material (nombre, descripcion, unidad_medida) VALUES
('TELA', 'Telas deportivas (DryFit, Jersey, etc.)', 'METROS'),
('VINIL', 'Vinil para sublimación y números', 'METROS'),
('HILO', 'Hilos para costura', 'CONOS'),
('CLON', 'Uniformes clon importados', 'PIEZAS'),
('ACCESORIO', 'Accesorios diversos (medias, etc.)', 'PIEZAS');

-- ============================================
-- 5. TIPOS DE CORTE
-- ============================================

INSERT INTO tipos_corte (nombre, descripcion, tallas_disponibles) VALUES
('FUTBOL_SOCCER', 'Camisa básica deportiva de futbol', ARRAY['6','8','10','12','14','16','CH','M','L','XL','XXL','3XL']),
('BASQUETBOL', 'Camisa sin mangas tipo tank top', ARRAY['6','8','10','12','14','16','CH','M','L','XL','XXL','3XL']),
('BASEBALL_ABIERTA', 'Camisa con botones al frente', ARRAY['6','8','10','12','14','16','CH','M','L','XL','XXL','3XL']),
('BASEBALL_CERRADA', 'Camisa cuello redondo sin botones', ARRAY['6','8','10','12','14','16','CH','M','L','XL','XXL','3XL']),
('POLO_ALETILLA', 'Camisa tipo polo con cuello y botones', ARRAY['CH','M','L','XL','XXL','3XL']),
('VOLEIBOL_DAMA', 'Camisa ajustada para dama', ARRAY['8','10','12','14','16','CH','M','L','XL']),
('SHORT_DEPORTIVO', 'Short para basquetbol o futbol', ARRAY['6','8','10','12','14','16','CH','M','L','XL','XXL','3XL']),
('FALDA_DEPORTIVA', 'Falda para voleibol o cheerleader', ARRAY['8','10','12','14','16','CH','M','L']),
('UNIFORME_ESCOLAR', 'Camisa formal de uniforme escolar', ARRAY['6','8','10','12','14','16','CH','M','L','XL']);

-- ============================================
-- 6. PRODUCTOS PRE-CONFIGURADOS
-- ============================================

-- Producto 1: Camisa Futbol Caballero
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Camisa Futbol Caballero', 1, 1.84, true, 0.35, false, 0, null);

-- Ajustes de tallas para Camisa Futbol Caballero
INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(1, '6', -0.10),
(1, '8', -0.08),
(1, '10', -0.05),
(1, '12', -0.03),
(1, '14', -0.01),
(1, '16', 0.00),
(1, 'CH', 0.00),
(1, 'M', 0.05),
(1, 'L', 0.10),
(1, 'XL', 0.15),
(1, 'XXL', 0.20),
(1, '3XL', 0.25);

-- Producto 2: Camisa Futbol Dama
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Camisa Futbol Dama', 1, 1.70, true, 0.30, false, 0, null);

INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(2, '8', -0.08),
(2, '10', -0.05),
(2, '12', -0.03),
(2, '14', 0.00),
(2, 'CH', 0.00),
(2, 'M', 0.03),
(2, 'L', 0.06),
(2, 'XL', 0.10);

-- Producto 3: Short Futbol Caballero
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Short Futbol Caballero', 7, 0.85, false, 0, false, 0, null);

INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(3, '6', -0.10),
(3, '8', -0.08),
(3, '10', -0.05),
(3, '12', -0.03),
(3, 'CH', 0.00),
(3, 'M', 0.05),
(3, 'L', 0.08),
(3, 'XL', 0.12),
(3, 'XXL', 0.16);

-- Producto 4: Camisa Basquetbol Caballero
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Camisa Basquetbol Caballero', 2, 1.60, false, 0, false, 0, null);

INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(4, 'CH', 0.00),
(4, 'M', 0.05),
(4, 'L', 0.10),
(4, 'XL', 0.15),
(4, 'XXL', 0.20);

-- Producto 5: Short Basquetbol Caballero
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Short Basquetbol Caballero', 7, 0.90, false, 0, false, 0, null);

INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(5, 'CH', 0.00),
(5, 'M', 0.05),
(5, 'L', 0.08),
(5, 'XL', 0.12);

-- Producto 6: Camisa Baseball Abierta
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Camisa Baseball Abierta', 3, 1.90, true, 0.40, true, 0.15, 'Tiras para botones');

INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(6, 'CH', 0.00),
(6, 'M', 0.05),
(6, 'L', 0.10),
(6, 'XL', 0.15);

-- Producto 7: Camisa Baseball Cerrada
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Camisa Baseball Cerrada', 4, 1.85, true, 0.35, false, 0, null);

INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(7, 'CH', 0.00),
(7, 'M', 0.05),
(7, 'L', 0.10),
(7, 'XL', 0.15);

-- Producto 8: Polo Deportivo
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Polo Deportivo', 5, 1.75, true, 0.30, true, 0.10, 'Tira para cuello');

INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(8, 'CH', 0.00),
(8, 'M', 0.05),
(8, 'L', 0.10),
(8, 'XL', 0.15);

-- Producto 9: Falda Voleibol
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Falda Voleibol', 8, 1.30, false, 0, true, 0.10, 'Pretina');

INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(9, '8', -0.05),
(9, '10', -0.03),
(9, 'CH', 0.00),
(9, 'M', 0.05),
(9, 'L', 0.10);

-- Producto 10: Uniforme Escolar Caballero
INSERT INTO productos (nombre, tipo_corte_id, consumo_base_metros, incluye_mangas, consumo_mangas_metros, incluye_otro, consumo_otro_metros, descripcion_otro) VALUES
('Uniforme Escolar Caballero', 9, 1.88, true, 0.35, false, 0, null);

INSERT INTO producto_ajustes_talla (producto_id, talla, ajuste_metros) VALUES
(10, '6', -0.10),
(10, '8', -0.08),
(10, '10', -0.05),
(10, '12', -0.03),
(10, 'CH', 0.00),
(10, 'M', 0.05),
(10, 'L', 0.10),
(10, 'XL', 0.15);

-- ============================================
-- 7. MATERIALES DE EJEMPLO
-- ============================================

-- Telas
INSERT INTO materiales (tipo_material_id, nombre, color, stock_actual, stock_minimo, stock_critico, prioridad, precio_unitario) VALUES
(1, 'Tela DryFit', 'Blanco', 92, 96, 48, 'ALTA', 45.00),
(1, 'Tela DryFit', 'Negro', 235, 96, 48, 'MEDIA', 45.00),
(1, 'Tela DryFit', 'Azul Rey', 145, 96, 48, 'MEDIA', 45.00),
(1, 'Tela DryFit', 'Rojo', 188, 96, 48, 'MEDIA', 45.00),
(1, 'Tela DryFit', 'Amarillo', 88, 48, 24, 'BAJA', 45.00);

-- Vinil
INSERT INTO materiales (tipo_material_id, nombre, color, stock_actual, stock_minimo, stock_critico, prioridad, precio_unitario) VALUES
(2, 'Vinil', 'Negro', 15, 20, 5, 'ALTA', 120.00),
(2, 'Vinil', 'Blanco', 28, 20, 5, 'ALTA', 120.00),
(2, 'Vinil', 'Rojo', 12, 15, 5, 'MEDIA', 120.00);

-- Hilos
INSERT INTO materiales (tipo_material_id, nombre, color, stock_actual, stock_minimo, stock_critico, prioridad, precio_unitario) VALUES
(3, 'Hilo', 'Blanco', 2, 5, 2, 'ALTA', 25.00),
(3, 'Hilo', 'Negro', 4, 5, 2, 'ALTA', 25.00);

-- Clones
INSERT INTO materiales (tipo_material_id, nombre, color, talla, stock_actual, stock_minimo, stock_critico, prioridad, precio_unitario) VALUES
(4, 'Clon Barcelona 2024', 'Azulgrana', 'M', 12, 5, 2, 'MEDIA', 350.00),
(4, 'Clon Barcelona 2024', 'Azulgrana', 'L', 8, 5, 2, 'MEDIA', 350.00),
(4, 'Clon Real Madrid 2024', 'Blanco', 'M', 15, 5, 2, 'MEDIA', 350.00),
(4, 'Clon Real Madrid 2024', 'Blanco', 'L', 10, 5, 2, 'MEDIA', 350.00);

-- ============================================
-- 8. ROLLOS DE EJEMPLO
-- ============================================

INSERT INTO rollos (material_id, codigo_rollo, metros_iniciales, metros_actuales, destino, fecha_entrada, proveedor, precio_compra) VALUES
(1, 'R-2026-001-BLA', 96, 92, 'MIXTO', '2026-02-01', 'Importadora México SA', 4300.00),
(2, 'R-2026-002-NEG', 96, 235, 'CORTE', '2026-02-01', 'Importadora México SA', 4300.00),
(3, 'R-2026-003-AZU', 96, 145, 'CORTE', '2026-02-03', 'Importadora México SA', 4300.00),
(4, 'R-2026-004-ROJ', 96, 188, 'CORTE', '2026-02-05', 'Importadora México SA', 4300.00),
(5, 'R-2026-005-AMA', 96, 88, 'MIXTO', '2026-02-01', 'Importadora México SA', 4300.00);

-- ============================================
-- FIN DE SEED DATA
-- ============================================