import { useAtom, useSetAtom } from 'jotai';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ArrowRight, CheckCircle, RefreshCcw, FileText, Wallet } from 'lucide-react';
import {
  scanStepAtom,
  scanResultsAtom,
  processingCountAtom,
  totalGrossAmountAtom,
  totalReclaimableAtom,
  resetScanAtom,
} from '../../store';
import { DropZone } from '../upload/DropZone';
import { ScanningOverlay } from '../upload/ScanningOverlay';
import { Button } from '../ui/Button';
import { Card } from '../ui/Card';
import { validateFiles } from '../../lib/validation';
import { dividendsApi } from '../../api/clients';
import { showError, showSuccess } from '../../lib/toast-helpers';

// ...

export function ScanView() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [scanStep, setScanStep] = useAtom(scanStepAtom);
  const [scanResults, setScanResults] = useAtom(scanResultsAtom);
  const [processingCount, setProcessingCount] = useAtom(processingCountAtom);
  const [totalGross] = useAtom(totalGrossAmountAtom);
  const [totalReclaimable] = useAtom(totalReclaimableAtom);
  const resetScan = useSetAtom(resetScanAtom);

  const handleFilesSelect = async (files: File[]) => {
    // Validate files
    const { valid, invalid } = validateFiles(files);

    // Show errors for invalid files
    if (invalid.length > 0) {
      invalid.forEach(({ error }) => {
        showError(t(error));
      });
    }

    // Process valid files
    if (valid.length === 0) {
      return;
    }

    showSuccess(t('toast.files_validated', { count: valid.length }));

    setScanStep('scanning');
    setProcessingCount({ current: 0, total: valid.length });

    const results: any[] = [];
    for (let i = 0; i < valid.length; i++) {
      setProcessingCount((prev) => ({ ...prev, current: i + 1 }));
      try {
        const response = await dividendsApi.parseDividendStatement(valid[i]);
        if (response.data.dividends) {
          // Map API data to local type (add status)
          const mappedDividends = response.data.dividends.map(d => ({
            ...d,
            status: 'OPEN' as const
          }));
          results.push(...mappedDividends);
        }
      } catch (error) {
        console.error('Error parsing', error);
        showError(t('validation.error.generic'));
      }
    }

    // Update scan results atom
    setScanResults(results);

    setScanStep('result');
  };

  const startNewScan = () => {
    resetScan();
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <section className="text-center space-y-4 py-8">
        <h1 className="text-4xl font-bold font-heading text-foreground leading-tight">
          {t('hero.title_start')} <span className="text-brand-600">{t('hero.title_highlight')}</span>
        </h1>
        <p className="text-muted-foreground text-lg">{t('hero.subtitle')}</p>
      </section>

      <div className="min-h-[400px]">
        {scanStep === 'upload' && <DropZone onFileSelect={handleFilesSelect} />}

        {scanStep === 'scanning' && (
          <ScanningOverlay
            progress={`${t('scanning.title')} (${processingCount.current}/${processingCount.total})`}
          />
        )}

        {scanStep === 'result' && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-6"
          >
            {/* Summary Card */}
            <Card className="p-6 bg-gradient-to-br from-brand-50 to-brand-100/50 dark:from-brand-900/20 dark:to-brand-800/10 border-brand-200 dark:border-brand-800">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-10 h-10 rounded-full bg-brand-600 flex items-center justify-center">
                  <CheckCircle className="w-6 h-6 text-white" />
                </div>
                <h3 className="text-lg font-bold font-heading">{t('result.scan_completed')}</h3>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <div className="flex items-center gap-2 text-muted-foreground text-sm">
                    <Wallet className="w-4 h-4" />
                    {t('result.gross_amount')}
                  </div>
                  <p className="text-2xl font-bold">{totalGross.toFixed(2)} €</p>
                </div>
                <div className="space-y-1">
                  <div className="flex items-center gap-2 text-muted-foreground text-sm">
                    <FileText className="w-4 h-4" />
                    {t('result.reclaimable_est')}
                  </div>
                  <p className="text-2xl font-bold text-brand-600">{totalReclaimable.toFixed(2)} €</p>
                </div>
              </div>
            </Card>

            {/* Results Grid */}
            <div className="grid gap-4">
              {scanResults.map((result, idx) => (
                <motion.div
                  key={idx}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: idx * 0.05 }}
                >
                  <Card className="p-4 hover:shadow-lg transition-shadow">
                    <div className="flex flex-col md:flex-row md:justify-between md:items-start md:mb-3 gap-2 mb-4">
                      <div>
                        <h4 className="font-bold font-heading text-lg">{result.securityName}</h4>
                        <p className="text-sm text-muted-foreground break-all">{result.isin}</p>
                      </div>
                      <span className="text-xs bg-muted px-2 py-1 rounded w-fit">
                        {t('result.payment_date')}: {result.paymentDate}
                      </span>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
                      <div>
                        <span className="text-muted-foreground">{t('result.gross_amount')}</span>
                        <p className="font-semibold">{result.grossAmount.toFixed(2)} {result.currency}</p>
                      </div>
                      <div>
                        <span className="text-muted-foreground">{t('result.withholding_tax')}</span>
                        <p className="font-semibold">{(result.withholdingTax ?? 0).toFixed(2)} {result.currency}</p>
                      </div>
                      <div>
                        <span className="text-muted-foreground">{t('result.reclaimable_est')}</span>
                        <p className="font-semibold text-brand-600">
                          {(result.reclaimableAmount ?? 0).toFixed(2)} {result.currency}
                        </p>
                      </div>
                    </div>
                  </Card>
                </motion.div>
              ))}
            </div>

            <div className="p-4 md:p-6 bg-muted/30 border-t border-border flex flex-row justify-center md:justify-end gap-2 md:gap-3 rounded-xl">
              <Button variant="ghost" onClick={startNewScan} className="flex-1 md:flex-none md:w-auto justify-center text-sm md:text-base">
                <RefreshCcw className="w-4 h-4 mr-2" /> {t('result.new_scan')}
              </Button>
              <Button onClick={() => navigate('/forms')} className="flex-1 md:flex-none md:w-auto justify-center text-sm md:text-base whitespace-nowrap">
                <span className="hidden md:inline">{t('result.generate_forms')}</span>
                <span className="md:hidden">Forms</span>
                <ArrowRight className="w-4 h-4 ml-2" />
              </Button>
            </div>
          </motion.div>
        )}
      </div>
    </div>
  );
}
