import { useState, useMemo } from 'react';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';

import { MultiSelect, type Option } from '../ui/MultiSelect';
import { FileText, Search, Filter, CheckCircle, Send, Wallet } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { dividendsApi } from '../../api/clients';
import type { DividendCase } from '../../api/generated';

const fetchHistory = async () => {
    const response = await dividendsApi.getDividendHistory(0, 100); // Fetch all for now
    return response.data;
};

export function HistoryView() {
    const { t, i18n } = useTranslation();
    const queryClient = useQueryClient();
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedYear, setSelectedYear] = useState<string>('all');
    const [selectedStatuses, setSelectedStatuses] = useState<string[]>([]);
    const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());
    const [isUpdating, setIsUpdating] = useState(false);

    const { data: historyResponse } = useQuery({
        queryKey: ['dividend-history'],
        queryFn: fetchHistory
    });

    const historyData = historyResponse?.data || [];

    const updateStatusMutation = useMutation({
        mutationFn: async ({ ids, status }: { ids: string[], status: 'OPEN' | 'SENT' | 'PAID' }) => {
            await dividendsApi.updateDividendStatus({ ids, status });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['dividend-history'] });
            queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
            setSelectedIds(new Set());
            setIsUpdating(false);
        },
        onError: () => setIsUpdating(false)
    });

    const [confirmationData, setConfirmationData] = useState<{ isOpen: boolean; status: 'OPEN' | 'SENT' | 'PAID' | null; count: number }>({
        isOpen: false,
        status: null,
        count: 0
    });

    // Extract unique years from history
    const availableYears = useMemo(() => {
        if (!historyData) return [];
        const years = new Set(historyData
            .map(item => item.date ? item.date.split('-')[0] : '')
            .filter(y => y !== '')
        );
        return Array.from(years).sort().reverse();
    }, [historyData]);

    // Status options
    const statusOptions: Option[] = [
        { label: t('history.status.open'), value: 'OPEN' },
        { label: t('history.status.sent'), value: 'SENT' },
        { label: t('history.status.paid'), value: 'PAID' },
    ];

    // Filter history based on search, year and status
    const filteredHistory = useMemo(() => {
        return historyData.filter(item => {
            const matchesSearch = (item.security || '').toLowerCase().includes(searchQuery.toLowerCase());
            const matchesYear = selectedYear === 'all' || (item.date && item.date.startsWith(selectedYear));
            const matchesStatus = selectedStatuses.length === 0 || (item.status && selectedStatuses.includes(item.status));
            return matchesSearch && matchesYear && matchesStatus;
        });
    }, [historyData, searchQuery, selectedYear, selectedStatuses]);

    const handleSelectAll = (checked: boolean) => {
        if (checked) {
            setSelectedIds(new Set(filteredHistory.filter(h => h.id).map(h => h.id!)));
        } else {
            setSelectedIds(new Set());
        }
    };

    const handleSelectOne = (id: string, checked: boolean) => {
        const newSelected = new Set(selectedIds);
        if (checked) {
            newSelected.add(id);
        } else {
            newSelected.delete(id);
        }
        setSelectedIds(newSelected);
    };

    const initiateStatusUpdate = (status: 'OPEN' | 'SENT' | 'PAID') => {
        if (selectedIds.size === 0) return;
        setConfirmationData({
            isOpen: true,
            status,
            count: selectedIds.size
        });
    };

    const confirmStatusUpdate = async () => {
        const status = confirmationData.status;
        if (!status || selectedIds.size === 0) return;

        setIsUpdating(true);
        setConfirmationData({ ...confirmationData, isOpen: false }); // Close modal immediately

        updateStatusMutation.mutate({ ids: Array.from(selectedIds), status });
    };

    const getStatusBadge = (status?: DividendCase['status']) => {
        switch (status) {
            case 'OPEN':
                return <span className="px-2 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-700">{t('history.status.open')}</span>;
            case 'SENT':
                return <span className="px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-700">{t('history.status.sent')}</span>;
            case 'PAID':
                return <span className="px-2 py-1 rounded-full text-xs font-medium bg-emerald-100 text-emerald-700">{t('history.status.paid')}</span>;
            default:
                return null;
        }
    };

    const selectedItems = useMemo(() => {
        return historyData.filter(item => item.id && selectedIds.has(item.id));
    }, [historyData, selectedIds]);

    const canMarkSent = useMemo(() => {
        return selectedItems.some(item => item.status === 'OPEN');
    }, [selectedItems]);

    const canMarkPaid = useMemo(() => {
        return selectedItems.some(item => item.status === 'OPEN' || item.status === 'SENT');
    }, [selectedItems]);

    const canMarkOpen = useMemo(() => {
        return selectedItems.some(item => item.status === 'SENT' || item.status === 'PAID');
    }, [selectedItems]);

    return (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-6">
            <div className="flex flex-row justify-between items-center gap-4">
                <h2 className="text-2xl md:text-3xl font-heading font-bold text-foreground">{t('app.history')}</h2>
                <div className="flex gap-2">
                    {selectedIds.size > 0 && (
                        <>
                            <Button
                                variant="secondary"
                                size="sm"
                                onClick={() => initiateStatusUpdate('OPEN')}
                                disabled={isUpdating || !canMarkOpen}
                                className="text-gray-600 border-gray-200 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                <CheckCircle className="w-4 h-4 mr-2" />
                                <span className="hidden sm:inline">{t('history.actions.mark_open')}</span>
                            </Button>
                            <Button
                                variant="secondary"
                                size="sm"
                                onClick={() => initiateStatusUpdate('SENT')}
                                disabled={isUpdating || !canMarkSent}
                                className="text-blue-600 border-blue-200 hover:bg-blue-50 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                <Send className="w-4 h-4 mr-2" />
                                <span className="hidden sm:inline">{t('history.actions.mark_sent')}</span>
                            </Button>
                            <Button
                                variant="secondary"
                                size="sm"
                                onClick={() => initiateStatusUpdate('PAID')}
                                disabled={isUpdating || !canMarkPaid}
                                className="text-emerald-600 border-emerald-200 hover:bg-emerald-50 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                <Wallet className="w-4 h-4 mr-2" />
                                <span className="hidden sm:inline">{t('history.actions.mark_paid')}</span>
                            </Button>
                        </>
                    )}
                </div>
            </div>

            {/* Confirmation Modal */}
            {confirmationData.isOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
                    <Card className="max-w-md w-full p-6 space-y-4 shadow-xl">
                        <h3 className="text-lg font-bold font-heading">{t('history.confirmation.title')}</h3>
                        <p className="text-muted-foreground">
                            {t('history.confirmation.message', {
                                count: confirmationData.count,
                                status: t(`history.status.${confirmationData.status?.toLowerCase()}`)
                            })}
                        </p>
                        <div className="flex justify-end gap-3 pt-2">
                            <Button
                                variant="ghost"
                                onClick={() => setConfirmationData({ ...confirmationData, isOpen: false })}
                            >
                                {t('history.confirmation.cancel')}
                            </Button>
                            <Button
                                variant="primary"
                                onClick={confirmStatusUpdate}
                            >
                                {t('history.confirmation.confirm')}
                            </Button>
                        </div>
                    </Card>
                </div>
            )}

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
                            <div
                                key={item.id}
                                className={`bg-card p-4 rounded-xl border shadow-sm flex flex-col gap-3 transition-colors ${item.id && selectedIds.has(item.id) ? 'border-primary ring-1 ring-primary' : 'border-border'}`}
                                onClick={() => item.id && handleSelectOne(item.id, !selectedIds.has(item.id))}
                            >
                                <div className="flex justify-between items-start">
                                    <div className="flex gap-3">
                                        <div className={`mt-1 w-5 h-5 rounded-full border flex items-center justify-center ${item.id && selectedIds.has(item.id) ? 'bg-primary border-primary text-white' : 'border-muted-foreground'}`}>
                                            {item.id && selectedIds.has(item.id) && <CheckCircle className="w-3 h-3" />}
                                        </div>
                                        <div>
                                            <span className="text-xs text-muted-foreground font-mono">{item.date}</span>
                                            <h4 className="font-bold text-foreground">{item.security}</h4>
                                        </div>
                                    </div>
                                    {getStatusBadge(item.status)}
                                </div>
                                <div className="flex justify-between items-center pt-2 border-t border-border/50">
                                    <div>
                                        <div className="text-xs text-muted-foreground">{t('history.columns.reclaimed')}</div>
                                        <div className="font-bold text-emerald-600">+{item.reclaimedAmount?.toFixed(2) ?? '0.00'} €</div>
                                    </div>
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
                                <th className="px-6 py-4 w-12">
                                    <input
                                        type="checkbox"
                                        className="rounded border-gray-300"
                                        checked={selectedIds.size === filteredHistory.length && filteredHistory.length > 0}
                                        onChange={(e) => handleSelectAll(e.target.checked)}
                                    />
                                </th>
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
                                    <td colSpan={7} className="px-6 py-8 text-center text-muted-foreground">
                                        {t('forms.no_data')}
                                    </td>
                                </tr>
                            ) : (
                                filteredHistory.map((item) => (
                                    <tr
                                        key={item.id}
                                        className={`hover:bg-muted/50 transition-colors ${item.id && selectedIds.has(item.id) ? 'bg-muted/30' : ''}`}
                                    >
                                        <td className="px-6 py-4">
                                            <input
                                                type="checkbox"
                                                className="rounded border-gray-300"
                                                checked={!!item.id && selectedIds.has(item.id)}
                                                onChange={(e) => item.id && handleSelectOne(item.id, e.target.checked)}
                                            />
                                        </td>
                                        <td className="px-6 py-4 text-slate-600 font-mono text-xs">
                                            {item.date ? new Date(item.date).toLocaleDateString(i18n.language, { year: 'numeric', month: '2-digit', day: '2-digit' }) : '-'}
                                        </td>
                                        <td className="px-6 py-4 font-medium text-foreground">{item.security}</td>
                                        <td className="px-6 py-4 text-right text-slate-600">{item.grossAmount?.toFixed(2) ?? '0.00'} €</td>
                                        <td className="px-6 py-4 text-right font-medium text-emerald-600">+{item.reclaimedAmount?.toFixed(2) ?? '0.00'} €</td>
                                        <td className="px-6 py-4 text-center">{getStatusBadge(item.status)}</td>
                                        <td className="px-6 py-4 text-right">
                                            <button
                                                className="text-brand-600 hover:text-brand-800 font-medium inline-flex items-center text-xs"
                                                title={t('history.actions.download_pdf')}
                                            >
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
