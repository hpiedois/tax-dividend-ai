import axios from 'axios';
import { userManager } from '../lib/auth';

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use(async (config) => {
    const user = await userManager.getUser();
    if (user?.access_token) {
        config.headers.Authorization = `Bearer ${user.access_token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// Optional: Add interceptor for 401 to redirect to login
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Redirect to login or clear auth state
            // Use userManager to signout which clears storage and redirects
            userManager.signoutRedirect();
        }
        return Promise.reject(error);
    }
);

// --- Apply Mock Adapter if Enabled ---
export const setupApi = async () => {
    if (import.meta.env.VITE_USE_MOCK === 'true') {
        const { applyMockAdapter } = await import('./mock-adapter');
        applyMockAdapter(api);
        console.log('[API] Mock adapter initialized');
    }
};
