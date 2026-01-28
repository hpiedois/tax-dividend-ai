import React, { createContext, useState, useEffect } from 'react';
import { api } from '../api';

export interface User {
    id: string;
    email: string;
    fullName: string;
}

export interface LoginCredentials {
    email: string;
    password: string;
}

export interface RegisterData {
    email: string;
    password: string;
    fullName: string;
}

export interface AuthContextType {
    user: User | null;
    isLoading: boolean;
    login: (credentials: LoginCredentials) => Promise<void>;
    register: (data: RegisterData) => Promise<void>;
    logout: () => Promise<void>;
    verifyEmail: (token: string) => Promise<void>;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    // Check if user is authenticated on mount (optional, requires /me endpoint)
    useEffect(() => {
        // For now we assume session is not persistent across refresh unless we implement /me
        // Or we rely on the cookie being there and we just need client state.
        // Let's implement a simple /me check if possible, or just default specific state.
        // Since backend doesn't have /me yet, we will start with null.
        setIsLoading(false);
    }, []);

    const login = async (credentials: LoginCredentials) => {
        // Mock login for development
        console.log('Mock login with:', credentials);
        setUser({
            id: 'mock-user-id',
            email: 'john.doe@example.com',
            fullName: 'John Doe'
        });

        // Original API call preserved but commented out for reference
        // const res = await api.post('/auth/login', credentials);
        // setUser(res.data);
    };

    const register = async (data: RegisterData) => {
        // Mock registration for development
        console.log('Mock register with:', data);
        await new Promise(resolve => setTimeout(resolve, 1000)); // Simulate delay
        // Simulate successful registration
        // In a real app we might auto-login or just return
    };

    const verifyEmail = async (token: string) => {
        await api.post(`/auth/verify?token=${token}`);
    };

    const logout = async () => {
        try {
            await api.post('/auth/logout');
        } finally {
            setUser(null);
        }
    };

    return (
        <AuthContext.Provider value={{ user, isLoading, login, register, verifyEmail, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
