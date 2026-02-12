import { useState, useEffect } from 'react';
import { X, Save, Calculator, AlertCircle } from 'lucide-react';
import costoService from '../services/costoService';

const ModalCostos = ({ isOpen, onClose, pedido, onSuccess }) => {
    const [formData, setFormData] = useState({
        pedidoId: '',
        costoTela: 0,
        costoVinil: 0,
        costoHilo: 0,
        costoMaquila: 0,
        costoVarios: 0,
        precioVenta: 0,
        notas: ''
    });

    const [totales, setTotales] = useState({
        totalCosto: 0,
        utilidad: 0,
        margenPorcentaje: 0
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (pedido) {
            setFormData(prev => ({
                ...prev,
                pedidoId: pedido.id,
                precioVenta: pedido.precioTotal || 0
            }));

            // Si el pedido ya tiene costos, cargarlos
            cargarCostosExistentes();
        }
    }, [pedido]);

    useEffect(() => {
        calcularTotales();
    }, [formData.costoTela, formData.costoVinil, formData.costoHilo, formData.costoMaquila, formData.costoVarios, formData.precioVenta]);

    const cargarCostosExistentes = async () => {
        if (!pedido?.id) return;

        try {
            const costos = await costoService.obtenerCostosPedido(pedido.id);
            setFormData({
                pedidoId: pedido.id,
                costoTela: costos.costoTela || 0,
                costoVinil: costos.costoVinil || 0,
                costoHilo: costos.costoHilo || 0,
                costoMaquila: costos.costoMaquila || 0,
                costoVarios: costos.costoVarios || 0,
                precioVenta: costos.precioVenta || 0,
                notas: costos.notas || ''
            });
        } catch (err) {
            // Si no tiene costos, no hacer nada (es nuevo)
            console.log('No hay costos existentes');
        }
    };

    const calcularTotales = () => {
        const totalCosto = parseFloat(formData.costoTela || 0) +
            parseFloat(formData.costoVinil || 0) +
            parseFloat(formData.costoHilo || 0) +
            parseFloat(formData.costoMaquila || 0) +
            parseFloat(formData.costoVarios || 0);

        const precioVenta = parseFloat(formData.precioVenta || 0);
        const utilidad = precioVenta - totalCosto;
        const margenPorcentaje = precioVenta > 0 ? (utilidad / precioVenta) * 100 : 0;

        setTotales({
            totalCosto,
            utilidad,
            margenPorcentaje
        });
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: name === 'notas' ? value : parseFloat(value) || 0
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await costoService.registrarCostos(formData);
            onSuccess?.();
            onClose();
        } catch (err) {
            setError(err.response?.data?.message || 'Error al guardar costos');
        } finally {
            setLoading(false);
        }
    };

    const formatCurrency = (value) => {
        return new Intl.NumberFormat('es-MX', {
            style: 'currency',
            currency: 'MXN'
        }).format(value || 0);
    };

    const getNivelAlerta = () => {
        if (totales.margenPorcentaje >= 30) return { color: 'green', text: 'Excelente' };
        if (totales.margenPorcentaje >= 15) return { color: 'blue', text: 'Normal' };
        if (totales.margenPorcentaje >= 0) return { color: 'yellow', text: 'Bajo' };
        return { color: 'red', text: 'Pérdida' };
    };

    if (!isOpen) return null;

    const nivelAlerta = getNivelAlerta();

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
                {/* Header */}
                <div className="bg-gradient-to-r from-red-600 to-red-700 p-6 rounded-t-2xl">
                    <div className="flex items-center justify-between">
                        <div>
                            <h2 className="text-2xl font-bold text-white flex items-center gap-3">
                                <Calculator className="w-7 h-7" />
                                Registro de Costos
                            </h2>
                            {pedido && (
                                <p className="text-red-100 mt-1">
                                    Pedido: {pedido.folio} - {pedido.nombrePedido}
                                </p>
                            )}
                        </div>
                        <button
                            onClick={onClose}
                            className="text-white hover:bg-red-800 rounded-lg p-2 transition-colors"
                        >
                            <X className="w-6 h-6" />
                        </button>
                    </div>
                </div>

                <form onSubmit={handleSubmit} className="p-6">
                    {error && (
                        <div className="mb-6 bg-red-50 border-l-4 border-red-500 p-4 rounded-lg flex items-start gap-3">
                            <AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
                            <p className="text-red-700">{error}</p>
                        </div>
                    )}

                    {/* Grid de Costos */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                        {/* Costo Tela */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Costo de Tela
                            </label>
                            <input
                                type="number"
                                name="costoTela"
                                value={formData.costoTela}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                                placeholder="0.00"
                            />
                        </div>

                        {/* Costo Vinil */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Costo de Vinil
                            </label>
                            <input
                                type="number"
                                name="costoVinil"
                                value={formData.costoVinil}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                                placeholder="0.00"
                            />
                        </div>

                        {/* Costo Hilo */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Costo de Hilo
                            </label>
                            <input
                                type="number"
                                name="costoHilo"
                                value={formData.costoHilo}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                                placeholder="0.00"
                            />
                        </div>

                        {/* Costo Maquila */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Costo de Maquila
                            </label>
                            <input
                                type="number"
                                name="costoMaquila"
                                value={formData.costoMaquila}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                                placeholder="0.00"
                            />
                        </div>

                        {/* Costos Varios */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Costos Varios
                            </label>
                            <input
                                type="number"
                                name="costoVarios"
                                value={formData.costoVarios}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                                placeholder="0.00"
                            />
                        </div>

                        {/* Precio de Venta */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Precio de Venta *
                            </label>
                            <input
                                type="number"
                                name="precioVenta"
                                value={formData.precioVenta}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                                placeholder="0.00"
                            />
                        </div>
                    </div>

                    {/* Notas */}
                    <div className="mb-6">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Notas
                        </label>
                        <textarea
                            name="notas"
                            value={formData.notas}
                            onChange={handleChange}
                            rows="3"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                            placeholder="Observaciones o comentarios adicionales..."
                        />
                    </div>

                    {/* Calculadora en Tiempo Real */}
                    <div className={`p-6 rounded-xl border-2 mb-6 ${nivelAlerta.color === 'green' ? 'bg-green-50 border-green-500' :
                            nivelAlerta.color === 'blue' ? 'bg-blue-50 border-blue-500' :
                                nivelAlerta.color === 'yellow' ? 'bg-yellow-50 border-yellow-500' :
                                    'bg-red-50 border-red-500'
                        }`}>
                        <h3 className="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
                            <Calculator className="w-5 h-5" />
                            Resumen Calculado
                        </h3>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div>
                                <p className="text-sm text-gray-600 mb-1">Total Costo</p>
                                <p className="text-2xl font-bold text-gray-900">
                                    {formatCurrency(totales.totalCosto)}
                                </p>
                            </div>

                            <div>
                                <p className="text-sm text-gray-600 mb-1">Utilidad</p>
                                <p className={`text-2xl font-bold ${totales.utilidad >= 0 ? 'text-green-600' : 'text-red-600'
                                    }`}>
                                    {formatCurrency(totales.utilidad)}
                                </p>
                            </div>

                            <div>
                                <p className="text-sm text-gray-600 mb-1">Margen</p>
                                <div className="flex items-center gap-2">
                                    <p className={`text-2xl font-bold ${nivelAlerta.color === 'green' ? 'text-green-600' :
                                            nivelAlerta.color === 'blue' ? 'text-blue-600' :
                                                nivelAlerta.color === 'yellow' ? 'text-yellow-600' :
                                                    'text-red-600'
                                        }`}>
                                        {totales.margenPorcentaje.toFixed(2)}%
                                    </p>
                                    <span className={`text-xs px-2 py-1 rounded-full ${nivelAlerta.color === 'green' ? 'bg-green-200 text-green-800' :
                                            nivelAlerta.color === 'blue' ? 'bg-blue-200 text-blue-800' :
                                                nivelAlerta.color === 'yellow' ? 'bg-yellow-200 text-yellow-800' :
                                                    'bg-red-200 text-red-800'
                                        }`}>
                                        {nivelAlerta.text}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Botones */}
                    <div className="flex items-center justify-end gap-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            <Save className="w-5 h-5" />
                            {loading ? 'Guardando...' : 'Guardar Costos'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ModalCostos;
