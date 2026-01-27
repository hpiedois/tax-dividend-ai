import { describe, it, expect } from 'vitest';
import { createStore } from 'jotai';
import {
  userAtom,
  isAuthenticatedAtom,
  isLoadingAuthAtom,
  loginAtom,
  logoutAtom,
} from '../auth.atoms';

describe('auth.atoms', () => {
  it('should start with no user', () => {
    const store = createStore();
    const user = store.get(userAtom);

    expect(user).toBeNull();
  });

  it('should not be authenticated when user is null', () => {
    const store = createStore();
    const isAuthenticated = store.get(isAuthenticatedAtom);

    expect(isAuthenticated).toBe(false);
  });

  it('should be authenticated when user is set', () => {
    const store = createStore();
    store.set(userAtom, {
      id: '1',
      email: 'test@example.com',
      fullName: 'Test User',
    });

    const isAuthenticated = store.get(isAuthenticatedAtom);
    expect(isAuthenticated).toBe(true);
  });

  it('should not be loading by default', () => {
    const store = createStore();
    const isLoading = store.get(isLoadingAuthAtom);

    expect(isLoading).toBe(false);
  });

  it('should set user on successful login', async () => {
    const store = createStore();

    await store.set(loginAtom, { email: 'test@example.com', password: 'password123' });

    const user = store.get(userAtom);
    expect(user).not.toBeNull();
    expect(user?.email).toBe('test@example.com');
  });

  it('should clear user on logout', () => {
    const store = createStore();

    // First login
    store.set(userAtom, {
      id: '1',
      email: 'test@example.com',
    });

    // Then logout
    store.set(logoutAtom);

    const user = store.get(userAtom);
    expect(user).toBeNull();
  });

  it('should return success=true on login', async () => {
    const store = createStore();

    const result = await store.set(loginAtom, {
      email: 'test@example.com',
      password: 'password123',
    });

    expect(result).toEqual({ success: true });
  });
});
