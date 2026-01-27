export interface MockCase {
    id: string;
    date: string;
    security: string;
    grossAmount: number;
    reclaimedAmount: number;
    status: 'pending' | 'submitted' | 'refunded';
}

export const MOCK_HISTORY: MockCase[] = [
    { id: '1', date: '2026-05-20', security: 'SANOFI', grossAmount: 1500, reclaimedAmount: 177.00, status: 'pending' },
    { id: '2', date: '2025-06-12', security: 'L\'OREAL', grossAmount: 2100, reclaimedAmount: 247.80, status: 'submitted' },
    { id: '3', date: '2024-05-15', security: 'AIR LIQUIDE SA', grossAmount: 1250, reclaimedAmount: 147.50, status: 'pending' },
    { id: '4', date: '2024-04-22', security: 'LVMH MOET HENNESSY', grossAmount: 3400, reclaimedAmount: 401.20, status: 'submitted' },
    { id: '5', date: '2024-03-10', security: 'TOTALENERGIES SE', grossAmount: 850, reclaimedAmount: 100.30, status: 'refunded' },
    { id: '6', date: '2023-11-05', security: 'AXA SA', grossAmount: 1100, reclaimedAmount: 129.80, status: 'refunded' },
];

export const MOCK_STATS = {
    totalReclaimed: 231.00, // Refunded amount only
    pendingAmount: 548.70,
    casesCount: 4,
};
