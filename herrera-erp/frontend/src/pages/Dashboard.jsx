import { useAuth } from '../context/AuthContext';
import { TrendingUp, Package, ShoppingCart, DollarSign, AlertCircle } from 'lucide-react';

const Dashboard = () => {
    const { user, isAdmin } = useAuth();

    const stats = [
        {
            title: 'Pedidos Activos',
            value: '12',
            icon: ShoppingCart,
            color: 'blue',
            change: '+3 esta semana'
        },
        {
            title: 'Inventario Crítico',
            value: '5',
            icon: AlertCircle,
            color: 'red',
            change: 'Requieren atención'
        },
        {
            title: 'Productos',
            value: '48',
            icon: Package,
            color: 'green',
            change: 'Total en catálogo'
        },
        {
            title: 'Ventas del Mes',
            value: '$45,200',
            icon: DollarSign,
            color: 'yellow',
            change: '+12% vs mes anterior'
        }
    ];

    const getColorClasses = (color) => {
        const colors = {
            blue: 'bg-blue-50 text-blue-600 border-blue-200',
            red: 'bg-red-50 text-red-600 border-red-200',
            green: 'bg-green-50 text-green-600 border-green-200',
            yellow: 'bg-yellow-50 text-yellow-600 border-yellow-200'
        };
        return colors[color] || colors.blue;
    };

    return (
        <div className="min-h-screen bg-gray-50 p-8">
            <div className="max-w-7xl mx-auto">
                {/* Header */}
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-2">
                        Bienvenido, {user?.username}
                    </h1>
                    <p className="text-gray-600">
                        Resumen del sistema Herrera ERP
                    </p>
                </div>

                {/* Stats Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                    {stats.map((stat, index) => (
                        <div
                            key={index}
                            className="bg-white rounded-xl shadow-md p-6 border-l-4 border-red-600 hover:shadow-lg transition-shadow"
                        >
                            <div className="flex items-center justify-between mb-4">
                                <div className={`w-12 h-12 rounded-lg flex items-center justify-center ${getColorClasses(stat.color)}`}>
                                    <stat.icon className="w-6 h-6" />
                                </div>
                            </div>
                            <h3 className="text-sm font-medium text-gray-600 mb-1">{stat.title}</h3>
                            <p className="text-3xl font-bold text-gray-900 mb-2">{stat.value}</p>
                            <p className="text-xs text-gray-500">{stat.change}</p>
                        </div>
                    ))}
                </div>

                {/* Admin Section */}
                {isAdmin() && (
                    <div className="bg-gradient-to-r from-red-600 to-red-700 rounded-xl shadow-lg p-6 text-white mb-8">
                        <div className="flex items-center gap-3 mb-4">
                            <DollarSign className="w-8 h-8" />
                            <div>
                                <h2 className="text-2xl font-bold">Módulo de Costos Disponible</h2>
                                <p className="text-red-100">Acceso exclusivo para administradores</p>
                            </div>
                        </div>
                        <p className="text-red-50 mb-4">
                            Analiza la rentabilidad de tus pedidos, visualiza gráficas financieras y genera reportes detallados.
                        </p>
                        <a
                            href="/costos"
                            className="inline-block bg-white text-red-600 px-6 py-3 rounded-lg font-semibold hover:bg-red-50 transition-colors shadow-md"
                        >
                            Ir al Módulo de Costos →
                        </a>
                    </div>
                )}

                {/* Info Cards */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    <div className="bg-white rounded-xl shadow-md p-6">
                        <h3 className="text-lg font-bold text-gray-900 mb-4">Sistema Activo</h3>
                        <div className="space-y-3">
                            <div className="flex items-center justify-between">
                                <span className="text-gray-600">Backend</span>
                                <span className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium">
                                    ● Conectado
                                </span>
                            </div>
                            <div className="flex items-center justify-between">
                                <span className="text-gray-600">Base de Datos</span>
                                <span className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium">
                                    ● Conectado
                                </span>
                            </div>
                            <div className="flex items-center justify-between">
                                <span className="text-gray-600">API REST</span>
                                <span className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium">
                                    ● Activo
                                </span>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-xl shadow-md p-6">
                        <h3 className="text-lg font-bold text-gray-900 mb-4">Accesos Rápidos</h3>
                        <div className="space-y-3">
                            <a href="/pedidos" className="block p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                                <div className="flex items-center gap-3">
                                    <ShoppingCart className="w-5 h-5 text-gray-600" />
                                    <span className="font-medium text-gray-900">Gestionar Pedidos</span>
                                </div>
                            </a>
                            <a href="/inventario" className="block p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                                <div className="flex items-center gap-3">
                                    <Package className="w-5 h-5 text-gray-600" />
                                    <span className="font-medium text-gray-900">Ver Inventario</span>
                                </div>
                            </a>
                            {isAdmin() && (
                                <a href="/costos" className="block p-3 bg-red-50 rounded-lg hover:bg-red-100 transition-colors border border-red-200">
                                    <div className="flex items-center gap-3">
                                        <DollarSign className="w-5 h-5 text-red-600" />
                                        <span className="font-medium text-red-900">Análisis de Costos</span>
                                        <span className="ml-auto text-xs bg-red-600 text-white px-2 py-0.5 rounded">ADMIN</span>
                                    </div>
                                </a>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
