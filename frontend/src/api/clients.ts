import { Configuration } from './generated';
import { DividendsApi, FormsApi, AuthApi } from './generated/api';
import { api } from './index'; // Our configured axios instance

// Create a configuration that uses our central axios instance
// Note: typescript-axios accepts the axios instance as the second argument to the API constructor
const config = new Configuration({
    basePath: import.meta.env.VITE_API_URL || '/api',
});

// Export instantiated API clients
export const dividendsApi = new DividendsApi(config, undefined, api);
export const formsApi = new FormsApi(config, undefined, api);
export const authApi = new AuthApi(config, undefined, api);
