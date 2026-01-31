import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
    appId: 'com.taxdividendai.app',
    appName: 'Tax Dividend AI',
    webDir: 'dist',
    server: {
        androidScheme: 'https'
    }
};

export default config;
