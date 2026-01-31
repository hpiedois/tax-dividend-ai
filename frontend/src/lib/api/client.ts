/**
 * API Client for Spring Boot BFF
 *
 * This client will connect to your Spring Boot backend when ready.
 * For now, it uses mock data.
 */

import axios, { AxiosError, type AxiosInstance } from 'axios';

// API base URL (will come from environment variable)
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Create axios instance
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
  withCredentials: true, // Send Cookies (HttpOnly)
});

import { User } from 'oidc-client-ts';

// Request interceptor - inject OIDC Token
apiClient.interceptors.request.use(
  (config) => {
    // Attempt to retrieve token from sessionStorage (default storage for oidc-client-ts)
    // Key pattern: oidc.user:<authority>:<client_id>
    const oidcStorageKey = `oidc.user:http://localhost:8180/realms/tax-dividend:frontend`;
    const oidcStorage = sessionStorage.getItem(oidcStorageKey);

    if (oidcStorage) {
      try {
        const user = User.fromStorageString(oidcStorage);
        if (user && user.access_token) {
          config.headers.Authorization = `Bearer ${user.access_token}`;
        }
      } catch (e) {
        console.warn('Failed to parse OIDC user from storage', e);
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - handle errors
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    // If 401, mostly handled by oidc-client automaticSilentRenew.
    // If explicitly failing here, we might redirect to login via window.location but prefer relying on AuthProvider
    if (error.response?.status === 401) {
      console.error('Unauthorized API Call', error);
      // Optional: Force login if token is definitely invalid?
      // window.location.href = '/'; 
    }
    return Promise.reject(error);
  }
);

// API Error type
export interface APIError {
  message: string;
  status?: number;
  code?: string;
}

// Helper to extract error message
export const getErrorMessage = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    return error.response?.data?.message || error.message || 'An error occurred';
  }
  if (error instanceof Error) {
    return error.message;
  }
  return 'An unknown error occurred';
};
