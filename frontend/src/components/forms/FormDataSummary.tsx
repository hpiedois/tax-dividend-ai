import { useTranslation } from 'react-i18next';
import { TrendingUp, DollarSign, Receipt, ArrowDownCircle } from 'lucide-react';
import { Card } from '../ui/Card';
import type { Dividend } from '../../types/dividend.types';

interface FormDataSummaryProps {
  dividends: Dividend[];
  taxYear: number;
}

export function FormDataSummary({ dividends, taxYear }: FormDataSummaryProps) {
  const { t } = useTranslation();

  const totalGross = dividends.reduce((sum, d) => sum + d.grossAmount, 0);
  const totalWithholding = dividends.reduce((sum, d) => sum + (d.withholdingTax ?? 0), 0);
  const totalReclaimable = dividends.reduce((sum, d) => sum + (d.reclaimableAmount ?? 0), 0);
  const totalTreaty = dividends.reduce((sum, d) => sum + d.grossAmount * 0.15, 0);

  return (
    <Card className="p-6">
      <h3 className="text-lg font-heading font-bold mb-4">
        {t('forms.data_summary')} ({taxYear})
      </h3>

      {/* Statistics Grid */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        <div className="text-center p-4 rounded-lg bg-muted/50">
          <Receipt className="w-5 h-5 text-muted-foreground mx-auto mb-2" />
          <p className="text-sm text-muted-foreground">{t('forms.dividends_count')}</p>
          <p className="text-2xl font-bold font-heading">{dividends.length}</p>
        </div>
        <div className="text-center p-4 rounded-lg bg-blue-50 dark:bg-blue-900/20">
          <TrendingUp className="w-5 h-5 text-blue-600 mx-auto mb-2" />
          <p className="text-sm text-muted-foreground">{t('result.gross_amount')}</p>
          <p className="text-2xl font-bold font-heading text-blue-600">{totalGross.toFixed(2)} €</p>
        </div>
        <div className="text-center p-4 rounded-lg bg-orange-50 dark:bg-orange-900/20">
          <DollarSign className="w-5 h-5 text-orange-600 mx-auto mb-2" />
          <p className="text-sm text-muted-foreground">{t('result.withholding_tax')}</p>
          <p className="text-2xl font-bold font-heading text-orange-600">{totalWithholding.toFixed(2)} €</p>
        </div>
        <div className="text-center p-4 rounded-lg bg-green-50 dark:bg-green-900/20">
          <ArrowDownCircle className="w-5 h-5 text-green-600 mx-auto mb-2" />
          <p className="text-sm text-muted-foreground">{t('result.reclaimable_est')}</p>
          <p className="text-2xl font-bold font-heading text-green-600">{totalReclaimable.toFixed(2)} €</p>
        </div>
      </div>

      {/* Calculation Details */}
      <div className="space-y-2 p-4 bg-muted/30 rounded-lg text-sm">
        <div className="flex justify-between">
          <span className="text-muted-foreground">{t('forms.total_gross')}</span>
          <span className="font-semibold">{totalGross.toFixed(2)} €</span>
        </div>
        <div className="flex justify-between">
          <span className="text-muted-foreground">{t('forms.french_withholding')}</span>
          <span className="font-semibold text-red-600">- {totalWithholding.toFixed(2)} €</span>
        </div>
        <div className="flex justify-between">
          <span className="text-muted-foreground">{t('forms.treaty_amount')} (15%)</span>
          <span className="font-semibold text-green-600">+ {totalTreaty.toFixed(2)} €</span>
        </div>
        <div className="border-t border-border pt-2 flex justify-between">
          <span className="font-bold">{t('forms.net_reclaimable')}</span>
          <span className="font-bold text-brand-600 text-lg">{totalReclaimable.toFixed(2)} €</span>
        </div>
      </div>

      {/* Dividends List */}
      <div className="mt-6">
        <h4 className="text-sm font-semibold text-muted-foreground mb-3">
          {t('forms.dividends_detail')}
        </h4>
        <div className="space-y-2 max-h-64 overflow-y-auto">
          {dividends.map((dividend, index) => (
            <div
              key={index}
              className="flex justify-between items-center p-3 rounded-lg bg-background border border-border hover:border-brand-300 transition"
            >
              <div>
                <p className="font-semibold text-sm">{dividend.securityName}</p>
                <p className="text-xs text-muted-foreground">
                  {dividend.isin ?? 'N/A'} • {dividend.paymentDate ?? 'N/A'}
                </p>
              </div>
              <div className="text-right">
                <p className="font-semibold">{dividend.grossAmount.toFixed(2)} {dividend.currency}</p>
                <p className="text-xs text-green-600">
                  +{(dividend.reclaimableAmount ?? 0).toFixed(2)} {dividend.currency}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Card>
  );
}
