import { useState } from 'react';
import { Card } from '../../ui/Card';
import { ChevronDown, ChevronUp } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import type { DividendCase } from '../../../api/generated';

interface RecentActivityListProps {
    activities: DividendCase[];
}

export function RecentActivityList({ activities }: RecentActivityListProps) {
    const { t } = useTranslation();
    const [expandedActivityId, setExpandedActivityId] = useState<string | null>(null);

    const toggleActivity = (id: string) => {
        setExpandedActivityId(expandedActivityId === id ? null : id);
    };

    const item = {
        hidden: { opacity: 0, y: 20 },
        show: { opacity: 1, y: 0 }
    };

    return (
        <motion.div variants={item}>
            <h3 className="text-xl font-heading font-bold text-foreground mb-4">{t('dashboard.recent_activity')}</h3>
            <Card className="p-0 overflow-hidden">
                {activities && activities.length > 0 ? (
                    activities.map((activity) => (
                        <div key={activity.id} className="border-b border-border last:border-0">
                            <div
                                className="p-4 flex items-center justify-between hover:bg-muted/50 transition-colors cursor-pointer"
                                onClick={() => activity.id && toggleActivity(activity.id)}
                            >
                                <div className="flex items-center gap-4">
                                    <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold bg-slate-50 text-slate-600`}>
                                        {/* Fallback initials if security name is missing */}
                                        {(activity.security || '??').substring(0, 2).toUpperCase()}
                                    </div>
                                    <div>
                                        <p className="font-medium text-foreground">{activity.security}</p>
                                        <p className="text-sm text-slate-500">
                                            {activity.grossAmount ? `${activity.grossAmount} EUR` : 'N/A'}
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-4">
                                    <span className="text-sm text-slate-400">{activity.date}</span>
                                    {expandedActivityId === activity.id ?
                                        <ChevronUp className="w-4 h-4 text-slate-400" /> :
                                        <ChevronDown className="w-4 h-4 text-slate-400" />
                                    }
                                </div>
                            </div>
                            <AnimatePresence>
                                {expandedActivityId === activity.id && (
                                    <motion.div
                                        initial={{ height: 0, opacity: 0 }}
                                        animate={{ height: 'auto', opacity: 1 }}
                                        exit={{ height: 0, opacity: 0 }}
                                        transition={{ duration: 0.2 }}
                                        className="overflow-hidden bg-muted/30"
                                    >
                                        <div className="p-4 pt-0 pl-[4.5rem] grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                                            <div>
                                                <p className="text-slate-500 mb-1">{t('result.gross_amount')}</p>
                                                <p className="font-medium text-foreground">
                                                    {activity.grossAmount?.toFixed(2)} EUR
                                                </p>
                                            </div>
                                            <div>
                                                <p className="text-slate-500 mb-1">{t('result.reclaimable_est')}</p>
                                                <p className="font-medium text-emerald-600">+{activity.reclaimedAmount?.toFixed(2)} EUR</p>
                                            </div>
                                            <div>
                                                <p className="text-slate-500 mb-1">{t('dashboard.status')}</p>
                                                <p className="font-medium text-foreground">{activity.status}</p>
                                            </div>
                                        </div>
                                    </motion.div>
                                )}
                            </AnimatePresence>
                        </div>
                    ))
                ) : (
                    <div className="p-12 text-center">
                        <p className="text-slate-400">{t('dashboard.no_activity')}</p>
                    </div>
                )}
            </Card>
        </motion.div>
    );
}
