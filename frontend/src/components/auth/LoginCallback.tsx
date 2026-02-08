import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export const LoginCallback: React.FC = () => {
    const { user, isAuthenticated, isLoading } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!isLoading && isAuthenticated && user) {
            navigate('/dashboard', { replace: true });
        } else if (!isLoading && !isAuthenticated) {
            // If processing finished but not authenticated, something went wrong
            // Check if we still have query params (maybe processing hasn't started?) 
            // but isLoading should cover it.
            // navigate('/login'); // Optional: fallback
        }
    }, [user, isAuthenticated, isLoading, navigate]);

    return (
        <div className="flex flex-col items-center justify-center h-screen bg-slate-50 dark:bg-slate-900">
            <div className="w-16 h-16 border-4 border-brand-200 border-t-brand-600 rounded-full animate-spin mb-4"></div>
            <p className="text-slate-600 dark:text-slate-400 font-medium">Finalizing login...</p>
        </div>
    );
};
