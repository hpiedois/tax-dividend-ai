import { motion } from 'framer-motion';
import { Outlet, NavLink } from 'react-router-dom';
import { useAtom, useSetAtom } from 'jotai';
import { useTranslation } from 'react-i18next';
import { LanguageSwitcher } from './LanguageSwitcher';
import { ThemeToggle } from './ThemeToggle';
import { UserMenu } from './UserMenu';
import { logoutAtom, isAuthenticatedAtom } from '../../store';
import { LogoFinFinal } from '../ui/AppLogos';
import { MobileNav } from './MobileNav';

export function Shell() {
    const { t } = useTranslation();
    const [isAuthenticated] = useAtom(isAuthenticatedAtom);
    const logout = useSetAtom(logoutAtom);

    return (
        <div className="min-h-screen bg-background relative overflow-hidden font-sans text-foreground">
            {/* Abstract Background Shapes */}
            <div className="absolute top-0 left-0 w-full h-[500px] bg-gradient-to-b from-brand-50/50 dark:from-brand-950/20 to-transparent -z-10" />
            <div className="absolute -top-[200px] -right-[200px] w-[600px] h-[600px] bg-brand-200/20 dark:bg-brand-500/5 rounded-full blur-3xl -z-10 animate-pulse" />
            <div className="absolute top-[200px] -left-[100px] w-[300px] h-[300px] bg-indigo-200/20 dark:bg-indigo-500/5 rounded-full blur-3xl -z-10" />

            {/* Header */}
            <header className="fixed top-0 left-0 right-0 h-16 bg-background/80 backdrop-blur-md border-b border-border z-50 px-4 md:px-8 flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <LogoFinFinal className="w-10 h-10 shadow-sm" />
                    <span className="font-heading font-bold text-foreground text-lg tracking-tight">
                        {t('app.title')}
                    </span>
                </div>
                <nav className="hidden md:flex items-center gap-6">
                    {isAuthenticated && (
                        <>
                            <NavLink
                                to="/dashboard"
                                className={({ isActive }) =>
                                    `text-sm font-medium transition-colors ${isActive ? 'text-primary' : 'text-muted-foreground hover:text-primary'
                                    }`
                                }
                            >
                                {t('app.dashboard')}
                            </NavLink>
                            <NavLink
                                to="/history"
                                className={({ isActive }) =>
                                    `text-sm font-medium transition-colors ${isActive ? 'text-primary' : 'text-muted-foreground hover:text-primary'
                                    }`
                                }
                            >
                                {t('app.history')}
                            </NavLink>
                        </>
                    )}
                    <LanguageSwitcher />
                    <div className="pl-2 border-l border-border ml-2">
                        <ThemeToggle />
                    </div>

                    {isAuthenticated && (
                        <div className="flex items-center gap-3 pl-2 border-l border-border ml-2">
                            <UserMenu
                                isLoggedIn={isAuthenticated}
                                onLogout={logout}
                            />
                        </div>
                    )}
                </nav>
            </header>

            {/* Main Content */}
            <main className="pt-24 pb-12 px-4 md:px-8 max-w-7xl mx-auto z-10 relative">
                <motion.div
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.4 }}
                >
                    <Outlet />
                </motion.div>
            </main>

            {/* Mobile Navigation */}
            <MobileNav />
        </div >
    );
}
