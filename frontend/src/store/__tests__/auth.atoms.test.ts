import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { createStore } from 'jotai';
import {
  userAtom,
  isAuthenticatedAtom,
  isLoadingAuthAtom,
  loginAtom,
  logoutAtom,
} from '../auth.atoms';

// Mock the API client
const mockPost = vi.fn();
vi.mock('../../lib/api/client', () => ({
  apiClient: {
    post: (...args: any[]) => mockPost(...args),
  },
}));

describe('auth.atoms', () => {
  const originalLocation = window.location;

  beforeEach(() => {
    mockPost.mockReset();
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: { href: '' },
    });
  });

  afterEach(() => {
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: originalLocation,
    });
  });

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
    const mockUser = { id: '1', email: 'test@example.com', fullName: 'Test User' };

    // Setup mock response
    mockPost.mockResolvedValue({ data: mockUser });

    await store.set(loginAtom, { email: 'test@example.com', password: 'password123' });

    const user = store.get(userAtom);
    expect(user).not.toBeNull();
    expect(user?.email).toBe('test@example.com');
  });

  it('should clear user on logout', async () => {
    const store = createStore();

    // First login (manually setting state)
    store.set(userAtom, {
      id: '1',
      email: 'test@example.com',
    });

    // Mock logout success
    mockPost.mockResolvedValue({});

    // Then logout
    await store.set(logoutAtom);

    const user = store.get(userAtom);
    expect(user).toBeNull();
    expect(window.location.href).toBe('/login');
  });

  it('should return success=true on login', async () => {
    const store = createStore();
    const mockUser = { id: '1', email: 'test@example.com', fullName: 'Test User' };
    mockPost.mockResolvedValue({ data: mockUser });

    const result = await store.set(loginAtom, {
      email: 'test@example.com',
      password: 'password123',
    });

    expect(result).toEqual({ success: true });
  });
});
