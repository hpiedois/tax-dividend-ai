import { useTranslation } from 'react-i18next';
import { useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { Upload, FileText } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { Card } from '../ui/Card';

interface DropZoneProps {
    onFileSelect: (files: File[]) => void;
}

export function DropZone({ onFileSelect }: DropZoneProps) {
    const { t } = useTranslation();
    const onDrop = useCallback((acceptedFiles: File[]) => {
        if (acceptedFiles.length > 0) {
            onFileSelect(acceptedFiles);
        }
    }, [onFileSelect]);

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        accept: {
            'application/pdf': ['.pdf'],
            'image/*': ['.png', '.jpg', '.jpeg']
        },
        maxFiles: 10,
    });

    return (
        <Card className="relative overflow-hidden group border-2 border-dashed border-border hover:border-primary hover:bg-primary/5 transition-all duration-300">
            <div
                {...getRootProps()}
                className="cursor-pointer min-h-[300px] flex flex-col items-center justify-center text-center p-8 transition-colors"
            >
                <input {...getInputProps()} capture="environment" />

                <AnimatePresence>
                    {isDragActive ? (
                        <motion.div
                            initial={{ scale: 0.8, opacity: 0 }}
                            animate={{ scale: 1.1, opacity: 1 }}
                            exit={{ scale: 0.8, opacity: 0 }}
                            className="bg-primary/10 p-6 rounded-full"
                        >
                            <Upload className="w-12 h-12 text-primary" />
                        </motion.div>
                    ) : (
                        <motion.div
                            initial={{ scale: 1, opacity: 1 }}
                            animate={{ scale: 1, opacity: 1 }}
                            exit={{ scale: 0.8, opacity: 0 }}
                            className="bg-muted p-6 rounded-full group-hover:bg-card group-hover:shadow-lg transition-all"
                        >
                            <FileText className="w-12 h-12 text-muted-foreground group-hover:text-primary transition-colors" />
                        </motion.div>
                    )}
                </AnimatePresence>

                <div className="mt-6 space-y-2">
                    <p className="text-xl font-heading font-medium text-foreground group-hover:text-primary transition-colors">
                        {isDragActive ? t('upload.drop_title_active') : t('upload.drop_title_idle')}
                    </p>
                    <p className="text-muted-foreground">
                        {t('upload.drop_subtitle')} (PDF, Photo)
                    </p>
                </div>
            </div>
        </Card>
    );
}
