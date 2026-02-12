import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
    LayoutDashboard,
    Package,
    ShoppingCart,
    DollarSign,
    FileText,
    Settings,
    LogOut,
    Menu,
    X
} from 'lucide-react';
import { useState } from 'react';

const Layout = () => {
    const { user, logout, isAdmin } = useAuth();
    const navigate = useNavigate();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const menuItems = [
        {
            path: '/dashboard',
            icon: LayoutDashboard,
            label: 'Dashboard',
            adminOnly: false
        },
        {
            path: '/inventario',
            icon: Package,
            label: 'Inventario',
            adminOnly: false
        },
        {
            path: '/pedidos',
            icon: ShoppingCart,
            label: 'Pedidos',
            adminOnly: false
        },
        {
            path: '/costos',
            icon: DollarSign,
            label: 'Costos',
            adminOnly: true
        },
        {
            path: '/reportes',
            icon: FileText,
            label: 'Reportes',
            adminOnly: false
        }
    ];

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Sidebar */}
            < div className={`fixed inset-y-0 left-0 z-50 w-64 bg-gradient-to-b from-gray-900 to-gray-800 transform transition-transform duration-300 ease-in-out ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'
                } lg:translate-x-0`}>
                {/* Logo */}
                <div className="flex items-center justify-between p-6 border-b border-gray-700">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-gradient-to-br from-red-600 to-red-700 rounded-lg flex items-center justify-center shadow-lg">
                            <span className="text-xl text-white font-bold">H</span>
                        </div>
                        <div>
                            <h1 className="text-white font-bold text-lg">Herrera ERP</h1>
                            <p className="text-gray-400 text-xs">Sistema de Gestión</p>
                        </div>
                    </div>
                    <button
                        onClick={() => setSidebarOpen(false)}
                        className="lg:hidden text-gray-400 hover:text-white"
                    >
                        <X className="w-6 h-6" />
                    </button>
                </div>

                {/* Usuario Info */}
                <div className="p-6 border-b border-gray-700">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-red-600 rounded-full flex items-center justify-center">
                            <span className="text-white font-semibold text-sm">
                                {user?.username?.charAt(0).toUpperCase()}
                            </span>
                        </div>
                        <div>
                            <p className="text-white font-medium">{user?.username}</p>
                            <p className="text-gray-400 text-xs">{user?.role}</p>
                        </div>
                    </div>
                </div>

                {/* Navigation */}
                <nav className="p-4 space-y-2">
                    {menuItems.map((item) => {
                        // Ocultar items de solo admin si no es admin
                        if (item.adminOnly && !isAdmin()) return null;

                        return (
                            <NavLink
                                key={item.path}
                                to={item.path}
                                onClick={() => setSidebarOpen(false)}
                                className={({ isActive }) => `
                                    flex items-center gap-3 px-4 py-3 rounded-lg transition-all
                                    ${isActive
                                        ? 'bg-red-600 text-white shadow-lg'
                                        : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                                    }
                                `}
                            >
                                <item.icon className="w-5 h-5" />
                                <span className="font-medium">{item.label}</span>
                                {item.adminOnly && (
                                    <span className="ml-auto text-xs bg-red-500 px-2 py-0.5 rounded">
                                        ADMIN
                                    </span>
                                )}
                            </NavLink>
                        );
                    })}
                </nav>

                {/* Logout Button */}
                <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-700">
                    <button
                        onClick={handleLogout}
                        className="w-full flex items-center gap-3 px-4 py-3 text-gray-300 hover:bg-red-600 hover:text-white rounded-lg transition-all"
                    >
                        <LogOut className="w-5 h-5" />
                        <span className="font-medium">Cerrar Sesión</span>
                    </button>
                </div>
            </div>

            {/* Main Content */}
            <div className="lg:ml-64">
                {/* Top Bar (Mobile) */}
                <div className="lg:hidden bg-white border-b border-gray-200 p-4 flex items-center justify-between">
                    <button
                        onClick={() => setSidebarOpen(true)}
                        className="text-gray-600 hover:text-gray-900"
                    >
                        <Menu className="w-6 h-6" />
                    </button>
                    <h2 className="text-lg font-bold text-gray-900">Herrera ERP</h2>
                    <div className="w-6" /> {/* Spacer */}
                </div>

                {/* Page Content */}
                <main className="min-h-screen">
                    <Outlet />
                </main>
            </div>

            {/* Overlay (Mobile) */}
            {sidebarOpen && (
                <div
                    className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
                    onClick={() => setSidebarOpen(false)}
                />
            )}
        </div>
    );
};

export default Layout;
