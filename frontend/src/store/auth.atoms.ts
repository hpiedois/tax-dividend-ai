import { atom } from 'jotai';
import { atomWithStorage } from 'jotai/utils';

// Types
export interface User {
  id: string;
  email: string;
  fullName?: string;
  taxId?: string; // NIF/AVS number
}

// Atoms
export const userAtom = atomWithStorage<User | null>('user', null);
export const isAuthenticatedAtom = atom((get) => get(userAtom) !== null);
export const isLoadingAuthAtom = atom(false);

// Actions
// Actions
export const loginAtom = atom(
  null,
  async (_get, set, { email, password }: { email: string; password: string }) => {
    set(isLoadingAuthAtom, true);

    // Support Mock Mode for frontend-only testing
    const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true';

    try {
      if (USE_MOCK) {
        // Simulate API call
        await new Promise((resolve) => setTimeout(resolve, 1000));

        // Mock user
        const mockUser: User = {
          id: '1',
          email,
          fullName: 'John Doe',
          taxId: '1234567890123',
        };

        set(userAtom, mockUser);
        return { success: true };
      }

      // Real API call
      // Import dynamically to avoid cycle if necessary, or just use the global one
      const { apiClient } = await import('../lib/api/client');

      const response = await apiClient.post<User>('/auth/login', { username: email, password });

      set(userAtom, response.data);
      return { success: true };
    } catch (error) {
      console.error('Login error:', error);
      return { success: false, error: 'Invalid credentials' };
    } finally {
      set(isLoadingAuthAtom, false);
    }
  }
);

export const logoutAtom = atom(null, async (_get, set) => {
  try {
    const { apiClient } = await import('../lib/api/client');
    await apiClient.post('/auth/logout');
  } catch (e) {
    console.error('Logout failed', e);
  } finally {
    set(userAtom, null);
    // Optional: Force reload to clear any memory states/caches
    window.location.href = '/login';
  }
});
