/**
 * Dividends API
 *
 * Endpoints for dividend processing (will connect to Spring Boot BFF)
 */

import { apiClient } from './client';
import type { DividendData } from '../../types/dividend.types';
import type { MockCase } from '../mock-db';

export interface ParsePDFResponse {
  data: DividendData;
}

export interface DividendHistoryResponse {
  data: MockCase[];
  total: number;
  page: number;
  pageSize: number;
}

/**
 * Parse a PDF dividend statement
 * Future endpoint: POST /api/dividends/parse
 */
export const parsePDF = async (file: File): Promise<DividendData> => {
  const formData = new FormData();
  formData.append('file', file);

  const { data } = await apiClient.post<ParsePDFResponse>(
    '/dividends/parse',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }
  );

  return data.data;
};

/**
 * Get dividend history
 * Future endpoint: GET /api/dividends/history
 */
export const getDividendHistory = async (
  page: number = 0,
  pageSize: number = 10
): Promise<DividendHistoryResponse> => {
  const { data } = await apiClient.get<DividendHistoryResponse>(
    '/dividends/history',
    {
      params: { page, pageSize },
    }
  );

  return data;
};

/**
 * Get dividend statistics
 * Future endpoint: GET /api/dividends/stats
 */
export const getDividendStats = async (): Promise<{
  totalReclaimed: number;
  pendingAmount: number;
  casesCount: number;
}> => {
  const { data } = await apiClient.get('/dividends/stats');
  return data;
};

/**
 * Generate tax forms (5000/5001)
 * Future endpoint: POST /api/dividends/generate-forms
 */
export const generateTaxForms = async (
  dividendIds: string[]
): Promise<{ formUrl: string }> => {
  const { data } = await apiClient.post('/forms/generate', {
    dividendIds,
  });
  return data;
};
