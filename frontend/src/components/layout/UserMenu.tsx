import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { useNavigate, useLocation } from 'react-router-dom';
import { User, LogOut, HelpCircle } from 'lucide-react';
import { HelpModal } from './HelpModal';

interface UserMenuProps {
    isLoggedIn: boolean;
    onLogout: () => void;
}

export function UserMenu({ onLogout }: UserMenuProps) {
    const navigate = useNavigate();
    const location = useLocation();
    const { t } = useTranslation();
    const [isOpen, setIsOpen] = useState(false);
    const [showHelp, setShowHelp] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        }
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const menuItems = [
        {
            icon: User,
            label: t('settings.title', 'Profil'),
            action: () => {
                navigate('/settings');
                setIsOpen(false);
            },
            active: location.pathname === '/settings'
        },
        {
            icon: HelpCircle,
            label: t('help.menu_title', 'Aide'),
            action: () => {
                setShowHelp(true);
                setIsOpen(false);
            }
        },
        {
            icon: LogOut,
            label: t('auth.logout', 'Déconnexion'),
            action: () => {
                onLogout();
                setIsOpen(false);
            },
            className: "text-destructive hover:bg-destructive/10 hover:text-destructive"
        }
    ];

    return (
        <>
            <div className="relative" ref={containerRef}>
                <div
                    onClick={() => setIsOpen(!isOpen)}
                    className={`w-8 h-8 rounded-full border-2 shadow-sm flex items-center justify-center font-bold text-xs ring-2 ring-background cursor-pointer transition-colors ${location.pathname === '/settings'
                        ? 'bg-primary text-primary-foreground border-primary'
                        : 'bg-muted border-background text-muted-foreground hover:bg-secondary'
                        }`}
                    title={t('settings.title')}
                >
                    JD
                </div>

                <AnimatePresence>
                    {isOpen && (
                        <motion.div
                            initial={{ opacity: 0, y: 10, scale: 0.95 }}
                            animate={{ opacity: 1, y: 0, scale: 1 }}
                            exit={{ opacity: 0, y: 10, scale: 0.95 }}
                            transition={{ duration: 0.2 }}
                            className="absolute right-0 mt-2 w-56 bg-popover rounded-xl shadow-xl border border-border overflow-hidden z-50 py-1"
                        >
                            <div className="px-4 py-3 border-b border-border">
                                <p className="text-sm font-medium text-foreground">John Doe</p>
                                <p className="text-xs text-muted-foreground truncate">john.doe@example.com</p>
                            </div>

                            <div className="py-1">
                                {menuItems.map((item, index) => (
                                    <button
                                        key={index}
                                        onClick={item.action}
                                        className={`w-full text-left flex items-center px-4 py-2.5 text-sm transition-colors ${item.active
                                            ? 'bg-primary/10 text-primary'
                                            : item.className || 'text-foreground hover:bg-muted'
                                            }`}
                                    >
                                        <item.icon className="w-4 h-4 mr-2.5" />
                                        {item.label}
                                    </button>
                                ))}
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>
            </div>

            <HelpModal isOpen={showHelp} onClose={() => setShowHelp(false)} />
        </>
    );
}
