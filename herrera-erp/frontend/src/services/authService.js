import axios from 'axios';

const API_URL = '/api/auth';

/**
 * Servicio de autenticación
 */
const authService = {
    /**
     * Login de usuario
     */
    async login(username, password) {
        const response = await axios.post(`${API_URL}/login`, {
            username,
            password
        });

        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data));
        }

        return response.data;
    },

    /**
     * Logout
     */
    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    /**
     * Obtener token almacenado
     */
    getToken() {
        return localStorage.getItem('token');
    },

    /**
     * Obtener usuario actual
     */
    getCurrentUser() {
        const userStr = localStorage.getItem('user');
        if (userStr) {
            return JSON.parse(userStr);
        }
        return null;
    },

    /**
     * Verificar si está autenticado
     */
    isAuthenticated() {
        return !!this.getToken();
    },

    /**
     * Validar token con el servidor
     */
    async validateToken(token) {
        const response = await axios.post(`${API_URL}/validate`, { token });
        return response.data.valid;
    }
};

export default authService;
