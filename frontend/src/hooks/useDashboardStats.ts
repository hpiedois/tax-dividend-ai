import { useQuery } from '@tanstack/react-query';
import { dividendsApi } from '../api/clients';
import { useAuth } from '../hooks/useAuth';

export const useDashboardStats = (taxYear: number = new Date().getFullYear()) => {
    const { user } = useAuth();

    return useQuery({
        queryKey: ['dashboard-stats', user?.id, taxYear],
        queryFn: async () => {
            if (!user?.id) throw new Error("User not authenticated");

            // Fetch Stats
            const statsResponse = await dividendsApi.getDividendStats(taxYear);

            // Fetch Recent Activity (first page of history)
            // Note: History API returns { content: DividendCase[] }
            const historyResponse = await dividendsApi.getDividendHistory(0, 5); // Fetch top 5 recent

            return {
                stats: statsResponse.data,
                recentActivity: historyResponse.data.data || [] // Adjusted to match generated response structure
            };
        },
        enabled: !!user?.id
    });
};
