import { useAuth as useOidcAuth } from 'react-oidc-context';

export interface User {
    id: string;
    email: string;
    fullName: string;
    taxId?: string;
}

export const useAuth = () => {
    const auth = useOidcAuth();

    const user: User | null = auth.user?.profile ? {
        id: auth.user.profile.sub || '',
        email: auth.user.profile.email || '',
        fullName: auth.user.profile.name || auth.user.profile.preferred_username || 'User',
    } : null;

    // Helper to unify login/register/logout calls if needed, 
    // or consumers can use auth.signinRedirect() directly.
    // For now, we expose what the detailed AuthContext exposed to keep changes minimal in consumers.

    // Note: detailed AuthContext exposed: login, register, logout, isLoading, isAuthenticated, user, token

    const login = async () => {
        await auth.signinRedirect();
    };

    const register = async () => {
        await auth.signinRedirect({ extraQueryParams: { kc_action: 'register' } });
    };

    const logout = async () => {
        await auth.signoutRedirect();
    };

    return {
        user,
        isLoading: auth.isLoading,
        isAuthenticated: auth.isAuthenticated,
        login,
        register,
        logout,
        token: auth.user?.access_token,
        // Expose original auth object if needed for advanced use cases
        _oidc: auth
    };
};
