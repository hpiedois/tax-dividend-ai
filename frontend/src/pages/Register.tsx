import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { Link } from 'react-router-dom';
import { LogoFinFinal } from '../components/ui/AppLogos';
import { User, Mail, Lock, ArrowRight, CheckCircle2, Eye, EyeOff } from 'lucide-react';

export const Register: React.FC = () => {
    const { register } = useAuth();
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        const formData = new FormData(e.currentTarget);
        const data = Object.fromEntries(formData);

        if (data.password !== data.confirmPassword) {
            setError("Passwords don't match");
            setLoading(false);
            return;
        }

        try {
            await register({
                email: data.email as string,
                password: data.password as string,
                fullName: data.fullName as string
            });
            setSuccess(true);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Registration failed');
        } finally {
            setLoading(false);
        }
    };

    if (success) {
        return (
            <div className="w-full max-w-[420px] mx-auto">
                <div className="bg-white dark:bg-[#0f172a] p-10 rounded-[28px] shadow-[0_20px_60px_-10px_rgba(0,0,0,0.05)] dark:shadow-[0_20px_60px_-10px_rgba(0,0,0,0.3)] border border-slate-100/60 dark:border-slate-800 text-center relative overflow-hidden transition-colors duration-300">
                    <div className="flex justify-center mb-6">
                        <div className="w-20 h-20 bg-green-50 dark:bg-green-900/20 rounded-full flex items-center justify-center relative">
                            <div className="absolute inset-0 bg-green-500/10 blur-xl rounded-full animate-pulse"></div>
                            <CheckCircle2 className="w-10 h-10 text-green-500 relative z-10" strokeWidth={2} />
                        </div>
                    </div>
                    <h2 className="text-2xl font-bold mb-3 text-slate-900 dark:text-white font-heading">Registration Successful!</h2>
                    <p className="text-slate-500 dark:text-slate-400 mb-8 leading-relaxed">
                        Please check your email to verify your account and start reclaiming your dividends.
                    </p>
                    <Link
                        to="/login"
                        className="w-full bg-slate-900 dark:bg-white text-white dark:text-slate-900 font-bold py-3.5 rounded-xl hover:bg-slate-800 dark:hover:bg-slate-100 hover:shadow-lg hover:shadow-slate-900/20 dark:hover:shadow-white/10 hover:scale-[1.01] active:scale-[0.98] transition-all flex items-center justify-center gap-2 group"
                    >
                        <span>Go to Login</span>
                        <ArrowRight size={18} strokeWidth={2.5} className="group-hover:translate-x-0.5 transition-transform" />
                    </Link>
                </div>
            </div>
        );
    }

    return (
        <div className="w-full max-w-[500px] mx-auto">
            {/* Main Card */}
            <div className="bg-white dark:bg-[#0f172a] p-10 rounded-[28px] shadow-[0_20px_60px_-10px_rgba(0,0,0,0.05)] dark:shadow-[0_20px_60px_-10px_rgba(0,0,0,0.3)] border border-slate-100/60 dark:border-slate-800 relative overflow-hidden transition-colors duration-300">

                {/* Header */}
                <div className="flex flex-col items-center mb-8">
                    <div className="w-20 h-20 mb-6 relative">
                        {/* Glowing effect */}
                        <div className="absolute inset-0 bg-brand-500/20 blur-2xl rounded-full transform translate-y-2 opacity-60"></div>
                        <LogoFinFinal className="w-full h-full drop-shadow-lg relative z-10" />
                    </div>
                    <h2 className="text-[28px] font-bold text-slate-900 dark:text-white font-heading tracking-tight mb-2">Create Account</h2>
                    <p className="text-slate-500 dark:text-slate-400 font-medium">Join Tax Dividend AI</p>
                </div>

                {error && (
                    <div className="bg-red-50 dark:bg-red-950/50 text-red-600 dark:text-red-400 px-4 py-3 rounded-xl mb-6 text-sm font-medium border border-red-100 dark:border-red-900/50 flex items-center justify-center animate-in fade-in slide-in-from-top-2">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="space-y-1.5">
                        <div className="relative group">
                            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 dark:text-slate-600 group-focus-within:text-brand-500 transition-colors">
                                <User size={20} strokeWidth={2} />
                            </div>
                            <input
                                name="fullName"
                                type="text"
                                required
                                className="w-full pl-12 pr-4 py-3.5 bg-slate-50 dark:bg-slate-100 border border-slate-200 dark:border-slate-200 rounded-xl focus:bg-white dark:focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all text-slate-900 placeholder:text-slate-400 dark:placeholder:text-slate-500 font-medium disabled:opacity-50"
                                placeholder="Full Name"
                                disabled={loading}
                            />
                        </div>
                    </div>

                    <div className="space-y-1.5">
                        <div className="relative group">
                            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 dark:text-slate-600 group-focus-within:text-brand-500 transition-colors">
                                <Mail size={20} strokeWidth={2} />
                            </div>
                            <input
                                name="email"
                                type="email"
                                required
                                className="w-full pl-12 pr-4 py-3.5 bg-slate-50 dark:bg-slate-100 border border-slate-200 dark:border-slate-200 rounded-xl focus:bg-white dark:focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all text-slate-900 placeholder:text-slate-400 dark:placeholder:text-slate-500 font-medium disabled:opacity-50"
                                placeholder="Email Address"
                                disabled={loading}
                            />
                        </div>
                    </div>

                    <div className="space-y-4">
                        <div className="space-y-1.5">
                            <div className="relative group">
                                <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 dark:text-slate-600 group-focus-within:text-brand-500 transition-colors">
                                    <Lock size={18} strokeWidth={2} />
                                </div>
                                <input
                                    name="password"
                                    type={showPassword ? 'text' : 'password'}
                                    required
                                    className="w-full pl-10 pr-12 py-3.5 bg-slate-50 dark:bg-slate-100 border border-slate-200 dark:border-slate-200 rounded-xl focus:bg-white dark:focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all text-slate-900 placeholder:text-slate-400 dark:placeholder:text-slate-500 font-medium disabled:opacity-50 text-sm"
                                    placeholder="Password"
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

                        <div className="space-y-1.5">
                            <div className="relative group">
                                <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 dark:text-slate-600 group-focus-within:text-brand-500 transition-colors">
                                    <Lock size={18} strokeWidth={2} />
                                </div>
                                <input
                                    name="confirmPassword"
                                    type={showConfirmPassword ? 'text' : 'password'}
                                    required
                                    className="w-full pl-10 pr-12 py-3.5 bg-slate-50 dark:bg-slate-100 border border-slate-200 dark:border-slate-200 rounded-xl focus:bg-white dark:focus:bg-white focus:ring-4 focus:ring-brand-500/10 focus:border-brand-500 outline-none transition-all text-slate-900 placeholder:text-slate-400 dark:placeholder:text-slate-500 font-medium disabled:opacity-50 text-sm"
                                    placeholder="Confirm Password"
                                    disabled={loading}
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:text-slate-500 dark:hover:text-slate-700 transition-colors outline-none"
                                >
                                    {showConfirmPassword ? (
                                        <EyeOff size={18} strokeWidth={2} />
                                    ) : (
                                        <Eye size={18} strokeWidth={2} />
                                    )}
                                </button>
                            </div>
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
                                    <span>Creating...</span>
                                </span>
                            ) : (
                                <>
                                    <span>Create Account</span>
                                    <ArrowRight size={18} strokeWidth={2.5} className="opacity-70 group-hover:translate-x-0.5 transition-transform" />
                                </>
                            )}
                        </button>
                    </div>
                </form>

                <p className="mt-8 text-center text-sm text-slate-500 dark:text-slate-400 font-medium">
                    Already have an account? <Link to="/login" className="text-brand-600 dark:text-brand-500 font-bold hover:text-brand-700 dark:hover:text-brand-400 hover:underline transition-all ml-1">Log In</Link>
                </p>
            </div>
        </div>
    );
};
