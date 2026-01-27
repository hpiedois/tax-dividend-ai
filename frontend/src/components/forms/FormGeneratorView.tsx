import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAtom } from 'jotai';
import { motion } from 'framer-motion';
import { FileText, Download, Calendar, User, MapPin, Hash } from 'lucide-react';
import { userAtom, scanResultsAtom } from '../../store';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { FormPreview } from './FormPreview';
import { FormDataSummary } from './FormDataSummary';
import { useGenerateForm } from '../../hooks/useFormGeneration';
import type { Form5001Data } from '../../types/form.types';

export function FormGeneratorView() {
  const { t } = useTranslation();
  const [user] = useAtom(userAtom);
  const [scanResults] = useAtom(scanResultsAtom);
  const [showPreview, setShowPreview] = useState(false);
  const [generatedForm, setGeneratedForm] = useState<{ pdfUrl: string; formId: string } | null>(null);

  // Form data state
  const [taxYear, setTaxYear] = useState(new Date().getFullYear() - 1);
  const [address, setAddress] = useState('');
  const [city, setCity] = useState('');
  const [postalCode, setPostalCode] = useState('');

  const { mutate: generateForm, isPending } = useGenerateForm();

  // Validation: require address fields, taxId is optional (can be filled later)
  const canGenerate = scanResults.length > 0 && address && city && postalCode;

  const handleGenerate = () => {
    if (!canGenerate || !user) return;

    const formData: Form5001Data = {
      taxpayerName: user.fullName || user.email,
      taxId: user.taxId || '',
      address,
      city,
      postalCode,
      country: 'Suisse', // Swiss resident claiming back French tax
      taxYear,
      dividends: scanResults.map((result) => ({
        securityName: result.securityName,
        isin: result.isin,
        paymentDate: result.paymentDate,
        grossAmount: result.grossAmount,
        currency: result.currency,
        withholdingTax: result.withholdingTax,
        treatyAmount: result.grossAmount * 0.15, // Treaty rate
        reclaimableAmount: result.reclaimableAmount,
        frenchRate: result.frenchRate,
      })),
      totalGrossAmount: scanResults.reduce((sum, r) => sum + r.grossAmount, 0),
      totalWithholdingTax: scanResults.reduce((sum, r) => sum + r.withholdingTax, 0),
      totalTreatyAmount: scanResults.reduce((sum, r) => sum + r.grossAmount * 0.15, 0),
      totalReclaimableAmount: scanResults.reduce((sum, r) => sum + r.reclaimableAmount, 0),
    };

    generateForm(
      { formData, includeForm5000: true },
      {
        onSuccess: (data) => {
          setGeneratedForm({ pdfUrl: data.pdfUrl, formId: data.formId });
          setShowPreview(true);
        },
      }
    );
  };

  if (scanResults.length === 0) {
    return (
      <div className="max-w-4xl mx-auto">
        <Card className="p-12 text-center">
          <FileText className="w-16 h-16 text-muted-foreground mx-auto mb-4" />
          <h2 className="text-2xl font-heading font-bold mb-2">{t('forms.no_data')}</h2>
          <p className="text-muted-foreground mb-6">{t('forms.scan_first')}</p>
          <Button onClick={() => window.location.href = '/scan'}>
            {t('result.new_scan')}
          </Button>
        </Card>
      </div>
    );
  }

  if (showPreview && generatedForm) {
    return (
      <div className="max-w-6xl mx-auto">
        <div className="mb-6 flex items-center justify-between">
          <Button variant="ghost" onClick={() => setShowPreview(false)}>
            ← {t('common.back')}
          </Button>
          <Button onClick={() => window.open(`/api/forms/${generatedForm.formId}/download`, '_blank')}>
            <Download className="w-4 h-4 mr-2" />
            {t('forms.download')}
          </Button>
        </div>
        <FormPreview pdfUrl={generatedForm.pdfUrl} />
      </div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="max-w-4xl mx-auto space-y-6"
    >
      <div className="flex items-center gap-3">
        <div className="w-12 h-12 rounded-xl bg-brand-600 flex items-center justify-center">
          <FileText className="w-6 h-6 text-white" />
        </div>
        <div>
          <h1 className="text-3xl font-heading font-bold">{t('forms.title')}</h1>
          <p className="text-muted-foreground">{t('forms.subtitle')}</p>
        </div>
      </div>

      {/* Personal Information Form */}
      <Card className="p-6">
        <h3 className="text-lg font-heading font-bold mb-4 flex items-center gap-2">
          <User className="w-5 h-5" />
          {t('forms.personal_info')}
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-muted-foreground mb-2">
              {t('forms.full_name')}
            </label>
            <input
              type="text"
              value={user?.fullName || user?.email || ''}
              disabled
              className="w-full px-4 py-2 rounded-lg border border-border bg-muted text-muted-foreground"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-muted-foreground mb-2 flex items-center gap-2">
              <Hash className="w-4 h-4" />
              {t('forms.tax_id')}
            </label>
            <input
              type="text"
              value={user?.taxId || ''}
              disabled
              className="w-full px-4 py-2 rounded-lg border border-border bg-muted text-muted-foreground"
            />
          </div>
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-foreground mb-2 flex items-center gap-2">
              <MapPin className="w-4 h-4" />
              {t('forms.address')} <span className="text-red-600">*</span>
            </label>
            <input
              type="text"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              placeholder="123 Rue de la République"
              required
              className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-foreground mb-2">
              {t('forms.city')} <span className="text-red-600">*</span>
            </label>
            <input
              type="text"
              value={city}
              onChange={(e) => setCity(e.target.value)}
              placeholder="Paris"
              required
              className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-foreground mb-2">
              {t('forms.postal_code')} <span className="text-red-600">*</span>
            </label>
            <input
              type="text"
              value={postalCode}
              onChange={(e) => setPostalCode(e.target.value)}
              placeholder="75001"
              required
              className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
            />
          </div>
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-foreground mb-2 flex items-center gap-2">
              <Calendar className="w-4 h-4" />
              {t('forms.tax_year')}
            </label>
            <select
              value={taxYear}
              onChange={(e) => setTaxYear(Number(e.target.value))}
              className="w-full px-4 py-2 rounded-lg border border-border bg-background focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
            >
              {[0, 1, 2].map((offset) => {
                const year = new Date().getFullYear() - offset;
                return (
                  <option key={year} value={year}>
                    {year}
                  </option>
                );
              })}
            </select>
          </div>
        </div>
      </Card>

      {/* Data Summary */}
      <FormDataSummary dividends={scanResults} taxYear={taxYear} />

      {/* Generate Button */}
      <div className="space-y-3">
        {!canGenerate && (
          <div className="p-3 bg-orange-50 dark:bg-orange-900/20 border border-orange-200 dark:border-orange-800 rounded-lg text-sm text-orange-800 dark:text-orange-200">
            {scanResults.length === 0 && (
              <p>⚠️ {t('forms.scan_first')}</p>
            )}
            {scanResults.length > 0 && (!address || !city || !postalCode) && (
              <p>⚠️ {t('forms.fill_required_fields')}</p>
            )}
          </div>
        )}
        <div className="flex justify-end">
          <Button onClick={handleGenerate} disabled={!canGenerate} isLoading={isPending}>
            <FileText className="w-4 h-4 mr-2" />
            {t('forms.generate')}
          </Button>
        </div>
      </div>
    </motion.div>
  );
}
