import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { AlertCircle, FileText } from 'lucide-react';
import { Card } from '../ui/Card';
import { cn } from '../../lib/utils';

interface FormPreviewProps {
  pdfUrl?: string; // Fallback
  pdf5000Url?: string;
  pdf5001Url?: string;
}

export function FormPreview({ pdfUrl, pdf5000Url, pdf5001Url }: FormPreviewProps) {
  const { t } = useTranslation();

  // Determine available tabs
  const hasTabs = !!pdf5000Url && !!pdf5001Url;
  const [activeTab, setActiveTab] = useState<'5000' | '5001' | 'single'>(hasTabs ? '5000' : 'single');

  const currentPdfUrl =
    activeTab === '5000' ? pdf5000Url :
      activeTab === '5001' ? pdf5001Url :
        pdfUrl;

  return (
    <Card className="p-0 overflow-hidden flex flex-col h-full bg-background border-border">
      <div className="flex items-center justify-between px-4 py-3 bg-muted/30 border-b border-border">
        <div>
          <h3 className="font-heading font-bold text-foreground">{t('forms.preview_title')}</h3>
          <p className="text-xs text-muted-foreground">{t('forms.preview_subtitle')}</p>
        </div>

        {hasTabs && (
          <div className="flex bg-muted p-1 rounded-lg">
            <button
              onClick={() => setActiveTab('5000')}
              className={cn(
                "px-4 py-1.5 text-sm font-medium rounded-md transition-all flex items-center gap-2",
                activeTab === '5000'
                  ? "bg-white dark:bg-slate-700 text-brand-600 shadow-sm"
                  : "text-muted-foreground hover:text-foreground hover:bg-white/50"
              )}
            >
              <FileText className="w-4 h-4" />
              Form 5000
            </button>
            <button
              onClick={() => setActiveTab('5001')}
              className={cn(
                "px-4 py-1.5 text-sm font-medium rounded-md transition-all flex items-center gap-2",
                activeTab === '5001'
                  ? "bg-white dark:bg-slate-700 text-brand-600 shadow-sm"
                  : "text-muted-foreground hover:text-foreground hover:bg-white/50"
              )}
            >
              <FileText className="w-4 h-4" />
              Form 5001
            </button>
          </div>
        )}
      </div>

      <div className="relative flex-grow bg-slate-100 dark:bg-slate-900" style={{ minHeight: '600px', height: 'calc(100vh - 250px)' }}>
        {currentPdfUrl ? (
          <embed
            key={currentPdfUrl} // Force re-render when switching tabs
            src={`${currentPdfUrl}#toolbar=0&navpanes=0&scrollbar=1&view=FitH`}
            type="application/pdf"
            className="w-full h-full"
            title={t('forms.preview_title')}
          />
        ) : (
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="text-center p-6 max-w-sm">
              <AlertCircle className="w-10 h-10 text-muted-foreground mx-auto mb-3 opacity-50" />
              <p className="text-muted-foreground">PDF Preview not available.</p>
            </div>
          </div>
        )}
      </div>
    </Card>
  );
}
