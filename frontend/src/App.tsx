import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { useAuth } from './hooks/useAuth';
import { Layout } from './components/Layout';
import { AuthLayout } from './components/layout/AuthLayout';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { VerifyEmail } from './pages/VerifyEmail';
import { Dashboard } from './pages/Dashboard';
import { ScanView } from './components/views/ScanView';
import { HistoryView } from './components/views/HistoryView';
import { FormGeneratorView } from './components/forms/FormGeneratorView';
import { SettingsView } from './components/views/SettingsView';
import { MockSelector } from './components/debug/MockSelector';

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const { user, isLoading } = useAuth();
    if (isLoading) return <div className="flex justify-center items-center h-screen">Loading...</div>;
    if (!user) return <Navigate to="/login" replace />;
    return <>{children}</>;
};

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    {/* Auth Routes */}
                    <Route element={<AuthLayout />}>
                        <Route path="login" element={<Login />} />
                        <Route path="register" element={<Register />} />
                        <Route path="verify" element={<VerifyEmail />} />
                        <Route path="auth/verify" element={<VerifyEmail />} />
                    </Route>

                    {/* Main App Routes */}
                    <Route path="/" element={<Layout />}>
                        <Route index element={<Navigate to="/dashboard" replace />} />
                        <Route path="dashboard" element={
                            <ProtectedRoute>
                                <Dashboard />
                            </ProtectedRoute>
                        } />
                        <Route path="history" element={
                            <ProtectedRoute>
                                <HistoryView />
                            </ProtectedRoute>
                        } />
                        <Route path="scan" element={
                            <ProtectedRoute>
                                <ScanView />
                            </ProtectedRoute>
                        } />
                        <Route path="forms" element={
                            <ProtectedRoute>
                                <FormGeneratorView />
                            </ProtectedRoute>
                        } />
                        <Route path="settings" element={
                            <ProtectedRoute>
                                <SettingsView />
                            </ProtectedRoute>
                        } />
                    </Route>
                </Routes>
            </BrowserRouter>
            {import.meta.env.VITE_USE_MOCK === 'true' && <MockSelector />}
        </AuthProvider >
    );
}

export default App;
