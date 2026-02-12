import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Costos from './pages/Costos';
import Inventario from './pages/Inventario';
import Pedidos from './pages/Pedidos';
import Reportes from './pages/Reportes';

// Componente de ruta protegida
function ProtectedRoute({ children, adminOnly = false }) {
    const { user, loading } = useAuth();

    if (loading) {
        return (
            <div className="flex items-center justify-center h-screen">
                <div className="text-center">
                    <div className="w-16 h-16 border-4 border-red-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                    <p className="text-gray-600">Cargando...</p>
                </div>
            </div>
        );
    }

    if (!user) {
        return <Navigate to="/" replace />;
    }

    if (adminOnly && user.role !== 'ADMIN') {
        return <Navigate to="/dashboard" replace />;
    }

    return children;
}

function AppRoutes() {
    const { user } = useAuth();

    return (
        <Routes>
            {/* Ruta pública de login */}
            <Route
                path="/"
                element={user ? <Navigate to="/dashboard" replace /> : <Login />}
            />

            {/* Rutas protegidas con layout */}
            <Route
                path="/"
                element={
                    <ProtectedRoute>
                        <Layout />
                    </ProtectedRoute>
                }
            >
                <Route path="dashboard" element={<Dashboard />} />
                <Route path="inventario" element={<Inventario />} />
                <Route path="pedidos" element={<Pedidos />} />
                <Route path="reportes" element={<Reportes />} />

                {/* Ruta de Costos - Solo ADMIN */}
                <Route
                    path="costos"
                    element={
                        <ProtectedRoute adminOnly>
                            <Costos />
                        </ProtectedRoute>
                    }
                />
            </Route>

            {/* Ruta 404 */}
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
}

function App() {
    return (
        <AuthProvider>
            <AppRoutes />
        </AuthProvider>
    );
}

export default App;
