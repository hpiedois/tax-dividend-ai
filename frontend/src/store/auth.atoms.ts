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

    try {
      // Use central API instance (mock adapter will intercept if enabled)
      const { api } = await import('../api/index');

      const response = await api.post<User>('/auth/login', { username: email, password });

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
    const { api } = await import('../api/index');
    await api.post('/auth/logout');
  } catch (e) {
    console.error('Logout failed', e);
  } finally {
    set(userAtom, null);
    // Optional: Force reload to clear any memory states/caches
    window.location.href = '/login';
  }
});
