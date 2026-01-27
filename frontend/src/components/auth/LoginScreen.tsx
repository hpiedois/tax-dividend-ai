import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { useSetAtom, useAtom } from 'jotai';
import { motion } from 'framer-motion';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { Card } from '../ui/Card';
import { LogoFinFinal } from '../ui/AppLogos';
import { loginAtom, isLoadingAuthAtom } from '../../store';
import { showError, showSuccess } from '../../lib/toast-helpers';
import { LanguageSwitcher } from '../layout/LanguageSwitcher';
import { ThemeToggle } from '../layout/ThemeToggle';

export function LoginScreen() {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const login = useSetAtom(loginAtom);
    const [isLoading] = useAtom(isLoadingAuthAtom);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const result = await login({ email, password });

        if (result.success) {
            showSuccess(t('auth.login_success', 'Connexion réussie'));
            navigate('/dashboard');
        } else {
            showError(result.error || t('auth.login_error', 'Erreur de connexion'));
        }
    };

    return (
        <div className="flex items-center justify-center min-h-[calc(100vh-200px)] relative">
            {/* Absolute Controls for Login (Language & Theme) */}
            <div className="absolute top-4 right-4 flex items-center gap-4 bg-card/50 backdrop-blur-sm p-2 rounded-2xl border border-border/50 shadow-sm z-50">
                <LanguageSwitcher />
                <div className="w-px h-4 bg-border" />
                <ThemeToggle />
            </div>

            <motion.div
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.3 }}
                className="w-full max-w-md"
            >
                <Card className="p-8 backdrop-blur-xl bg-card/90">
                    <div className="flex flex-col items-center mb-8 w-full">
                        <LogoFinFinal className="w-20 h-20 shadow-lg shadow-brand-500/20 rounded-2xl mb-4" />
                        <h2 className="text-2xl font-heading font-bold text-foreground">{t('auth.login_title')}</h2>
                        <p className="text-muted-foreground text-center mt-2">
                            Tax Dividend AI
                        </p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <Input
                            type="email"
                            placeholder={t('auth.email_placeholder')}
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                        <Input
                            type="password"
                            placeholder={t('auth.password_placeholder')}
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />

                        <Button
                            type="submit"
                            className="w-full"
                            size="lg"
                            isLoading={isLoading}
                        >
                            {isLoading ? t('auth.logging_in') : t('auth.login_button')}
                        </Button>
                    </form>
                </Card>
            </motion.div>
        </div>
    );
}
