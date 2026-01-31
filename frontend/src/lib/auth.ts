import { UserManager, WebStorageStateStore } from 'oidc-client-ts';

export const oidcConfig = {
    authority: "http://localhost:8180/realms/tax-dividend",
    client_id: "frontend",
    redirect_uri: window.location.origin,
    response_type: 'code',
    scope: 'openid profile email',
    userStore: new WebStorageStateStore({ store: window.sessionStorage }),
    automaticSilentRenew: true,
};

import { MockUserManager } from './mock-auth';

// Check for mock mode
const useMock = import.meta.env.VITE_USE_MOCK_AUTH === 'true';

export const userManager = useMock
    ? new MockUserManager()
    : new UserManager(oidcConfig);
