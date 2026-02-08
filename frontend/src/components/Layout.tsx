import React from 'react';
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { LayoutGrid, FileClock, Settings } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { toast } from 'sonner';
import { LanguageSwitcher } from './layout/LanguageSwitcher';
import { ThemeToggle } from './layout/ThemeToggle';
import { UserMenu } from './layout/UserMenu';
import { LogoFinFinal } from './ui/AppLogos';

import { MockSelector } from './debug/MockSelector';

export const Layout: React.FC = () => {
    const { user, logout } = useAuth();
    console.log('Layout Render:', { user });
    const navigate = useNavigate();
    const toastShownRef = React.useRef(false);
    const location = useLocation();

    // STRICT CONDITION: Only show in Mock Mode
    const showDebugMocks = String(import.meta.env.VITE_USE_MOCK) === 'true';

    React.useEffect(() => {
        if (location.state?.loginSuccess && !toastShownRef.current) {
            toastShownRef.current = true;
            // Use standard Sonner success toast which is reliable and beautiful
            toast.success('Successfully logged in', {
                duration: 4000,
            });

            // Clear state so toast doesn't show on refresh
            window.history.replaceState({}, document.title);
        }
    }, [location]);

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    return (
        <div className="min-h-screen bg-background flex flex-col transition-colors duration-300">

            <nav className="bg-card/80 backdrop-blur-md border-b border-border sticky top-0 z-40 transition-colors duration-300">
                <div className="w-full px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex">
                            <Link to="/" className="flex-shrink-0 flex items-center gap-2">
                                <div className="w-8 h-8 rounded-lg overflow-hidden">
                                    <LogoFinFinal className="w-full h-full" />
                                </div>
                                <span className="font-heading font-bold text-lg text-foreground tracking-tight">Tax Dividend AI</span>
                            </Link>
                        </div>
                        <div className="hidden md:flex items-center gap-1">
                            <div className="hidden md:flex items-center gap-6 mr-4">
                                <Link
                                    to="/dashboard"
                                    className={`text-sm font-medium transition-colors ${location.pathname === '/dashboard' || location.pathname === '/'
                                        ? 'text-brand-600'
                                        : 'text-muted-foreground hover:text-foreground'
                                        }`}
                                >
                                    Dashboard
                                </Link>
                                <Link
                                    to="/history"
                                    className={`text-sm font-medium transition-colors ${location.pathname === '/history'
                                        ? 'text-brand-600'
                                        : 'text-muted-foreground hover:text-foreground'
                                        }`}
                                >
                                    History
                                </Link>
                            </div>

                            <LanguageSwitcher variant="ghost" />

                            <div className="h-4 w-px bg-border mx-2"></div>

                            <ThemeToggle variant="ghost" />

                            <div className="h-4 w-px bg-border mx-2"></div>

                            {user ? (
                                <UserMenu user={user} onLogout={handleLogout} />
                            ) : (
                                <Link to="/login" className="text-sm font-medium text-brand-600 hover:text-brand-700">Login</Link>
                            )}
                        </div>
                    </div>
                </div>
            </nav>

            <main className="flex-grow max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-8 mb-16 md:mb-0">
                <Outlet />
            </main>

            <footer className="bg-card border-t border-border py-6 transition-colors duration-300 hidden md:block">
                <div className="max-w-7xl mx-auto px-4 text-center text-muted-foreground text-sm">
                    &copy; {new Date().getFullYear()} Tax Dividend AI. All rights reserved.
                </div>
            </footer>

            {/* Mobile Bottom Navigation */}
            <div className="md:hidden fixed bottom-0 left-0 right-0 bg-card border-t border-border z-50 pb-safe">
                <div className="grid grid-cols-3 h-16">
                    <Link
                        to="/dashboard"
                        className={`flex flex-col items-center justify-center space-y-1 ${location.pathname === '/dashboard' || location.pathname === '/'
                            ? 'text-brand-600'
                            : 'text-muted-foreground'
                            }`}
                    >
                        <LayoutGrid className="w-6 h-6" />
                        <span className="text-xs font-medium">Dashboard</span>
                    </Link>
                    <Link
                        to="/history"
                        className={`flex flex-col items-center justify-center space-y-1 ${location.pathname === '/history'
                            ? 'text-brand-600'
                            : 'text-muted-foreground'
                            }`}
                    >
                        <FileClock className="w-6 h-6" />
                        <span className="text-xs font-medium">History</span>
                    </Link>
                    <Link
                        to="/settings"
                        state={{}}
                        replace={location.pathname === '/settings'}
                        className={`flex flex-col items-center justify-center space-y-1 ${location.pathname === '/settings'
                            ? 'text-brand-600'
                            : 'text-muted-foreground'
                            }`}
                    >
                        <Settings className="w-6 h-6" />
                        <span className="text-xs font-medium">Settings</span>
                    </Link>
                </div>
            </div>

            {/* Debug Tools */}
            {showDebugMocks && <MockSelector />}

        </div>
    );
};