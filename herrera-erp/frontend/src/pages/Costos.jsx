import { useState, useEffect } from 'react';
import {
    DollarSign,
    TrendingUp,
    TrendingDown,
    AlertTriangle,
    ChevronRight,
    Calendar,
    Download,
    RefreshCw
} from 'lucide-react';
import {
    LineChart,
    Line,
    BarChart,
    Bar,
    PieChart,
    Pie,
    Cell,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts';
import { format, subMonths, startOfMonth, endOfMonth } from 'date-fns';
import { es } from 'date-fns/locale';
import costoService from '../services/costoService';

const Costos = () => {
    // Estados
    const [loading, setLoading] = useState(false);
    const [reporte, setReporte] = useState(null);
    const [periodo, setPeriodo] = useState('mes_actual');
    const [fechaInicio, setFechaInicio] = useState('');
    const [fechaFin, setFechaFin] = useState('');
    const [vistaActual, setVistaActual] = useState('dashboard'); // dashboard | lista | detalle

    // Colores del tema Herrera
    const COLORS = {
        primary: '#DC2626', // Rojo Herrera
        success: '#10B981',
        warning: '#F59E0B',
        danger: '#EF4444',
        dark: '#1F2937',
        light: '#F3F4F6',
        white: '#FFFFFF'
    };

    useEffect(() => {
        calcularFechas(periodo);
    }, [periodo]);

    useEffect(() => {
        if (fechaInicio && fechaFin) {
            cargarReporte();
        }
    }, [fechaInicio, fechaFin]);

    const calcularFechas = (periodo) => {
        const hoy = new Date();
        let inicio, fin;

        switch (periodo) {
            case 'mes_actual':
                inicio = startOfMonth(hoy);
                fin = endOfMonth(hoy);
                break;
            case 'mes_anterior':
                const mesAnterior = subMonths(hoy, 1);
                inicio = startOfMonth(mesAnterior);
                fin = endOfMonth(mesAnterior);
                break;
            case 'trimestre':
                inicio = subMonths(hoy, 3);
                fin = hoy;
                break;
            case 'personalizado':
                return; // Usuario define las fechas
            default:
                inicio = startOfMonth(hoy);
                fin = endOfMonth(hoy);
        }

        setFechaInicio(format(inicio, 'yyyy-MM-dd'));
        setFechaFin(format(fin, 'yyyy-MM-dd'));
    };

    const cargarReporte = async () => {
        setLoading(true);
        try {
            const data = await costoService.generarReporte(fechaInicio, fechaFin);
            setReporte(data);
        } catch (error) {
            console.error('Error al cargar reporte:', error);
        } finally {
            setLoading(false);
        }
    };

    const formatoCurrency = (valor) => {
        return new Intl.NumberFormat('es-MX', {
            style: 'currency',
            currency: 'MXN'
        }).format(valor || 0);
    };

    const formatoPorcentaje = (valor) => {
        return `${(valor || 0).toFixed(2)}%`;
    };

    const getNivelColor = (nivel) => {
        switch (nivel) {
            case 'EXCELENTE': return COLORS.success;
            case 'NORMAL': return COLORS.warning;
            case 'BAJO': return COLORS.danger;
            case 'PERDIDA': return COLORS.dark;
            default: return COLORS.light;
        }
    };

    // Preparar datos para gráficas
    const prepararDatosGrafica = () => {
        if (!reporte) return [];

        return [
            {
                nombre: 'Ventas',
                valor: parseFloat(reporte.totalVentas || 0),
                color: COLORS.primary
            },
            {
                nombre: 'Costos',
                valor: parseFloat(reporte.totalCostos || 0),
                color: COLORS.danger
            },
            {
                nombre: 'Utilidad',
                valor: parseFloat(reporte.utilidadTotal || 0),
                color: COLORS.success
            }
        ];
    };

    const prepararDatosPedidos = () => {
        if (!reporte) return [];

        return [
            {
                nombre: 'Rentables',
                value: reporte.pedidosRentables || 0,
                fill: COLORS.success
            },
            {
                nombre: 'Con Pérdida',
                value: reporte.pedidosConPerdidaCount || 0,
                fill: COLORS.danger
            },
            {
                nombre: 'Sin Costos',
                value: reporte.pedidosSinCostos || 0,
                fill: COLORS.light
            }
        ];
    };

    if (loading && !reporte) {
        return (
            <div className="flex items-center justify-center h-screen bg-white">
                <div className="text-center">
                    <RefreshCw className="w-12 h-12 animate-spin mx-auto text-red-600 mb-4" />
                    <p className="text-gray-600">Cargando datos financieros...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <div className="bg-white border-b border-gray-200 shadow-sm">
                <div className="max-w-7xl mx-auto px-6 py-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-3">
                                <div className="w-12 h-12 bg-gradient-to-br from-red-600 to-red-700 rounded-xl flex items-center justify-center shadow-lg">
                                    <DollarSign className="w-7 h-7 text-white" />
                                </div>
                                Control de Costos y Rentabilidad
                            </h1>
                            <p className="text-gray-600 mt-2">
                                Análisis financiero completo de tus pedidos
                            </p>
                        </div>

                        {/* Selector de Periodo */}
                        <div className="flex items-center gap-4">
                            <select
                                value={periodo}
                                onChange={(e) => setPeriodo(e.target.value)}
                                className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                            >
                                <option value="mes_actual">Mes Actual</option>
                                <option value="mes_anterior">Mes Anterior</option>
                                <option value="trimestre">Últimos 3 Meses</option>
                                <option value="personalizado">Personalizado</option>
                            </select>

                            <button
                                onClick={cargarReporte}
                                disabled={loading}
                                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors flex items-center gap-2 shadow-md"
                            >
                                <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
                                Actualizar
                            </button>
                        </div>
                    </div>

                    {periodo === 'personalizado' && (
                        <div className="mt-4 flex gap-4">
                            <input
                                type="date"
                                value={fechaInicio}
                                onChange={(e) => setFechaInicio(e.target.value)}
                                className="px-4 py-2 border border-gray-300 rounded-lg"
                            />
                            <span className="self-center text-gray-500">hasta</span>
                            <input
                                type="date"
                                value={fechaFin}
                                onChange={(e) => setFechaFin(e.target.value)}
                                className="px-4 py-2 border border-gray-300 rounded-lg"
                            />
                        </div>
                    )}
                </div>
            </div>

            {/* Contenido Principal */}
            <div className="max-w-7xl mx-auto px-6 py-8">
                {reporte ? (
                    <>
                        {/* KPIs Principales */}
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                            {/* Total Ventas */}
                            <div className="bg-white rounded-xl shadow-md p-6 border-l-4 border-red-600">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <p className="text-sm font-medium text-gray-600">Total Ventas</p>
                                        <p className="text-2xl font-bold text-gray-900 mt-2">
                                            {formatoCurrency(reporte.totalVentas)}
                                        </p>
                                    </div>
                                    <div className="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
                                        <TrendingUp className="w-6 h-6 text-red-600" />
                                    </div>
                                </div>
                            </div>

                            {/* Total Costos */}
                            <div className="bg-white rounded-xl shadow-md p-6 border-l-4 border-gray-600">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <p className="text-sm font-medium text-gray-600">Total Costos</p>
                                        <p className="text-2xl font-bold text-gray-900 mt-2">
                                            {formatoCurrency(reporte.totalCostos)}
                                        </p>
                                    </div>
                                    <div className="w-12 h-12 bg-gray-100 rounded-lg flex items-center justify-center">
                                        <TrendingDown className="w-6 h-6 text-gray-600" />
                                    </div>
                                </div>
                            </div>

                            {/* Utilidad Neta */}
                            <div className={`bg-white rounded-xl shadow-md p-6 border-l-4 ${parseFloat(reporte.utilidadTotal) > 0 ? 'border-green-600' : 'border-red-600'
                                }`}>
                                <div className="flex items-center justify-between">
                                    <div>
                                        <p className="text-sm font-medium text-gray-600">Utilidad Neta</p>
                                        <p className={`text-2xl font-bold mt-2 ${parseFloat(reporte.utilidadTotal) > 0 ? 'text-green-600' : 'text-red-600'
                                            }`}>
                                            {formatoCurrency(reporte.utilidadTotal)}
                                        </p>
                                    </div>
                                    <div className={`w-12 h-12 rounded-lg flex items-center justify-center ${parseFloat(reporte.utilidadTotal) > 0 ? 'bg-green-100' : 'bg-red-100'
                                        }`}>
                                        <DollarSign className={`w-6 h-6 ${parseFloat(reporte.utilidadTotal) > 0 ? 'text-green-600' : 'text-red-600'
                                            }`} />
                                    </div>
                                </div>
                            </div>

                            {/* Margen Promedio */}
                            <div className="bg-white rounded-xl shadow-md p-6 border-l-4 border-blue-600">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <p className="text-sm font-medium text-gray-600">Margen Promedio</p>
                                        <p className="text-2xl font-bold text-blue-600 mt-2">
                                            {formatoPorcentaje(reporte.margenPromedio)}
                                        </p>
                                    </div>
                                    <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                                        <TrendingUp className="w-6 h-6 text-blue-600" />
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Gráficas */}
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
                            {/* Gráfica de Barras - Ventas vs Costos vs Utilidad */}
                            <div className="bg-white rounded-xl shadow-md p-6">
                                <h3 className="text-lg font-bold text-gray-900 mb-4">Análisis Financiero</h3>
                                <ResponsiveContainer width="100%" height={300}>
                                    <BarChart data={prepararDatosGrafica()}>
                                        <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                                        <XAxis dataKey="nombre" />
                                        <YAxis />
                                        <Tooltip
                                            formatter={(value) => formatoCurrency(value)}
                                            contentStyle={{
                                                backgroundColor: 'white',
                                                border: '1px solid #e5e7eb',
                                                borderRadius: '8px'
                                            }}
                                        />
                                        <Bar dataKey="valor" fill="#DC2626" radius={[8, 8, 0, 0]} />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>

                            {/* Gráfica de Pie - Distribución de Pedidos */}
                            <div className="bg-white rounded-xl shadow-md p-6">
                                <h3 className="text-lg font-bold text-gray-900 mb-4">Distribución de Pedidos</h3>
                                <ResponsiveContainer width="100%" height={300}>
                                    <PieChart>
                                        <Pie
                                            data={prepararDatosPedidos()}
                                            cx="50%"
                                            cy="50%"
                                            labelLine={false}
                                            label={(entry) => `${entry.nombre}: ${entry.value}`}
                                            outerRadius={100}
                                            fill="#8884d8"
                                            dataKey="value"
                                        >
                                            {prepararDatosPedidos().map((entry, index) => (
                                                <Cell key={`cell-${index}`} fill={entry.fill} />
                                            ))}
                                        </Pie>
                                        <Tooltip />
                                    </PieChart>
                                </ResponsiveContainer>
                            </div>
                        </div>

                        {/* Estadísticas de Pedidos */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                            <div className="bg-white rounded-xl shadow-md p-6">
                                <div className="flex items-center justify-between mb-4">
                                    <h4 className="text-sm font-medium text-gray-600">Pedidos Rentables</h4>
                                    <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                                        <TrendingUp className="w-5 h-5 text-green-600" />
                                    </div>
                                </div>
                                <p className="text-3xl font-bold text-green-600">{reporte.pedidosRentables || 0}</p>
                                <p className="text-sm text-gray-500 mt-2">
                                    {((reporte.pedidosRentables / reporte.totalPedidos) * 100).toFixed(1)}% del total
                                </p>
                            </div>

                            <div className="bg-white rounded-xl shadow-md p-6">
                                <div className="flex items-center justify-between mb-4">
                                    <h4 className="text-sm font-medium text-gray-600">Con Pérdida</h4>
                                    <div className="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
                                        <AlertTriangle className="w-5 h-5 text-red-600" />
                                    </div>
                                </div>
                                <p className="text-3xl font-bold text-red-600">{reporte.pedidosConPerdidaCount || 0}</p>
                                <p className="text-sm text-gray-500 mt-2">Requieren atención</p>
                            </div>

                            <div className="bg-white rounded-xl shadow-md p-6">
                                <div className="flex items-center justify-between mb-4">
                                    <h4 className="text-sm font-medium text-gray-600">Sin Costos</h4>
                                    <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center">
                                        <Calendar className="w-5 h-5 text-gray-600" />
                                    </div>
                                </div>
                                <p className="text-3xl font-bold text-gray-600">{reporte.pedidosSinCostos || 0}</p>
                                <p className="text-sm text-gray-500 mt-2">Pendientes de registro</p>
                            </div>
                        </div>

                        {/* Top Pedidos Rentables */}
                        {reporte.topPedidosRentables && reporte.topPedidosRentables.length > 0 && (
                            <div className="bg-white rounded-xl shadow-md p-6 mb-8">
                                <div className="flex items-center justify-between mb-6">
                                    <h3 className="text-lg font-bold text-gray-900">Top Pedidos Más Rentables</h3>
                                    <span className="text-sm text-gray-500">
                                        {reporte.topPedidosRentables.length} pedidos
                                    </span>
                                </div>
                                <div className="overflow-x-auto">
                                    <table className="w-full">
                                        <thead>
                                            <tr className="border-b border-gray-200">
                                                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-700">Folio</th>
                                                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-700">Cliente</th>
                                                <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Venta</th>
                                                <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Costo</th>
                                                <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Utilidad</th>
                                                <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Margen</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {reporte.topPedidosRentables.map((pedido) => (
                                                <tr key={pedido.id} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                                                    <td className="py-3 px-4 font-medium text-gray-900">{pedido.folioPedido}</td>
                                                    <td className="py-3 px-4 text-gray-700">{pedido.clienteNombre}</td>
                                                    <td className="py-3 px-4 text-right text-gray-900">
                                                        {formatoCurrency(pedido.precioVenta)}
                                                    </td>
                                                    <td className="py-3 px-4 text-right text-gray-600">
                                                        {formatoCurrency(pedido.totalCosto)}
                                                    </td>
                                                    <td className="py-3 px-4 text-right font-semibold text-green-600">
                                                        {formatoCurrency(pedido.utilidad)}
                                                    </td>
                                                    <td className="py-3 px-4 text-right">
                                                        <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                                                            {formatoPorcentaje(pedido.margenPorcentaje)}
                                                        </span>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        )}

                        {/* Pedidos con Pérdida */}
                        {reporte.listaPedidosConPerdida && reporte.listaPedidosConPerdida.length > 0 && (
                            <div className="bg-white rounded-xl shadow-md p-6 border-l-4 border-red-600">
                                <div className="flex items-center gap-3 mb-6">
                                    <AlertTriangle className="w-6 h-6 text-red-600" />
                                    <h3 className="text-lg font-bold text-gray-900">Pedidos con Pérdida</h3>
                                </div>
                                <div className="overflow-x-auto">
                                    <table className="w-full">
                                        <thead>
                                            <tr className="border-b border-gray-200">
                                                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-700">Folio</th>
                                                <th className="text-left py-3 px-4 text-sm font-semibold text-gray-700">Cliente</th>
                                                <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Venta</th>
                                                <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Costo</th>
                                                <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Pérdida</th>
                                                <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Margen</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {reporte.listaPedidosConPerdida.map((pedido) => (
                                                <tr key={pedido.id} className="border-b border-gray-100 hover:bg-red-50 transition-colors">
                                                    <td className="py-3 px-4 font-medium text-gray-900">{pedido.folioPedido}</td>
                                                    <td className="py-3 px-4 text-gray-700">{pedido.clienteNombre}</td>
                                                    <td className="py-3 px-4 text-right text-gray-900">
                                                        {formatoCurrency(pedido.precioVenta)}
                                                    </td>
                                                    <td className="py-3 px-4 text-right text-gray-600">
                                                        {formatoCurrency(pedido.totalCosto)}
                                                    </td>
                                                    <td className="py-3 px-4 text-right font-semibold text-red-600">
                                                        {formatoCurrency(pedido.utilidad)}
                                                    </td>
                                                    <td className="py-3 px-4 text-right">
                                                        <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
                                                            {formatoPorcentaje(pedido.margenPorcentaje)}
                                                        </span>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        )}
                    </>
                ) : (
                    <div className="bg-white rounded-xl shadow-md p-12 text-center">
                        <Calendar className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                        <h3 className="text-xl font-semibold text-gray-700 mb-2">Selecciona un Periodo</h3>
                        <p className="text-gray-500">
                            Elige un rango de fechas para ver el análisis financiero
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Costos;
