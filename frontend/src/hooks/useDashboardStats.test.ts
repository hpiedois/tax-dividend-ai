import { renderHook, waitFor } from '@testing-library/react';
import { useDashboardStats } from './useDashboardStats';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { dividendsApi } from '../api/clients';
import { AppWrapper } from '../test/utils';

// Mock the API client
vi.mock('../api/clients', () => ({
    dividendsApi: {
        getDividendStats: vi.fn(),
        getDividendHistory: vi.fn()
    }
}));

// Mock useAuth to simulate logged in user
const mockUseAuth = vi.fn();
vi.mock('./useAuth', () => ({
    useAuth: () => mockUseAuth()
}));

const wrapper = AppWrapper;

describe('useDashboardStats', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        mockUseAuth.mockReturnValue({ user: { id: 'user-1' } });
    });

    it('should fetch stats and history when user is authenticated', async () => {
        // Setup mocks
        const mockStats = { totalReclaimed: 100 };
        const mockHistory = { data: [{ id: 'case-1', securityName: 'Test Corp' }] };

        (dividendsApi.getDividendStats as any).mockResolvedValue({ data: mockStats });
        (dividendsApi.getDividendHistory as any).mockResolvedValue({ data: mockHistory });

        const { result } = renderHook(() => useDashboardStats(), { wrapper });

        await waitFor(() => expect(result.current.isSuccess).toBe(true));

        expect(result.current.data?.stats).toEqual(mockStats);
        expect(result.current.data?.recentActivity).toEqual(mockHistory.data);
    });

    it('should not fetch if user is not authenticated', () => {
        mockUseAuth.mockReturnValue({ user: null });

        const { result } = renderHook(() => useDashboardStats(), { wrapper });

        expect(result.current.isLoading).toBe(false); // React Query status stays idle when disabled
        // Specifically check call counts
        expect(dividendsApi.getDividendStats).not.toHaveBeenCalled();
    });
});
