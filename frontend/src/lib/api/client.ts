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

// Response interceptor - handle errors and refresh token
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config;

    // Handle 401 Unauthorized
    if (error.response?.status === 401 && originalRequest) {
      // Prevent infinite loops
      // @ts-expect-error - _retry is a custom property we add dynamically
      if (originalRequest._retry) {
        window.location.href = '/login';
        return Promise.reject(error);
      }

      // @ts-expect-error - Adding custom property to track retry attempts
      originalRequest._retry = true;

      try {
        // Attempt to refresh token
        // Use a separate axios instance or fetch to avoid interceptor loop if this fails contextually 
        // (though 401 here means access token expired, refresh endpoint should work directly with refresh cookie)
        await axios.post(`${API_BASE_URL.replace('/api', '')}/auth/refresh`, {}, { withCredentials: true });

        // Retry original request
        return apiClient(originalRequest);
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    // Handle 403 Forbidden
    if (error.response?.status === 403) {
      console.error('Access forbidden:', error.response.data);
    }

    // Handle network errors
    if (!error.response) {
      console.error('Network error:', error.message);
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
