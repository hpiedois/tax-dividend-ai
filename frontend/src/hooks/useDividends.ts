/**
 * React Query hooks for dividends
 *
 * These hooks will connect to your Spring Boot BFF when ready.
 * For now, they use mock data.
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { showSuccess, showError } from '../lib/toast-helpers';
import { parseDividendPDF } from '../mocks/parser';
import { MOCK_HISTORY, MOCK_STATS } from '../mocks/db';

/**
 * Hook to parse PDF files
 * Will use real API when Spring Boot BFF is ready
 */
export const useParsePDF = () => {
  const { t } = useTranslation();

  return useMutation({
    mutationFn: async (file: File) => {
      // For now, use mock parser
      // TODO: Replace with parsePDF(file) when BFF is ready
      return await parseDividendPDF(file);
    },
    onSuccess: () => {
      showSuccess(t('toast.files_validated', { count: 1 }));
    },
    onError: (error) => {
      console.error('Parse error:', error);
      showError(t('validation.error.generic'));
    },
  });
};

/**
 * Hook to get dividend history
 */
export const useDividendHistory = (page: number = 0, pageSize: number = 10) => {
  return useQuery({
    queryKey: ['dividends', 'history', page, pageSize],
    queryFn: async () => {
      // For now, return mock data
      // TODO: Replace with getDividendHistory(page, pageSize) when BFF is ready
      return {
        data: MOCK_HISTORY,
        total: MOCK_HISTORY.length,
        page,
        pageSize,
      };
    },
  });
};

/**
 * Hook to get dividend statistics
 */
export const useDividendStats = () => {
  return useQuery({
    queryKey: ['dividends', 'stats'],
    queryFn: async () => {
      // For now, return mock data
      // TODO: Replace with getDividendStats() when BFF is ready
      return MOCK_STATS;
    },
  });
};

/**
 * Hook to invalidate dividend queries (useful after mutations)
 */
export const useInvalidateDividends = () => {
  const queryClient = useQueryClient();

  return () => {
    queryClient.invalidateQueries({ queryKey: ['dividends'] });
  };
};
