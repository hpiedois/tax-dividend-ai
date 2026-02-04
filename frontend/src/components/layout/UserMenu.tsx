import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { User as UserIcon, LogOut, HelpCircle } from 'lucide-react';
import { HelpModal } from './HelpModal';
import type { User } from '../../hooks/useAuth';

interface UserMenuProps {
    user: User | null;
    onLogout: () => void;
}

export function UserMenu({ user, onLogout }: UserMenuProps) {
    const navigate = useNavigate();
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

    const initials = user?.fullName
        ? user.fullName.split(' ').map((n: string) => n[0]).join('').slice(0, 2).toUpperCase()
        : 'JD';

    return (
        <>
            <div className="relative" ref={containerRef}>
                <button
                    onClick={() => setIsOpen(!isOpen)}
                    className="w-9 h-9 rounded-full bg-slate-100 flex items-center justify-center text-slate-700 font-bold text-xs border border-slate-200 cursor-pointer hover:bg-slate-200 transition-colors focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                    title={user?.fullName || 'User'}
                >
                    {initials}
                </button>

                <AnimatePresence>
                    {isOpen && (
                        <motion.div
                            initial={{ opacity: 0, y: 8, scale: 0.96 }}
                            animate={{ opacity: 1, y: 0, scale: 1 }}
                            exit={{ opacity: 0, y: 8, scale: 0.96 }}
                            transition={{ duration: 0.2, ease: "easeOut" }}
                            className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-xl shadow-slate-200/50 border border-slate-100 overflow-hidden z-50 p-1"
                        >
                            <div className="px-3 py-2.5 border-b border-slate-200 mb-1">
                                <p className="font-semibold text-slate-900 text-sm">{user?.fullName || 'User'}</p>
                                <p className="text-xs text-slate-500 truncate">{user?.email}</p>
                            </div>

                            <button
                                onClick={() => {
                                    navigate('/settings', { state: { openTaxProfile: true } });
                                    setIsOpen(false);
                                }}
                                className="w-full text-left px-3 py-2 rounded-lg text-sm transition-colors flex items-center gap-3 text-slate-700 hover:bg-slate-50 font-medium"
                            >
                                <UserIcon className="w-4 h-4 text-slate-500" />
                                <span>Tax Profile</span>
                            </button>

                            <button
                                onClick={() => {
                                    setShowHelp(true);
                                    setIsOpen(false);
                                }}
                                className="w-full text-left px-3 py-2 rounded-lg text-sm transition-colors flex items-center gap-3 text-slate-700 hover:bg-slate-50 font-medium"
                            >
                                <HelpCircle className="w-4 h-4 text-slate-500" />
                                <span>Help</span>
                            </button>

                            <div className="mt-1 pt-1 border-t border-slate-50">
                                <button
                                    onClick={() => {
                                        onLogout();
                                        setIsOpen(false);
                                    }}
                                    className="w-full text-left px-3 py-2 rounded-lg text-sm transition-colors flex items-center gap-3 text-red-600 hover:bg-red-50 font-medium"
                                >
                                    <LogOut className="w-4 h-4" />
                                    <span>Logout</span>
                                </button>
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>
            </div>

            <HelpModal isOpen={showHelp} onClose={() => setShowHelp(false)} />
        </>
    );
}
