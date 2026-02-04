import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { ArrowUpRight, Clock, CheckCircle, ArrowRight, ChevronDown, ChevronUp } from 'lucide-react';
import { useDashboardStats } from '../../hooks/useDashboardStats';

export function DashboardView() {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [expandedActivityId, setExpandedActivityId] = useState<string | null>(null);

    // Use the hook instead of hardcoded scenarios
    const { data: dashboardData, isLoading } = useDashboardStats();

    const toggleActivity = (id: string) => {
        setExpandedActivityId(expandedActivityId === id ? null : id);
    };

    // Fallback while loading or if error (could optionally show error state)
    // Note: In a real app we'd have a Skeleton loader here
    if (isLoading) {
        return <div className="p-8 text-center">{t('dashboard.loading')}</div>;
    }

    const data = dashboardData;


    const container = {
        hidden: { opacity: 0 },
        show: {
            opacity: 1,
            transition: {
                staggerChildren: 0.1
            }
        }
    };

    const item = {
        hidden: { opacity: 0, y: 20 },
        show: { opacity: 1, y: 0 }
    };

    return (
        <>
            <motion.div variants={container} initial="hidden" animate="show" className="space-y-8 pb-16">
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                    <h2 className="text-3xl font-heading font-bold text-foreground">{t('app.dashboard')}</h2>
                    <div className="flex items-center gap-3 w-full sm:w-auto">
                        <Button
                            variant="ghost"
                            size="md"
                            onClick={() => navigate('/forms')}
                            className="flex-1 sm:flex-none justify-center font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-100 whitespace-nowrap"
                        >
                            <span className="hidden md:inline">{t('result.generate_forms')}</span>
                            <span className="md:hidden">Forms</span>
                            <ArrowRight className="w-4 h-4 ml-2" />
                        </Button>
                        <Button
                            onClick={() => navigate('/scan')}
                            size="md"
                            className="shadow-brand-500/20 flex-1 sm:flex-none justify-center"
                        >
                            + {t('result.new_scan')}
                        </Button>
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <motion.div variants={item}>
                        <Card className="p-6 bg-gradient-to-br from-brand-600 to-brand-800 text-white border-0 shadow-xl shadow-brand-900/20">
                            <div className="flex justify-between items-start mb-4">
                                <div className="p-2 bg-white/10 rounded-lg">
                                    <ArrowUpRight className="w-6 h-6 text-white" />
                                </div>
                            </div>
                            <p className="text-brand-100 font-medium mb-1">{t('dashboard.total_reclaimed')}</p>
                            <h3 className="text-3xl font-bold font-heading">{data?.stats?.totalReclaimed?.toFixed(2) ?? '0.00'} €</h3>
                        </Card>
                    </motion.div>

                    <motion.div variants={item}>
                        <Card className="p-6">
                            <div className="flex justify-between items-start mb-4">
                                <div className="p-2 bg-orange-100 rounded-lg">
                                    <Clock className="w-6 h-6 text-orange-600" />
                                </div>
                            </div>
                            <p className="text-slate-500 font-medium mb-1">{t('dashboard.pending')}</p>
                            <h3 className="text-3xl font-bold font-heading text-foreground">{data?.stats?.pendingAmount?.toFixed(2) ?? '0.00'} €</h3>
                        </Card>
                    </motion.div>

                    <motion.div variants={item}>
                        <Card className="p-6">
                            <div className="flex justify-between items-start mb-4">
                                <div className="p-2 bg-emerald-100 rounded-lg">
                                    <CheckCircle className="w-6 h-6 text-emerald-600" />
                                </div>
                            </div>
                            <p className="text-slate-500 font-medium mb-1">{t('dashboard.cases_processed')}</p>
                            <h3 className="text-3xl font-bold font-heading text-foreground">{data?.stats?.casesCount ?? 0}</h3>
                        </Card>
                    </motion.div>
                </div>

                <motion.div variants={item}>
                    <h3 className="text-xl font-heading font-bold text-foreground mb-4">{t('dashboard.recent_activity')}</h3>
                    <Card className="p-0 overflow-hidden">
                        {data?.recentActivity && data.recentActivity.length > 0 ? (
                            data.recentActivity.map((activity: any) => (
                                <div key={activity.id} className="border-b border-border last:border-0">
                                    <div
                                        className="p-4 flex items-center justify-between hover:bg-muted/50 transition-colors cursor-pointer"
                                        onClick={() => toggleActivity(activity.id)}
                                    >
                                        <div className="flex items-center gap-4">
                                            <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold bg-slate-50 text-slate-600`}>
                                                {/* Fallback initials if security name is missing */}
                                                {(activity.securityName || '??').substring(0, 2).toUpperCase()}
                                            </div>
                                            <div>
                                                <p className="font-medium text-foreground">{activity.securityName}</p>
                                                <p className="text-sm text-slate-500">
                                                    {activity.grossAmount ? `${activity.grossAmount} ${activity.currency || 'EUR'}` : 'N/A'}
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
                                                            {activity.grossAmount?.toFixed(2)} {activity.currency}
                                                        </p>
                                                    </div>
                                                    <div>
                                                        <p className="text-slate-500 mb-1">{t('result.reclaimable_est')}</p>
                                                        <p className="font-medium text-emerald-600">+{activity.reclaimedAmount?.toFixed(2)} {activity.currency}</p>
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
            </motion.div>



        </>
    );
}
