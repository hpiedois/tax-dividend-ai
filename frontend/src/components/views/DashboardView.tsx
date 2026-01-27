import { useState } from 'react';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { ArrowUpRight, Clock, CheckCircle, FileText } from 'lucide-react';
import { MockSelector } from '../debug/MockSelector';
import { SCENARIOS } from '../../lib/mock-scenarios';

export function DashboardView() {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [currentScenarioId, setCurrentScenarioId] = useState('few');

    const data = SCENARIOS[currentScenarioId] || SCENARIOS.few;

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
                            size="lg"
                            onClick={() => navigate('/forms')}
                            className="flex-1 sm:flex-none justify-center font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-100"
                        >
                            <FileText className="w-4 h-4 mr-2" />
                            {t('result.generate_forms')}
                        </Button>
                        <Button
                            onClick={() => navigate('/scan')}
                            size="lg"
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
                            <h3 className="text-3xl font-bold font-heading">{data.stats.totalReclaimed.toFixed(2)} €</h3>
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
                            <h3 className="text-3xl font-bold font-heading text-foreground">{data.stats.pendingAmount.toFixed(2)} €</h3>
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
                            <h3 className="text-3xl font-bold font-heading text-foreground">{data.stats.casesCount}</h3>
                        </Card>
                    </motion.div>
                </div>

                <motion.div variants={item}>
                    <h3 className="text-xl font-heading font-bold text-foreground mb-4">{t('dashboard.recent_activity')}</h3>
                    <Card className="p-0 overflow-hidden">
                        {data.recentActivity.length > 0 ? (
                            data.recentActivity.map((activity) => (
                                <div key={activity.id} className="p-4 border-b border-border flex items-center justify-between hover:bg-muted/50 transition-colors last:border-0">
                                    <div className="flex items-center gap-4">
                                        <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold ${activity.type === 'scan' ? 'bg-brand-50 text-brand-600' :
                                                activity.type === 'refund' ? 'bg-emerald-50 text-emerald-600' :
                                                    'bg-slate-50 text-slate-600'
                                            }`}>
                                            {activity.initials}
                                        </div>
                                        <div>
                                            <p className="font-medium text-foreground">{activity.name}</p>
                                            <p className="text-sm text-slate-500">{activity.description}</p>
                                        </div>
                                    </div>
                                    <span className="text-sm text-slate-400">{activity.time}</span>
                                </div>
                            ))
                        ) : (
                            <div className="p-12 text-center">
                                <p className="text-slate-400">No recent activity</p>
                            </div>
                        )}
                    </Card>
                </motion.div>
            </motion.div>

            <MockSelector
                currentScenario={currentScenarioId}
                onSelect={setCurrentScenarioId}
            />
        </>
    );
}
