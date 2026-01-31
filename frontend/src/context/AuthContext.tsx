import React, { createContext, useEffect, useState } from 'react';
import { useAuth as useOidcAuth } from 'react-oidc-context';

export interface User {
    id: string;
    email: string;
    fullName: string;
}

export interface AuthContextType {
    user: User | null;
    isLoading: boolean;
    isAuthenticated: boolean;
    login: () => Promise<void>;
    register: () => Promise<void>; // Deprecated but kept for type safety temporarily
    logout: () => Promise<void>;
    token?: string;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const auth = useOidcAuth();
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        if (auth.user?.profile) {
            setUser({
                id: auth.user.profile.sub || '',
                email: auth.user.profile.email || '',
                fullName: auth.user.profile.name || auth.user.profile.preferred_username || 'User',
            });
        } else {
            setUser(null);
        }
    }, [auth.user]);

    const login = async () => {
        await auth.signinRedirect();
    };

    const logout = async () => {
        await auth.signoutRedirect();
    };

    const register = async () => {
        // Redirect to Keycloak Registration page if configured, or just login
        await auth.signinRedirect({ extraQueryParams: { kc_action: 'register' } });
    };

    return (
        <AuthContext.Provider value={{
            user,
            isLoading: auth.isLoading,
            isAuthenticated: auth.isAuthenticated,
            login,
            register,
            logout,
            token: auth.user?.access_token
        }}>
            {children}
        </AuthContext.Provider>
    );
};
