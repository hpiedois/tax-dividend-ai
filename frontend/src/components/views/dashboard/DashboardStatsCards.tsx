import { Card } from '../../ui/Card';
import { ArrowUpRight, Clock, CheckCircle } from 'lucide-react';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';

// Define props interface matching the data structure
interface DashboardStatsCardsProps {
    stats: {
        totalReclaimed?: number;
        pendingAmount?: number;
        casesCount?: number;
    }
}

export function DashboardStatsCards({ stats }: DashboardStatsCardsProps) {
    const { t } = useTranslation();

    const item = {
        hidden: { opacity: 0, y: 20 },
        show: { opacity: 1, y: 0 }
    };

    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <motion.div variants={item}>
                <Card className="p-6 bg-gradient-to-br from-brand-600 to-brand-800 text-white border-0 shadow-xl shadow-brand-900/20">
                    <div className="flex justify-between items-start mb-4">
                        <div className="p-2 bg-white/10 rounded-lg">
                            <ArrowUpRight className="w-6 h-6 text-white" />
                        </div>
                    </div>
                    <p className="text-brand-100 font-medium mb-1">{t('dashboard.total_reclaimed')}</p>
                    <h3 className="text-3xl font-bold font-heading">{stats?.totalReclaimed?.toFixed(2) ?? '0.00'} €</h3>
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
                    <h3 className="text-3xl font-bold font-heading text-foreground">{stats?.pendingAmount?.toFixed(2) ?? '0.00'} €</h3>
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
                    <h3 className="text-3xl font-bold font-heading text-foreground">{stats?.casesCount ?? 0}</h3>
                </Card>
            </motion.div>
        </div>
    );
}
