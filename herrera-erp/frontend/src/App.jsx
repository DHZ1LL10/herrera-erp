import { Routes, Route, Navigate } from 'react-router-dom'

// ⚠️ NOTA: Este es un MVP inicial
// Páginas aún no implementadas completamente
// El objetivo es mostrar la estructura funcional

function App() {
    return (
        <div className="min-h-screen bg-gray-50">
            <Routes>
                <Route path="/" element={<LoginPlaceholder />} />
                <Route path="/dashboard" element={<DashboardPlaceholder />} />
                <Route path="/inventario" element={<PlaceholderPage title="Inventario" />} />
                <Route path="/pedidos" element={<PlaceholderPage title="Pedidos" />} />
                <Route path="/ventas" element={<PlaceholderPage title="Punto de Venta" />} />
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </div>
    )
}

// Placeholder para Login
function LoginPlaceholder() {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-herrera-red to-herrera-red-dark">
            <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-md">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-display font-bold text-herrera-black mb-2">
                        Herrera ERP
                    </h1>
                    <p className="text-gray-600">Sistema de Gestión Deportiva</p>
                </div>

                <div className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Usuario
                        </label>
                        <input
                            type="text"
                            className="input"
                            placeholder="admin"
                            disabled
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Contraseña
                        </label>
                        <input
                            type="password"
                            className="input"
                            placeholder="••••••••"
                            disabled
                        />
                    </div>

                    <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mt-6">
                        <p className="text-sm text-yellow-800 text-center">
                            ⚠️ <strong>Pantalla de demostración</strong><br />
                            El login funcional se implementará en la siguiente fase.
                        </p>
                    </div>

                    <button
                        className="btn-primary w-full mt-4"
                        disabled
                        title="Login en desarrollo"
                    >
                        Iniciar Sesión (En desarrollo)
                    </button>
                </div>

                <div className="mt-8 text-center text-sm text-gray-500">
                    <p>Deportes Herrera © 2026</p>
                    <p className="mt-1">MVP Local v1.0</p>
                </div>
            </div>
        </div>
    )
}

// Placeholder para Dashboard
function DashboardPlaceholder() {
    return (
        <div className="p-8">
            <h1 className="text-3xl font-display font-bold text-herrera-black mb-6">
                Dashboard
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <StatCard
                    title="Materiales Críticos"
                    value="0"
                    icon="🔴"
                    bgColor="bg-red-50"
                    textColor="text-red-700"
                />
                <StatCard
                    title="Pedidos Activos"
                    value="0"
                    icon="📦"
                    bgColor="bg-blue-50"
                    textColor="text-blue-700"
                />
                <StatCard
                    title="Rollos Disponibles"
                    value="0"
                    icon="🧵"
                    bgColor="bg-green-50"
                    textColor="text-green-700"
                />
                <StatCard
                    title="Ventas Hoy"
                    value="$0"
                    icon="💰"
                    bgColor="bg-yellow-50"
                    textColor="text-yellow-700"
                />
            </div>

            <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
                <h3 className="text-lg font-semibold text-blue-900 mb-2">
                    ℹ️ Backend Completado - Frontend en Desarrollo
                </h3>
                <p className="text-blue-800">
                    El backend está 100% funcional con todas las APIs implementadas.
                    El frontend avanzará en las siguientes fases con Login, Dashboard interactivo,
                    gestión de Inventario y creación de Folios.
                </p>
            </div>
        </div>
    )
}

// Componente de estadística
function StatCard({ title, value, icon, bgColor, textColor }) {
    return (
        <div className={`card ${bgColor}`}>
            <div className="flex items-center justify-between">
                <div>
                    <p className="text-sm font-medium text-gray-600 mb-1">{title}</p>
                    <p className={`text-2xl font-bold ${textColor}`}>{value}</p>
                </div>
                <div className="text-4xl">{icon}</div>
            </div>
        </div>
    )
}

// Placeholder genérico
function PlaceholderPage({ title }) {
    return (
        <div className="p-8">
            <h1 className="text-3xl font-display font-bold text-herrera-black mb-6">
                {title}
            </h1>
            <div className="card">
                <p className="text-gray-600">
                    Esta página se implementará en las siguientes fases del proyecto.
                </p>
            </div>
        </div>
    )
}

export default App
