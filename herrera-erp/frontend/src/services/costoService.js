import api from './api';

/**
 * Servicio para gestión de costos y rentabilidad
 * Solo accesible para usuarios con rol ADMIN
 */

const costoService = {
    /**
     * Obtener costos de un pedido específico
     */
    obtenerCostosPedido: async (pedidoId) => {
        const response = await api.get(`/costos/pedido/${pedidoId}`);
        return response.data;
    },

    /**
     * Listar todos los costos con paginación
     */
    listarCostos: async (params = {}) => {
        const response = await api.get('/costos', { params });
        return response.data;
    },

    /**
     * Registrar costos de un pedido
     */
    registrarCostos: async (datos) => {
        const response = await api.post('/costos', datos);
        return response.data;
    },

    /**
     * Actualizar costos existentes
     */
    actualizarCostos: async (id, datos) => {
        const response = await api.put(`/costos/${id}`, datos);
        return response.data;
    },

    /**
     * Eliminar costos
     */
    eliminarCostos: async (id) => {
        await api.delete(`/costos/${id}`);
    },

    /**
     * Generar reporte de utilidades por periodo
     */
    generarReporte: async (fechaInicio, fechaFin) => {
        const response = await api.get('/costos/reporte', {
            params: { fechaInicio, fechaFin }
        });
        return response.data;
    },

    /**
     * Obtener top pedidos más rentables
     */
    obtenerTopRentables: async (limite = 10) => {
        const response = await api.get('/costos/top-rentables', {
            params: { limite }
        });
        return response.data;
    },

    /**
     * Obtener pedidos con pérdida
     */
    obtenerPedidosConPerdida: async () => {
        const response = await api.get('/costos/con-perdida');
        return response.data;
    },

    /**
     * Verificar si un pedido tiene costos registrados
     */
    existeCostos: async (pedidoId) => {
        const response = await api.get(`/costos/existe/pedido/${pedidoId}`);
        return response.data;
    }
};

export default costoService;
