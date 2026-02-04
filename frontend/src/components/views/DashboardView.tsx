import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { Button } from '../ui/Button';
import { ArrowRight } from 'lucide-react';
import { useDashboardStats } from '../../hooks/useDashboardStats';
import { DashboardStatsCards } from './dashboard/DashboardStatsCards';
import { RecentActivityList } from './dashboard/RecentActivityList';

export function DashboardView() {
    const navigate = useNavigate();
    const { t } = useTranslation();

    // Use the hook instead of hardcoded scenarios
    const { data: dashboardData, isLoading } = useDashboardStats();

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

    return (
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

            <DashboardStatsCards stats={data?.stats || {}} />

            <RecentActivityList activities={data?.recentActivity || []} />

        </motion.div>
    );
}
