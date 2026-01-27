import React from 'react';
import { Outlet } from 'react-router-dom';
import { LanguageSwitcher } from './LanguageSwitcher';
import { ThemeToggle } from './ThemeToggle';

export const AuthLayout: React.FC = () => {
    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-950 flex flex-col relative text-slate-900 dark:text-slate-50">
            <div className="absolute top-4 right-4 z-10">
                <div className="flex items-center gap-0.5 bg-white dark:bg-slate-900 border border-slate-200/60 dark:border-slate-800 p-1 rounded-full shadow-sm">
                    <LanguageSwitcher variant="ghost" />
                    <div className="w-px h-4 bg-slate-200 dark:bg-slate-800 mx-1"></div>
                    <ThemeToggle variant="ghost" />
                </div>
            </div>
            <main className="flex-grow flex items-center justify-center p-4">
                <Outlet />
            </main>
        </div>
    );
};
