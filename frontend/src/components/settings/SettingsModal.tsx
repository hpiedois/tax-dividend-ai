import { motion, AnimatePresence } from 'framer-motion';
import { X, ChevronLeft } from 'lucide-react';
import { useState } from 'react';
import { SettingsMenu } from './SettingsMenu';
import { TaxProfileForm } from './TaxProfileForm';
import { HelpModal } from '../layout/HelpModal';

interface SettingsModalProps {
    isOpen: boolean;
    onClose: () => void;
}

export function SettingsModal({ isOpen, onClose }: SettingsModalProps) {
    const [view, setView] = useState<'menu' | 'tax-profile'>('menu');
    const [showHelp, setShowHelp] = useState(false);

    // Reset view when modal closes
    const handleClose = () => {
        setView('menu');
        onClose();
    };

    return (
        <AnimatePresence>
            {isOpen && (
                <>
                    {/* Backdrop */}
                    <motion.div
                        key="settings-backdrop"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={handleClose}
                        className="fixed inset-0 bg-black/50 z-[60] backdrop-blur-sm"
                    />

                    {/* Modal Content */}
                    <motion.div
                        key="settings-modal"
                        initial={{ opacity: 0, y: '100%' }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: '100%' }}
                        className="fixed inset-x-4 bottom-4 top-auto max-h-[85vh] bg-background rounded-2xl shadow-xl z-[70] flex flex-col font-sans"
                    >
                        {/* Header */}
                        <div className="flex items-center justify-between p-4 border-b border-border">
                            <div className="flex items-center gap-2">
                                {view === 'tax-profile' && (
                                    <button
                                        onClick={() => setView('menu')}
                                        className="p-1 hover:bg-muted rounded-full transition-colors"
                                    >
                                        <ChevronLeft className="w-5 h-5" />
                                    </button>
                                )}
                                <h2 className="text-lg font-bold font-heading">
                                    {view === 'tax-profile' ? 'Tax Profile' : 'Settings'}
                                </h2>
                            </div>
                            <button
                                onClick={handleClose}
                                className="p-2 hover:bg-muted rounded-full transition-colors text-muted-foreground hover:text-foreground"
                            >
                                <X className="w-5 h-5" />
                            </button>
                        </div>

                        {/* Body - allow overflow for dropdowns, but vertical scroll if needed.
                            However, absolute dropdowns inside overflow-y-auto get clipped. 
                            Since we want dropdowns to be visible, we should ideally use Portal or have enough space.
                            For now, making it overflow-visible might break scrolling if content is long.
                            Given the screenshot, the menu isn't that long. I will set overflow-y-auto but add padding-bottom.
                            Or better: use a fixed height container for the dropdowns if I can't use Portal.
                            
                            Actually, the user screenshot shows the dropdown OVERLAYING. 
                            The only way to achieve overlay in a scrolling container without Portal is if the container DOES NOT clip.
                            I will remove `overflow-hidden` from the main modal container and `overflow-y-auto` from the body 
                            and instead rely on max-height. 
                            Use overflow-visible for now to let dropdowns show.
                        */}
                        <div className="flex-1 overflow-visible p-4 pb-20">
                            {view === 'menu' ? (
                                <SettingsMenu
                                    onOpenTaxProfile={() => setView('tax-profile')}
                                    onOpenHelp={() => setShowHelp(true)}
                                    onLogout={handleClose}
                                />
                            ) : (
                                <TaxProfileForm />
                            )}
                        </div>
                    </motion.div>
                </>
            )}

            {/* Help Modal is independent */}
            <HelpModal isOpen={showHelp} onClose={() => setShowHelp(false)} />
        </AnimatePresence>
    );
}
