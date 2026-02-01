import React, { createContext, useEffect, useState } from 'react';
import { useAuth as useOidcAuth } from 'react-oidc-context';
import { useSetAtom } from 'jotai';
import { userAtom } from '../store/auth.atoms';

export interface User {
    id: string;
    email: string;
    fullName: string;
    taxId?: string;
}

export interface AuthContextType {
    user: User | null;
    isLoading: boolean;
    isAuthenticated: boolean;
    login: () => Promise<void>;
    register: () => Promise<void>;
    logout: () => Promise<void>;
    token?: string;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

// --- Real OIDC Provider ---
const OIDCAuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const auth = useOidcAuth();
    const setUser = useSetAtom(userAtom);

    const user: User | null = auth.user?.profile ? {
        id: auth.user.profile.sub || '',
        email: auth.user.profile.email || '',
        fullName: auth.user.profile.name || auth.user.profile.preferred_username || 'User',
    } : null;

    // Sync with Jotai Atom
    useEffect(() => {
        if (!auth.isLoading) {
            setUser(user);
        }
    }, [user, auth.isLoading, setUser]);

    const login = async () => {
        await auth.signinRedirect();
    };

    const logout = async () => {
        await auth.signoutRedirect();
    };

    const register = async () => {
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

// --- Mock Provider for Standalone Mode ---
const MockAuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const setUserAtom = useSetAtom(userAtom);
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    // Initial check (simulated session)
    useEffect(() => {
        const stored = localStorage.getItem('mock_user');
        if (stored) {
            try {
                const parsed = JSON.parse(stored);
                setUser(parsed);
                setUserAtom(parsed);
            } catch (e) {
                console.error("Failed to parse mock user", e);
            }
        }
    }, [setUserAtom]);

    const login = async () => {
        setIsLoading(true);
        try {
            // Use dynamic import to avoid potential circular deps if any, 
            // though importing api/index is usually safe.
            const { api } = await import('../api/index');

            // This call will be intercepted by mock-adapter if VITE_USE_MOCK is true
            // effectively reusing the logic in mock-adapter.ts
            // We use a default/test credential payload
            const response = await api.post<User>('/auth/login', {
                username: 'demo@taxdividend.ai',
                password: 'password'
            });

            const user = response.data;
            setUser(user);
            setUserAtom(user);
            localStorage.setItem('mock_user', JSON.stringify(user));
        } catch (error) {
            console.error("Mock login failed", error);
        } finally {
            setIsLoading(false);
        }
    };

    const logout = async () => {
        setIsLoading(true);
        try {
            const { api } = await import('../api/index');
            await api.post('/auth/logout');
        } catch (e) {
            console.error("Mock logout failed", e);
        } finally {
            setUser(null);
            setUserAtom(null);
            localStorage.removeItem('mock_user');
            setIsLoading(false);
        }
    };

    const register = async () => {
        // For mock, register is same as login
        await login();
    };

    return (
        <AuthContext.Provider value={{
            user,
            isLoading,
            isAuthenticated: !!user,
            login,
            register,
            logout,
            token: 'mock-token-123'
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    // Check if we are in Mock Mode
    // We can set this in .env (VITE_USE_MOCK=true)
    const useMock = import.meta.env.VITE_USE_MOCK === 'true';

    console.log(`[AuthProvider] Initializing in ${useMock ? 'MOCK' : 'REAL/OIDC'} mode`);

    if (useMock) {
        return <MockAuthProvider>{children}</MockAuthProvider>;
    }

    return <OIDCAuthProvider>{children}</OIDCAuthProvider>;
};
