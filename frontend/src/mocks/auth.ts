import { User, UserManager, type UserManagerSettings } from 'oidc-client-ts';

/**
 * A partial mock of UserManager for development/testing without a real OIDC provider.
 */
export class MockUserManager extends UserManager {
    private _user: User | undefined = undefined;

    constructor(settings?: UserManagerSettings) {
        // Pass dummy settings to satisfy the parent constructor
        super(settings || {
            authority: 'http://mock-authority',
            client_id: 'mock-client',
            redirect_uri: 'http://localhost',
        });
    }

    public async signinRedirect(): Promise<void> {
        console.log('[MockAuth] signinRedirect called. Simulating login...');
        const now = Math.floor(Date.now() / 1000);

        // Create a mock user
        this._user = new User({
            id_token: 'mock-id-token',
            session_state: 'mock-session',
            access_token: 'mock-access-token',
            refresh_token: 'mock-refresh-token',
            token_type: 'Bearer',
            scope: 'openid profile email',
            profile: {
                sub: 'mock-user-123',
                name: 'Mock User',
                email: 'mock@example.com',
                preferred_username: 'mockuser',
                email_verified: true,
                iss: 'http://mock-authority',
                aud: 'mock-client',
                exp: now + 3600,
                iat: now,
            },
            expires_at: now + 3600, // 1 hour
        });

        sessionStorage.setItem('mock_user_session', JSON.stringify(this._user.toStorageString()));
        this.events.load(this._user);
        return Promise.resolve();
    }

    // Fix signature to match base class
    public async signinCallback(_args?: any): Promise<User | undefined> {
        console.log('[MockAuth] signinCallback called.');
        const user = await this.getUser();
        return user || undefined;
    }

    public async getUser(): Promise<User | null> { // Base class return type is User | null (wait, let me check base class signature from error)
        // Error said: Type 'MockUserManager | UserManager' is not assignable to type 'UserManager | undefined'.
        // The types returned by 'signinCallback(...)' are incompatible: Promise<User | null> vs Promise<User | undefined>.
        // So signinCallback MUST return User | undefined.
        // But getUser in base class usually returns User | null in oidc-client-ts v3?
        // Let's check error again carefully.
        // "Type 'Promise<User | null>' is not assignable to type 'Promise<User | undefined>'."
        // This confirms signinCallback expects User | undefined.

        // What about getUser?
        // I'll stick to User | undefined for internal use if possible, or cast.
        if (this._user) return this._user;

        const stored = sessionStorage.getItem('mock_user_session');
        if (stored) {
            try {
                this._user = User.fromStorageString(stored);
                return this._user;
            } catch (e) {
                console.error('[MockAuth] Failed to restore user', e);
            }
        }
        return null;
    }

    // override signoutRedirect
    public async signoutRedirect(): Promise<void> {
        console.log('[MockAuth] signoutRedirect called.');
        this._user = undefined;
        sessionStorage.removeItem('mock_user_session');
        this.events.unload();
        return Promise.resolve();
    }

    // @ts-ignore
    public async storeUser(user: User | null): Promise<void> {
        return Promise.resolve();
    }
}
