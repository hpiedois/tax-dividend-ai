import { useState, useMemo } from 'react';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';

import { MultiSelect, type Option } from '../ui/MultiSelect';
import { Download, FileText, Search, Filter } from 'lucide-react';
import { MOCK_HISTORY, type MockCase } from '../../lib/mock-db';

export function HistoryView() {
    const { t } = useTranslation();
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedYear, setSelectedYear] = useState<string>('all');
    const [selectedStatuses, setSelectedStatuses] = useState<string[]>([]);

    // Extract unique years from history
    const availableYears = useMemo(() => {
        const years = new Set(MOCK_HISTORY.map(item => item.date.split('-')[0]));
        return Array.from(years).sort().reverse();
    }, []);

    // Status options
    const statusOptions: Option[] = [
        { label: t('history.status.pending'), value: 'pending' },
        { label: t('history.status.submitted'), value: 'submitted' },
        { label: t('history.status.refunded'), value: 'refunded' },
    ];

    // Filter history based on search, year and status
    const filteredHistory = useMemo(() => {
        return MOCK_HISTORY.filter(item => {
            const matchesSearch = item.security.toLowerCase().includes(searchQuery.toLowerCase());
            const matchesYear = selectedYear === 'all' || item.date.startsWith(selectedYear);
            const matchesStatus = selectedStatuses.length === 0 || selectedStatuses.includes(item.status);
            return matchesSearch && matchesYear && matchesStatus;
        });
    }, [searchQuery, selectedYear, selectedStatuses]);

    const getStatusBadge = (status: MockCase['status']) => {
        switch (status) {
            case 'pending':
                return <span className="px-2 py-1 rounded-full text-xs font-medium bg-orange-100 text-orange-700">{t('history.status.pending')}</span>;
            case 'submitted':
                return <span className="px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-700">{t('history.status.submitted')}</span>;
            case 'refunded':
                return <span className="px-2 py-1 rounded-full text-xs font-medium bg-emerald-100 text-emerald-700">{t('history.status.refunded')}</span>;
        }
    };

    return (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-6">
            <div className="flex flex-row justify-between items-center gap-4">
                <h2 className="text-2xl md:text-3xl font-heading font-bold text-foreground">{t('app.history')}</h2>
                <Button variant="secondary" size="sm">
                    <Download className="w-4 h-4 mr-2" />
                    <span className="hidden sm:inline">{t('history.export_csv')}</span>
                    <span className="sm:hidden">CSV</span>
                </Button>
            </div>

            {/* Filters */}
            <Card className="p-4 bg-card/50 backdrop-blur-sm border-border/50">
                <div className="flex flex-col md:flex-row gap-4">
                    <div className="relative flex-[2]">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                        <input
                            type="text"
                            placeholder={t('history.filters.search_placeholder')}
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="w-full pl-9 pr-4 py-2 bg-background border border-border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all placeholder:text-muted-foreground"
                        />
                    </div>

                    <MultiSelect
                        options={statusOptions}
                        selected={selectedStatuses}
                        onChange={setSelectedStatuses}
                        placeholder={t('history.filters.status_label')}
                    />

                    <div className="relative flex-1 min-w-[140px]">
                        <Filter className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground pointer-events-none" />
                        <select
                            value={selectedYear}
                            onChange={(e) => setSelectedYear(e.target.value)}
                            className="w-full pl-9 pr-8 py-2 bg-background border border-border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all appearance-none cursor-pointer text-foreground"
                        >
                            <option value="all">{t('history.filters.all_years')}</option>
                            {availableYears.map(year => (
                                <option key={year} value={year}>{year}</option>
                            ))}
                        </select>
                        <div className="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
                            <svg className="w-4 h-4 text-muted-foreground" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" /></svg>
                        </div>
                    </div>
                </div>
            </Card>

            <Card className="overflow-hidden p-0 bg-transparent border-none md:bg-card md:border-border">
                {/* Mobile View: Simple List */}
                <div className="block md:hidden space-y-3">
                    {filteredHistory.length === 0 ? (
                        <div className="text-center py-8 text-muted-foreground">
                            {t('forms.no_data')}
                        </div>
                    ) : (
                        filteredHistory.map((item) => (
                            <div key={item.id} className="bg-card p-4 rounded-xl border border-border shadow-sm flex flex-col gap-3">
                                <div className="flex justify-between items-start">
                                    <div>
                                        <span className="text-xs text-muted-foreground font-mono">{item.date}</span>
                                        <h4 className="font-bold text-foreground">{item.security}</h4>
                                    </div>
                                    {getStatusBadge(item.status)}
                                </div>
                                <div className="flex justify-between items-center pt-2 border-t border-border/50">
                                    <div>
                                        <div className="text-xs text-muted-foreground">{t('history.columns.reclaimed')}</div>
                                        <div className="font-bold text-emerald-600">+{item.reclaimedAmount.toFixed(2)} €</div>
                                    </div>
                                    <button className="p-2 text-brand-600 hover:bg-brand-50 rounded-lg transition-colors">
                                        <FileText className="w-5 h-5" />
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {/* Desktop View: Full Table */}
                <div className="hidden md:block overflow-x-auto bg-card rounded-xl border border-border">
                    <table className="w-full text-left text-sm">
                        <thead className="bg-muted/50 border-b border-border">
                            <tr>
                                <th className="px-6 py-4 font-heading font-semibold text-foreground">{t('history.columns.date')}</th>
                                <th className="px-6 py-4 font-heading font-semibold text-foreground">{t('history.columns.security')}</th>
                                <th className="px-6 py-4 font-heading font-semibold text-foreground text-right">{t('history.columns.gross_amount')}</th>
                                <th className="px-6 py-4 font-heading font-semibold text-foreground text-right">{t('history.columns.reclaimed')}</th>
                                <th className="px-6 py-4 font-heading font-semibold text-foreground text-center">{t('history.columns.status')}</th>
                                <th className="px-6 py-4 font-heading font-semibold text-foreground text-right">{t('history.columns.actions')}</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-border">
                            {filteredHistory.length === 0 ? (
                                <tr>
                                    <td colSpan={6} className="px-6 py-8 text-center text-muted-foreground">
                                        {t('forms.no_data')}
                                    </td>
                                </tr>
                            ) : (
                                filteredHistory.map((item) => (
                                    <tr key={item.id} className="hover:bg-muted/50 transition-colors">
                                        <td className="px-6 py-4 text-slate-600 font-mono text-xs">{item.date}</td>
                                        <td className="px-6 py-4 font-medium text-foreground">{item.security}</td>
                                        <td className="px-6 py-4 text-right text-slate-600">{item.grossAmount.toFixed(2)} €</td>
                                        <td className="px-6 py-4 text-right font-medium text-emerald-600">+{item.reclaimedAmount.toFixed(2)} €</td>
                                        <td className="px-6 py-4 text-center">{getStatusBadge(item.status)}</td>
                                        <td className="px-6 py-4 text-right">
                                            <button className="text-brand-600 hover:text-brand-800 font-medium inline-flex items-center text-xs">
                                                <FileText className="w-3 h-3 mr-1" /> PDF
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </Card>
        </motion.div>
    );
}
