import { useState } from 'react';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { ChevronLeft } from 'lucide-react';
import { SettingsMenu } from '../settings/SettingsMenu';
import { TaxProfileForm } from '../settings/TaxProfileForm';
import { HelpModal } from '../layout/HelpModal';

export function SettingsView() {
    const { t } = useTranslation();
    const [subPage, setSubPage] = useState<'main' | 'tax-profile'>('main');
    const [showHelp, setShowHelp] = useState(false);

    if (subPage === 'tax-profile') {
        return (
            <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} className="space-y-6 max-w-2xl mx-auto pb-20 md:pb-0">
                <div className="flex items-center gap-2 mb-6">
                    <button onClick={() => setSubPage('main')} className="p-2 hover:bg-muted rounded-full transition-colors">
                        <ChevronLeft className="w-5 h-5 text-foreground" />
                    </button>
                    <h2 className="text-2xl font-heading font-bold text-foreground">Tax Profile</h2>
                </div>
                <TaxProfileForm />
            </motion.div>
        );
    }

    return (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="max-w-md mx-auto pb-20 md:pb-6">
            <h2 className="text-2xl md:text-3xl font-heading font-bold text-foreground mb-6">{t('settings.title')}</h2>

            <SettingsMenu
                onOpenTaxProfile={() => setSubPage('tax-profile')}
                onOpenHelp={() => setShowHelp(true)}
            />

            <HelpModal isOpen={showHelp} onClose={() => setShowHelp(false)} />
        </motion.div>
    );
}
