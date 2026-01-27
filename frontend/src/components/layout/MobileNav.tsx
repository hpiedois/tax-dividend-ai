import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { LayoutDashboard, FileClock, Settings, X, LogOut } from 'lucide-react';
import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useAtom, useSetAtom } from 'jotai';
import { logoutAtom, isAuthenticatedAtom } from '../../store';
import { ThemeToggle } from './ThemeToggle';
import { LanguageSwitcher } from './LanguageSwitcher';
import { Button } from '../ui/Button';
import { useNavigate } from 'react-router-dom';
import { User, HelpCircle } from 'lucide-react';
import { HelpModal } from './HelpModal';

export function MobileNav() {
    const { t } = useTranslation();
    const [isAuthenticated] = useAtom(isAuthenticatedAtom);
    const logout = useSetAtom(logoutAtom);
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [showHelp, setShowHelp] = useState(false);
    const navigate = useNavigate();

    if (!isAuthenticated) return null;

    return (
        <>
            {/* Bottom Navigation Bar */}
            <div className="md:hidden fixed bottom-0 left-0 right-0 bg-background/80 backdrop-blur-xl border-t border-border z-40 pb-safe">
                <div className="flex justify-around items-center h-16">
                    <NavLink
                        to="/dashboard"
                        className={({ isActive }) =>
                            `flex flex-col items-center justify-center w-full h-full space-y-1 ${isActive ? 'text-primary' : 'text-muted-foreground hover:text-foreground'
                            }`
                        }
                    >
                        <LayoutDashboard className="w-6 h-6" />
                        <span className="text-[10px] font-medium">{t('app.dashboard')}</span>
                    </NavLink>

                    <NavLink
                        to="/history"
                        className={({ isActive }) =>
                            `flex flex-col items-center justify-center w-full h-full space-y-1 ${isActive ? 'text-primary' : 'text-muted-foreground hover:text-foreground'
                            }`
                        }
                    >
                        <FileClock className="w-6 h-6" />
                        <span className="text-[10px] font-medium">{t('app.history')}</span>
                    </NavLink>

                    <button
                        onClick={() => setIsMenuOpen(true)}
                        className={`flex flex-col items-center justify-center w-full h-full space-y-1 ${isMenuOpen ? 'text-primary' : 'text-muted-foreground hover:text-foreground'
                            }`}
                    >
                        <Settings className="w-6 h-6" />
                        <span className="text-[10px] font-medium">{t('app.settings')}</span>
                    </button>
                </div>
            </div>

            {/* Safe Area Spacer for Content */}
            <div className="md:hidden h-20" />

            {/* Settings Overlay (Drawer) */}
            <AnimatePresence>
                {isMenuOpen && (
                    <>
                        <motion.div
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 md:hidden"
                            onClick={() => setIsMenuOpen(false)}
                        />
                        <motion.div
                            initial={{ y: '100%' }}
                            animate={{ y: 0 }}
                            exit={{ y: '100%' }}
                            transition={{ type: 'spring', damping: 25, stiffness: 200 }}
                            className="fixed bottom-0 left-0 right-0 bg-card border-t border-border rounded-t-3xl p-6 z-50 md:hidden pb-safe-offset-4"
                        >
                            <div className="flex justify-between items-center mb-6">
                                <h3 className="text-lg font-heading font-bold">{t('app.settings')}</h3>
                                <button
                                    onClick={() => setIsMenuOpen(false)}
                                    className="p-2 bg-muted rounded-full text-muted-foreground"
                                >
                                    <X className="w-5 h-5" />
                                </button>
                            </div>

                            <div className="space-y-6">
                                <div className="space-y-2">
                                    <label className="text-sm font-medium text-muted-foreground uppercase tracking-wider">
                                        Appearance
                                    </label>
                                    <div className="flex items-center justify-between bg-muted/30 p-4 rounded-xl">
                                        <span className="font-medium">Theme</span>
                                        <ThemeToggle />
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <label className="text-sm font-medium text-muted-foreground uppercase tracking-wider">
                                        Language
                                    </label>
                                    <div className="flex items-center justify-between bg-muted/30 p-4 rounded-xl">
                                        <span className="font-medium">Language</span>
                                        <LanguageSwitcher />
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <label className="text-sm font-medium text-muted-foreground uppercase tracking-wider">
                                        Account
                                    </label>
                                    <div className="bg-muted/30 rounded-xl overflow-hidden">
                                        <button
                                            onClick={() => {
                                                navigate('/settings', { replace: true, state: {} });
                                                setIsMenuOpen(false);
                                            }}
                                            className="w-full flex items-center px-4 py-3 hover:bg-muted/50 transition-colors"
                                        >
                                            <User className="w-4 h-4 mr-3" />
                                            <span className="font-medium">{t('settings.title')}</span>
                                        </button>
                                        <div className="h-px bg-border/50 mx-4" />
                                        <button
                                            onClick={() => {
                                                setShowHelp(true);
                                                setIsMenuOpen(false);
                                            }}
                                            className="w-full flex items-center px-4 py-3 hover:bg-muted/50 transition-colors"
                                        >
                                            <HelpCircle className="w-4 h-4 mr-3" />
                                            <span className="font-medium">{t('help.menu_title', 'Aide')}</span>
                                        </button>
                                    </div>
                                </div>

                                <div className="pt-4 border-t border-border">
                                    <Button
                                        variant="ghost"
                                        className="w-full flex items-center justify-center gap-2 text-red-600 hover:text-red-700 hover:bg-red-50 dark:hover:bg-red-950/30"
                                        onClick={() => {
                                            logout();
                                            setIsMenuOpen(false);
                                        }}
                                    >
                                        <LogOut className="w-4 h-4" />
                                        {t('auth.logout')}
                                    </Button>
                                </div>
                            </div>
                        </motion.div>
                    </>
                )}
            </AnimatePresence>

            <HelpModal isOpen={showHelp} onClose={() => setShowHelp(false)} />
        </>
    );
}
