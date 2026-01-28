import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useNavigate, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { LogoFinFinal } from '../components/ui/AppLogos';
import { User, Lock, ArrowRight, Eye, EyeOff } from 'lucide-react';

export const Login: React.FC = () => {
    const { t } = useTranslation();
    const { login } = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        const formData = new FormData(e.currentTarget);

        try {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            await login(Object.fromEntries(formData) as any);
            navigate('/dashboard', { state: { loginSuccess: true } });
        } catch (err: any) {
            setError(err.response?.status === 401 ? t('auth.error_invalid_credentials', 'Invalid credentials') : t('auth.error_generic', 'Login failed'));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="w-full max-w-[500px] mx-auto">
            {/* Main Card */}
            <div className="bg-white dark:bg-[#0f172a] p-10 rounded-[28px] shadow-[0_20px_60px_-10px_rgba(0,0,0,0.05)] dark:shadow-[0_20px_60px_-10px_rgba(0,0,0,0.3)] border border-slate-100/60 dark:border-slate-800 relative overflow-hidden transition-colors duration-300">

                {/* Header Section */}
                <div className="flex flex-col items-center mb-10">
                    <div className="w-20 h-20 mb-6 relative">
                        {/* Glowing effect behind logo */}
                        <div className="absolute inset-0 bg-brand-500/20 blur-2xl rounded-full transform translate-y-2 opacity-60"></div>
                        <LogoFinFinal className="w-full h-full drop-shadow-lg relative z-10" />
                    </div>
                    <h2 className="text-[28px] font-bold text-slate-900 dark:text-white font-heading tracking-tight mb-2">
                        {t('auth.login_title')}
                    </h2>
                    <p className="text-slate-500 dark:text-slate-400 font-medium">{t('app.title')}</p>
                </div>

                {error && (
                    <div className="bg-red-50 dark:bg-red-950/50 text-red-600 dark:text-red-400 px-4 py-3 rounded-xl mb-6 text-sm font-medium border border-red-100 dark:border-red-900/50 flex items-center justify-center animate-in fade-in slide-in-from-top-2">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div className="space-y-1.5">
                        <div className="relative group">
                            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 dark:text-slate-600 group-focus-within:text-brand-500 transition-colors">
                                <User size={20} strokeWidth={2} />
                            </div>
                            <input
                                name="username"
                                type="text"
                                required
                                className="w-full pl-12 pr-4 py-3.5 bg-slate-50 dark:bg-slate-100 border border-slate-200 dark:border-slate-200 rounded-xl focus:bg-white dark:focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all text-slate-900 placeholder:text-slate-400 dark:placeholder:text-slate-500 font-medium disabled:opacity-50"
                                placeholder={t('auth.email_placeholder')}
                                disabled={loading}
                            />
                        </div>
                    </div>

                    <div className="space-y-1.5">
                        <div className="relative group">
                            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:text-slate-500 dark:hover:text-slate-700 transition-colors">
                                <Lock size={20} strokeWidth={2} />
                            </div>
                            <input
                                name="password"
                                type={showPassword ? 'text' : 'password'}
                                required
                                className="w-full pl-12 pr-12 py-3.5 bg-slate-50 dark:bg-slate-100 border border-slate-200 dark:border-slate-200 rounded-xl focus:bg-white dark:focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all text-slate-900 placeholder:text-slate-400 dark:placeholder:text-slate-500 font-medium disabled:opacity-50"
                                placeholder={t('auth.password_placeholder')}
                                disabled={loading}
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:text-slate-500 dark:hover:text-slate-700 transition-colors outline-none"
                            >
                                {showPassword ? (
                                    <EyeOff size={18} strokeWidth={2} />
                                ) : (
                                    <Eye size={18} strokeWidth={2} />
                                )}
                            </button>
                        </div>
                    </div>

                    <div className="pt-2">
                        <button
                            disabled={loading}
                            className="w-full bg-gradient-to-r from-brand-500 to-brand-600 text-white font-bold py-3.5 rounded-xl hover:shadow-lg hover:shadow-brand-500/30 hover:scale-[1.01] active:scale-[0.98] transition-all disabled:opacity-70 disabled:cursor-not-allowed flex items-center justify-center gap-2 group"
                        >
                            {loading ? (
                                <span className="flex items-center gap-2">
                                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                    <span>{t('auth.logging_in')}</span>
                                </span>
                            ) : (
                                <>
                                    <span>{t('auth.login_button')}</span>
                                    <ArrowRight size={18} strokeWidth={2.5} className="opacity-70 group-hover:translate-x-0.5 transition-transform" />
                                </>
                            )}
                        </button>
                    </div>
                </form>
                <p className="mt-8 text-center text-sm text-slate-500 dark:text-slate-400 font-medium">
                    {t('auth.no_account', "Don't have an account?")} <Link to="/register" className="text-brand-600 dark:text-brand-500 font-bold hover:text-brand-700 dark:hover:text-brand-400 hover:underline transition-all ml-1">{t('auth.register_link', "Create Account")}</Link>
                </p>
            </div>
        </div>
    );
};
