import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { User, HelpCircle, LogOut, Moon, Sun, Monitor, Globe, ChevronRight, ChevronDown, Check } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import { Card } from '../ui/Card';
import { useTheme } from '../../hooks/useTheme';
import { motion, AnimatePresence } from 'framer-motion';
import { Theme } from '../../contexts/ThemeContext';

interface SettingsMenuProps {
    onOpenTaxProfile: () => void;
    onOpenHelp: () => void;
    onLogout?: () => void;
}

export function SettingsMenu({ onOpenTaxProfile, onOpenHelp, onLogout }: SettingsMenuProps) {
    const { i18n } = useTranslation();
    const { logout } = useAuth();
    const navigate = useNavigate();
    const { theme, setTheme } = useTheme();
    
    const [openDropdown, setOpenDropdown] = useState<'language' | 'theme' | null>(null);
    
    // Close dropdowns when clicking outside
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if ((event.target as HTMLElement).closest('button')) return;
            setOpenDropdown(null);
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const handleLogout = async () => {
        if (onLogout) onLogout();
        await logout();
        navigate('/login');
    };

    const changeLanguage = (lng: string) => {
        i18n.changeLanguage(lng);
        setOpenDropdown(null);
    };

    const LANGUAGES = [
        { code: 'en', label: 'English' },
        { code: 'fr', label: 'Français' },
        { code: 'de', label: 'Deutsch' },
        { code: 'it', label: 'Italiano' },
    ];

    const THEMES: Array<{ code: Theme; label: string; icon: typeof Sun }> = [
        { code: 'light', label: 'Light', icon: Sun },
        { code: 'dark', label: 'Dark', icon: Moon },
        { code: 'system', label: 'System', icon: Monitor },
    ];

    const currentTheme = THEMES.find(t => t.code === theme) || THEMES[0];
    const currentLang = LANGUAGES.find(l => i18n.language.startsWith(l.code)) || LANGUAGES[0];

    return (
        <div className="space-y-6 font-sans">
            {/* Appearance Section */}
            <div className="relative z-30">
                <h3 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-3 px-1 font-heading">Appearance</h3>
                <Card className="p-0 overflow-visible bg-card/50 backdrop-blur-sm">
                    <div className="relative">
                        <div className="flex items-center justify-between p-4">
                            <span className="font-medium text-foreground">Theme</span>
                            <button 
                                onClick={() => setOpenDropdown(openDropdown === 'theme' ? null : 'theme')}
                                className="flex items-center gap-2 px-3 py-1.5 rounded-lg hover:bg-muted transition-colors"
                            >
                                <currentTheme.icon className="w-4 h-4 text-muted-foreground" />
                                <span className="text-sm font-medium">{currentTheme.label}</span>
                                <ChevronDown className={`w - 3 h - 3 text - muted - foreground transition - transform ${ openDropdown === 'theme' ? 'rotate-180' : '' } `} />
                            </button>
                        </div>
                        
                        {/* Theme Dropdown */}
                        <AnimatePresence>
                            {openDropdown === 'theme' && (
                                <motion.div 
                                    initial={{ opacity: 0, y: -10 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    exit={{ opacity: 0, y: -10 }}
                                    className="absolute right-2 top-12 w-40 bg-popover rounded-xl shadow-xl border border-border z-20 overflow-hidden"
                                >
                                    {THEMES.map((t) => (
                                        <button
                                            key={t.code}
                                            onClick={() => { setTheme(t.code); setOpenDropdown(null); }}
                                            className="w-full text-left px-4 py-3 text-sm flex items-center justify-between hover:bg-muted transition-colors"
                                        >
                                            <span className="flex items-center gap-2">
                                                <t.icon className="w-4 h-4" />
                                                <span className={theme === t.code ? 'font-medium' : ''}>{t.label}</span>
                                            </span>
                                            {theme === t.code && <Check className="w-3 h-3 text-brand-600" />}
                                        </button>
                                    ))}
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </div>
                </Card>
            </div>

            {/* Language Section */}
            <div className="relative z-20">
                <h3 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-3 px-1 font-heading">Language</h3>
                <Card className="p-0 overflow-visible bg-card/50 backdrop-blur-sm">
                    <div className="relative">
                        <div className="flex items-center justify-between p-4">
                            <span className="font-medium text-foreground">Language</span>
                            <button 
                                onClick={() => setOpenDropdown(openDropdown === 'language' ? null : 'language')}
                                className="flex items-center gap-2 px-3 py-1.5 bg-muted/50 rounded-lg hover:bg-muted transition-colors"
                            >
                                <Globe className="w-4 h-4 text-brand-600" />
                                <span className="text-sm font-medium uppercase">{currentLang.code}</span>
                                <ChevronDown className={`w - 3 h - 3 text - muted - foreground transition - transform ${ openDropdown === 'language' ? 'rotate-180' : '' } `} />
                            </button>
                        </div>

                        {/* Language Dropdown - Scrollable for future scalability */}
                        <AnimatePresence>
                            {openDropdown === 'language' && (
                                <motion.div 
                                    initial={{ opacity: 0, y: -10 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    exit={{ opacity: 0, y: -10 }}
                                    className="absolute right-2 top-12 w-48 bg-popover rounded-xl shadow-xl border border-border z-20 max-h-60 overflow-y-auto"
                                >
                                    {LANGUAGES.map((lang) => (
                                        <button
                                            key={lang.code}
                                            onClick={() => changeLanguage(lang.code)}
                                            className="w-full text-left px-4 py-3 text-sm flex items-center justify-between hover:bg-muted transition-colors"
                                        >
                                            <span className={i18n.language.startsWith(lang.code) ? 'text-brand-600 font-medium' : 'text-foreground'}>
                                                {lang.label}
                                            </span>
                                            {i18n.language.startsWith(lang.code) && <Check className="w-3 h-3 text-brand-600" />}
                                        </button>
                                    ))}
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </div>
                </Card>
            </div>

            {/* Account Section */}
            <div>
                <h3 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-3 px-1 font-heading">Account</h3>
                <Card className="p-0 overflow-hidden divide-y divide-border bg-card/50 backdrop-blur-sm">
                    <button 
                        onClick={onOpenTaxProfile}
                        className="w-full flex items-center justify-between p-4 hover:bg-muted/50 transition-colors text-left"
                    >
                        <div className="flex items-center gap-3">
                            <User className="w-5 h-5 text-foreground" />
                            <span className="font-medium text-foreground">Tax Profile</span>
                        </div>
                        <ChevronRight className="w-4 h-4 text-muted-foreground" />
                    </button>

                    <button 
                        onClick={onOpenHelp}
                        className="w-full flex items-center justify-between p-4 hover:bg-muted/50 transition-colors text-left"
                    >
                        <div className="flex items-center gap-3">
                            <HelpCircle className="w-5 h-5 text-foreground" />
                            <span className="font-medium text-foreground">Help</span>
                        </div>
                        <ChevronRight className="w-4 h-4 text-muted-foreground" />
                    </button>
                </Card>
            </div>

            {/* Logout */}
            <div className="pt-4 flex justify-center">
                <button 
                    onClick={handleLogout}
                    className="flex items-center gap-2 text-red-600 font-medium hover:text-red-700 transition-colors"
                >
                    <LogOut className="w-4 h-4" />
                    <span>Logout</span>
                </button>
            </div>
        </div>
    );
}
