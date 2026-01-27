import { useTranslation } from 'react-i18next';
import { motion } from 'framer-motion';
import { ScanLine } from 'lucide-react';

export function ScanningOverlay({ progress }: { progress?: string }) {
    const { t } = useTranslation();
    return (
        <div className="flex flex-col items-center justify-center p-12 min-h-[400px]">
            <div className="relative w-32 h-32 mb-8">
                {/* Radar Waves */}
                <motion.div
                    animate={{
                        scale: [1, 1.5, 2],
                        opacity: [0.5, 0],
                    }}
                    transition={{
                        duration: 2,
                        repeat: Infinity,
                        ease: "easeOut",
                    }}
                    className="absolute inset-0 bg-brand-500/20 rounded-full"
                />
                <motion.div
                    animate={{
                        scale: [1, 1.5],
                        opacity: [0.5, 0],
                    }}
                    transition={{
                        duration: 2,
                        repeat: Infinity,
                        ease: "easeOut",
                        delay: 0.5,
                    }}
                    className="absolute inset-0 bg-brand-500/20 rounded-full"
                />

                {/* Icon Container */}
                <div className="absolute inset-0 bg-card rounded-full flex items-center justify-center shadow-lg border border-border z-10">
                    <ScanLine className="w-10 h-10 text-primary" />
                </div>
            </div>

            <motion.p
                animate={{ opacity: [0.5, 1, 0.5] }}
                transition={{ duration: 1.5, repeat: Infinity }}
                className="text-lg font-heading font-medium text-foreground"
            >
                {progress || t('scanning.title')}
            </motion.p>
            <p className="text-muted-foreground mt-2 text-sm">{t('scanning.subtitle')}</p>
        </div>
    );
}
