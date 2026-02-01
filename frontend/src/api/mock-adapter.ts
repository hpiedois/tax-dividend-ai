import type { AxiosInstance } from 'axios';
import { mockState } from './mock-state';

// Simple mock adapter implementation
// We could use 'axios-mock-adapter' library but a custom interceptor is lighter for this purpose

export function applyMockAdapter(axiosInstance: AxiosInstance) {
    console.log('[MockAdapter] Initializing mock API layer...');

    // Function to simulate network delay
    const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

    axiosInstance.interceptors.request.use(async (config) => {
        console.log(`[MockAdapter] Request: ${config.method?.toUpperCase()} ${config.url}`);

        await delay(600); // Simulate 600ms latency

        // --- DASHBOARD STATS ---
        if (config.url?.includes('/dividends/stats') && config.method === 'get') {
            const scenario = mockState.activeScenario;
            return Promise.reject({
                response: {
                    data: scenario.stats, // Matches DividendStats structure
                    status: 200,
                    statusText: 'OK',
                    headers: {},
                    config,
                },
                isMock: true
            });
        }

        // --- RECENT HISTORY (Used for Dashboard Activity) ---
        if (config.url?.includes('/dividends/history') && config.method === 'get') {
            const scenario = mockState.activeScenario;
            // Map scenario activity to DividendCase structure
            const mockHistory = {
                data: scenario.recentActivity.map(act => ({
                    id: act.id,
                    securityName: act.name,
                    grossAmount: act.details?.grossAmount || 0,
                    reclaimedAmount: act.details?.reclaimableAmount || 0,
                    status: 'OPEN',
                    date: new Date().toISOString().split('T')[0],
                    currency: act.details?.currency || 'EUR'
                })),
                total: scenario.recentActivity.length,
                page: 0,
                pageSize: 10
            };

            return Promise.reject({
                response: {
                    data: mockHistory,
                    status: 200,
                    statusText: 'OK',
                    headers: {},
                    config,
                },
                isMock: true
            });
        }

        // --- SCAN / PARSE ---
        if (config.url?.includes('/dividends/parse-statement') && config.method === 'post') {
            // Mock parsing result
            const mockDividends = [
                {
                    securityName: "AIR LIQUIDE SA",
                    isin: "FR0000120073",
                    grossAmount: 1500.00,
                    currency: "EUR",
                    paymentDate: "2023-05-15",
                    withholdingTax: 450.00,
                    reclaimableAmount: 225.00,
                    sourceCountry: "FR"
                }
            ];

            return Promise.reject({
                response: {
                    data: {
                        dividends: mockDividends,
                        metadata: {
                            broker: "Boursorama",
                            periodStart: "2023-01-01",
                            periodEnd: "2023-12-31"
                        }
                    },
                    status: 200,
                    statusText: 'OK',
                    headers: {},
                    config,
                },
                isMock: true
            });
        }

        // --- AUTH (Login) ---
        if (config.url?.includes('/auth/login') && config.method === 'post') {
            const { username } = JSON.parse(config.data || '{}');
            return Promise.reject({
                response: {
                    data: {
                        id: '1',
                        email: username || 'test@example.com',
                        fullName: 'John Doe',
                        taxId: '1234567890123',
                    },
                    status: 200,
                    statusText: 'OK',
                    headers: {},
                    config,
                },
                isMock: true
            });
        }

        // --- AUTH (Logout) ---
        if (config.url?.includes('/auth/logout') && config.method === 'post') {
            return Promise.reject({
                response: {
                    data: {},
                    status: 200,
                    statusText: 'OK',
                    headers: {},
                    config,
                },
                isMock: true
            });
        }

        return config;
    });

    // Response interceptor to catch the "mock errors" and return them as success
    axiosInstance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.isMock && error.response) {
                console.log(`[MockAdapter] Returning mock response for ${error.response.config.url}`);
                return Promise.resolve(error.response);
            }
            return Promise.reject(error);
        }
    );
}
