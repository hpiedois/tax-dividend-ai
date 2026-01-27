import { useTranslation } from 'react-i18next';
import { AlertCircle } from 'lucide-react';
import { Card } from '../ui/Card';

interface FormPreviewProps {
  pdfUrl: string;
}

export function FormPreview({ pdfUrl }: FormPreviewProps) {
  const { t } = useTranslation();

  return (
    <Card className="p-0 overflow-hidden">
      <div className="p-4 bg-muted border-b border-border">
        <h3 className="font-heading font-bold">{t('forms.preview_title')}</h3>
        <p className="text-sm text-muted-foreground">{t('forms.preview_subtitle')}</p>
      </div>

      <div className="relative" style={{ height: 'calc(100vh - 200px)' }}>
        <iframe
          src={pdfUrl}
          className="w-full h-full border-0"
          title={t('forms.preview_title')}
        />

        {/* Fallback for browsers that don't support PDF preview */}
        <noscript>
          <div className="absolute inset-0 flex items-center justify-center bg-background">
            <div className="text-center p-6">
              <AlertCircle className="w-12 h-12 text-orange-600 mx-auto mb-4" />
              <p className="text-muted-foreground mb-4">
                {t('forms.preview_not_supported')}
              </p>
              <a
                href={pdfUrl}
                download
                className="inline-flex items-center px-4 py-2 rounded-lg bg-brand-600 text-white hover:bg-brand-700 transition"
              >
                {t('forms.download_instead')}
              </a>
            </div>
          </div>
        </noscript>
      </div>
    </Card>
  );
}
