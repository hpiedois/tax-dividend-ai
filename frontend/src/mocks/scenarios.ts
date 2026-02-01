export interface ActivityItem {
    id: string;
    initials: string;
    name: string;
    description: string;
    time: string;
    type: 'scan' | 'refund' | 'info';
    details?: {
        grossAmount: number;
        currency: string;
        withholdingTax: number;
        reclaimableAmount: number;
        paymentDate?: string;
        isin?: string;
    };
}

export interface DashboardStats {
    totalReclaimed: number;
    pendingAmount: number;
    casesCount: number;
}

export interface DashboardScenario {
    id: 'empty' | 'few' | 'many';
    label: string;
    stats: DashboardStats;
    recentActivity: ActivityItem[];
}

export const SCENARIOS: Record<string, DashboardScenario> = {
    empty: {
        id: 'empty',
        label: 'Empty State',
        stats: {
            totalReclaimed: 0,
            pendingAmount: 0,
            casesCount: 0
        },
        recentActivity: []
    },
    few: {
        id: 'few',
        label: 'Few Items (Default)',
        stats: {
            totalReclaimed: 231.00,
            pendingAmount: 548.70,
            casesCount: 4
        },
        recentActivity: [
            {
                id: '1',
                initials: 'AL',
                name: 'AIR LIQUIDE SA',
                description: 'Scan Imported',
                time: 'Auj. 10:23',
                type: 'scan',
                details: {
                    grossAmount: 1500.00,
                    currency: 'EUR',
                    withholdingTax: 450.00,
                    reclaimableAmount: 225.00,
                    paymentDate: '2023-05-15',
                    isin: 'FR0000120073'
                }
            },
            {
                id: '2',
                initials: 'TO',
                name: 'TOTALENERGIES SE',
                description: 'Refund Received',
                time: 'Hier',
                type: 'refund',
                details: {
                    grossAmount: 850.50,
                    currency: 'EUR',
                    withholdingTax: 255.15,
                    reclaimableAmount: 127.57,
                    paymentDate: '2023-04-03',
                    isin: 'FR0000120271'
                }
            }
        ]
    },
    many: {
        id: 'many',
        label: 'Many Items',
        stats: {
            totalReclaimed: 12450.50,
            pendingAmount: 3200.00,
            casesCount: 42
        },
        recentActivity: [
            { id: '1', initials: 'AL', name: 'AIR LIQUIDE SA', description: 'Scan Imported', time: 'Auj. 10:23', type: 'scan' },
            { id: '2', initials: 'TO', name: 'TOTALENERGIES SE', description: 'Refund Received', time: 'Hier', type: 'refund' },
            { id: '3', initials: 'LV', name: 'LVMH', description: 'Form Generated', time: 'Hier', type: 'info' },
            { id: '4', initials: 'AX', name: 'AXA SA', description: 'Submission Pending', time: '25 Jan', type: 'info' },
            { id: '5', initials: 'OR', name: 'L\'OREAL', description: 'Scan Imported', time: '24 Jan', type: 'scan' },
            { id: '6', initials: 'SN', name: 'SANOFI', description: 'Refund Received', time: '20 Jan', type: 'refund' },
            { id: '7', initials: 'BN', name: 'BNP PARIBAS', description: 'Scan Imported', time: '18 Jan', type: 'scan' },
            { id: '8', initials: 'DA', name: 'DANONE', description: 'Form Generated', time: '15 Jan', type: 'info' }
        ]
    }
};
