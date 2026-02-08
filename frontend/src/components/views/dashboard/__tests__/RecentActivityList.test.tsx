import { describe, it, expect } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { render } from '../../../../test/utils';
import { RecentActivityList } from '../RecentActivityList';
import type { DividendCase } from '../../../../api/generated';

describe('RecentActivityList', () => {
    const mockActivities: DividendCase[] = [
        {
            id: '1',
            security: 'Apple Inc.',
            grossAmount: 100,
            reclaimedAmount: 15,
            date: '2023-01-01',
            status: 'PAID'
        },
        {
            id: '2',
            security: 'Microsoft',
            grossAmount: 200,
            reclaimedAmount: 30,
            date: '2023-02-01',
            status: 'OPEN'
        }
    ];

    it('should render empty state when no activities', () => {
        render(<RecentActivityList activities={[]} />);
        expect(screen.getByText(/no recent activity/i)).toBeInTheDocument();
    });

    it('should render list of activities', () => {
        render(<RecentActivityList activities={mockActivities} />);

        expect(screen.getByText('Apple Inc.')).toBeInTheDocument();
        expect(screen.getByText('Microsoft')).toBeInTheDocument();
        expect(screen.getByText('100 EUR')).toBeInTheDocument();
        expect(screen.getByText('2023-01-01')).toBeInTheDocument();
    });

    it('should expand activity details on click', () => {
        render(<RecentActivityList activities={mockActivities} />);

        const row = screen.getByText('Apple Inc.').closest('.cursor-pointer');
        expect(row).toBeInTheDocument();

        // Click to expand
        if (row) fireEvent.click(row);

        // Check for expanded details
        // "Reclaimable Est." should be visible
        // Match "Est. Reclaimable" which corresponds to 'result.reclaimable_est'
        expect(screen.getByText(/Est. Reclaimable/i)).toBeInTheDocument();
        expect(screen.getByText('+15.00 EUR')).toBeInTheDocument();
    });

    it('should handle missing security name gracefully', () => {
        const incompleteActivity: DividendCase[] = [{
            id: '3',
            grossAmount: 50,
            date: '2023-03-01',
            status: 'OPEN'
        }];

        render(<RecentActivityList activities={incompleteActivity} />);

        // Should fallback to '??' or similar logic
        expect(screen.getByText('??')).toBeInTheDocument();
    });
});
