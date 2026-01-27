import { Moon, Sun } from "lucide-react";
import { useTheme } from "../../hooks/useTheme";
import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

export function ThemeToggle() {
    const { theme, setTheme } = useTheme();
    const [isOpen, setIsOpen] = useState(false);
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

    return (
        <div className="relative" ref={containerRef}>
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="p-2 rounded-lg hover:bg-muted text-muted-foreground transition-colors"
                title="Toggle Theme"
            >
                <Sun className="h-[1.2rem] w-[1.2rem] rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
                <Moon className="absolute top-2 h-[1.2rem] w-[1.2rem] rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
                <span className="sr-only">Toggle theme</span>
            </button>
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, y: 10, scale: 0.95 }}
                        animate={{ opacity: 1, y: 0, scale: 1 }}
                        exit={{ opacity: 0, y: 10, scale: 0.95 }}
                        transition={{ duration: 0.2 }}
                        className="absolute right-0 mt-2 w-32 bg-popover rounded-xl shadow-xl border border-border overflow-hidden z-50 py-1"
                    >
                        <button onClick={() => { setTheme("light"); setIsOpen(false); }} className={`flex w-full items-center px-4 py-2 text-sm ${theme === 'light' ? 'text-primary bg-primary/10' : 'text-foreground hover:bg-muted'}`}>
                            Light
                        </button>
                        <button onClick={() => { setTheme("dark"); setIsOpen(false); }} className={`flex w-full items-center px-4 py-2 text-sm ${theme === 'dark' ? 'text-primary bg-primary/10' : 'text-foreground hover:bg-muted'}`}>
                            Dark
                        </button>
                        <button onClick={() => { setTheme("system"); setIsOpen(false); }} className={`flex w-full items-center px-4 py-2 text-sm ${theme === 'system' ? 'text-primary bg-primary/10' : 'text-foreground hover:bg-muted'}`}>
                            System
                        </button>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
}
