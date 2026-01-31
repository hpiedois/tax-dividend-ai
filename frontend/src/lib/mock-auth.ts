```typescript
import { User, UserManager, UserManagerSettings } from 'oidc-client-ts';

/**
 * A partial mock of UserManager for development/testing without a real OIDC provider.
 */
export class MockUserManager extends UserManager {
    private _user: User | null = null;

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
        return Promise.resolve();
    }

    // Fix signature to match base class: (args?: any) => Promise<User | undefined>
    // Note: oidc-client-ts definition might vary slightly by version, 
    // but typically signinCallback takes args.
    public async signinCallback(args?: any): Promise<User | null> {
        console.log('[MockAuth] signinCallback called.');
        return this.getUser();
    }

    public async getUser(): Promise<User | null> {
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

    public async signoutRedirect(): Promise<void> {
        console.log('[MockAuth] signoutRedirect called.');
        this._user = null;
        sessionStorage.removeItem('mock_user_session');
        return Promise.resolve();
    }
    
    // override other methods as needed to prevent errors
    // @ts-ignore
    public async storeUser(user: User | null): Promise<void> {
        return Promise.resolve();
    }
}
```
