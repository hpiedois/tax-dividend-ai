import { useTranslation } from 'react-i18next';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { User, MapPin, Hash } from 'lucide-react';

export function TaxProfileForm() {
    const { t } = useTranslation();

    return (
        <Card className="space-y-6">
            <div className="flex items-center gap-4 border-b border-border pb-6">
                <div className="w-16 h-16 rounded-full bg-muted flex items-center justify-center text-muted-foreground">
                    <User className="w-8 h-8" />
                </div>
                <div>
                    <h3 className="text-lg font-bold text-foreground">John Doe</h3>
                    <p className="text-slate-500 text-sm">{t('settings.fiscal_resident')}</p>
                </div>
            </div>

            <div className="space-y-4">
                <h4 className="font-heading font-semibold flex items-center gap-2 text-foreground">
                    <MapPin className="w-4 h-4 text-brand-500" /> {t('settings.section_address')}
                </h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <Input label={t('settings.street')} defaultValue="Rue de la Gare" />
                    <Input label={t('settings.number')} defaultValue="12" />
                    <Input label={t('settings.zip')} defaultValue="1003" />
                    <Input label={t('settings.city')} defaultValue="Lausanne" />
                </div>
            </div>

            <div className="space-y-4 pt-4 border-t border-border">
                <h4 className="font-heading font-semibold flex items-center gap-2 text-foreground">
                    <Hash className="w-4 h-4 text-brand-500" /> {t('settings.section_id')}
                </h4>
                <Input label={t('settings.avs_nif')} defaultValue="756.1234.5678.90" />
                <Input label={t('settings.tax_office')} defaultValue="Administration Cantonale des Impôts - Vaud" />
            </div>

            <div className="pt-6 flex justify-end">
                <Button>{t('settings.save_button')}</Button>
            </div>
        </Card>
    );
}
