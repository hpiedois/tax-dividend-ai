import { motion, AnimatePresence } from 'framer-motion';
import { X, HelpCircle } from 'lucide-react';
import { useTranslation } from 'react-i18next';
import { Button } from '../ui/Button';
import { createPortal } from 'react-dom';

interface HelpModalProps {
    isOpen: boolean;
    onClose: () => void;
}

export function HelpModal({ isOpen, onClose }: HelpModalProps) {
    const { t } = useTranslation();

    return createPortal(
        <AnimatePresence>
            {isOpen && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
                    {/* Backdrop */}
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={onClose}
                        className="absolute inset-0 bg-black/40 backdrop-blur-sm"
                    />

                    {/* Modal */}
                    <motion.div
                        initial={{ opacity: 0, scale: 0.95, y: 20 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.95, y: 20 }}
                        className="relative w-full max-w-lg bg-card border border-border rounded-xl shadow-2xl flex flex-col max-h-[85vh] overflow-hidden"
                    >
                        {/* Header - Fixed */}
                        <div className="p-6 border-b border-border shrink-0 bg-card z-10">
                            <div className="flex items-center justify-between">
                                <div className="flex items-center gap-3">
                                    <div className="p-2 bg-primary/10 rounded-lg">
                                        <HelpCircle className="w-6 h-6 text-primary" />
                                    </div>
                                    <h2 className="text-xl font-heading font-bold text-foreground">
                                        {t('help.title', 'Centre d\'aide')}
                                    </h2>
                                </div>
                                <button
                                    onClick={onClose}
                                    className="p-2 hover:bg-muted rounded-full transition-colors text-muted-foreground hover:text-foreground"
                                >
                                    <X className="w-5 h-5" />
                                </button>
                            </div>
                        </div>

                        {/* Body - Scrollable */}
                        <div className="p-6 overflow-y-auto custom-scrollbar">
                            <div className="space-y-6">
                                <div className="space-y-3">
                                    <h3 className="font-medium text-foreground flex items-center gap-2">
                                        <span className="w-6 h-6 rounded-full bg-muted flex items-center justify-center text-xs font-bold text-muted-foreground">1</span>
                                        {t('help.how_it_works', 'Comment ça marche ?')}
                                    </h3>
                                    <p className="text-sm text-muted-foreground ml-8">
                                        {t('help.step_1', 'Téléversez vos relevés de dividendes (PDF) via la zone de dépôt.')}
                                    </p>
                                </div>

                                <div className="space-y-3">
                                    <h3 className="font-medium text-foreground flex items-center gap-2">
                                        <span className="w-6 h-6 rounded-full bg-muted flex items-center justify-center text-xs font-bold text-muted-foreground">2</span>
                                        {t('help.analysis', 'Analyse automatique')}
                                    </h3>
                                    <p className="text-sm text-muted-foreground ml-8">
                                        {t('help.step_2', 'L\'IA extrait les données clés : montant brut, impôt retenu, code ISIN, date de paiement.')}
                                    </p>
                                </div>

                                <div className="space-y-3">
                                    <h3 className="font-medium text-foreground flex items-center gap-2">
                                        <span className="w-6 h-6 rounded-full bg-muted flex items-center justify-center text-xs font-bold text-muted-foreground">3</span>
                                        {t('help.reclaim', 'Récupération')}
                                    </h3>
                                    <p className="text-sm text-muted-foreground ml-8">
                                        {t('help.step_3', 'L\'application calcule le montant récupérable et génère les formulaires fiscaux pré-remplis.')}
                                    </p>
                                </div>
                            </div>
                        </div>

                        {/* Footer - Fixed */}
                        <div className="p-6 border-t border-border bg-muted/20 shrink-0 flex justify-end">
                            <Button onClick={onClose}>
                                {t('common.close', 'Fermer')}
                            </Button>
                        </div>
                    </motion.div>
                </div>
            )}
        </AnimatePresence>,
        document.body
    );
}
