import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { QueryClientProvider } from '@tanstack/react-query'
import { AuthProvider } from 'react-oidc-context';
import { userManager } from './lib/auth';
import './index.css'
import './i18n'
import App from './App.tsx'
import { ThemeProvider } from './components/theme-provider';
import { ErrorBoundary } from './components/error/ErrorBoundary.tsx'
import { Toaster } from './components/ui/Toaster.tsx'
import { queryClient } from './lib/query-client.ts'
import { initDebugUtils } from './lib/debug-utils'

import { setupApi } from './api';

// Initialize debug utilities in development mode
if (import.meta.env.DEV) {
  initDebugUtils();
}

const onSigninCallback = () => {
  window.history.replaceState({}, document.title, window.location.pathname);
};

// Initialize API (and mocks if enabled) before rendering
setupApi().then(() => {
  createRoot(document.getElementById('root')!).render(
    <StrictMode>
      <AuthProvider userManager={userManager} onSigninCallback={onSigninCallback}>
        <ErrorBoundary>
          <QueryClientProvider client={queryClient}>
            <ThemeProvider defaultTheme="system" storageKey="tax-dividend-ui-theme">
              <App />
              <Toaster />
            </ThemeProvider>
          </QueryClientProvider>
        </ErrorBoundary>
      </AuthProvider>
    </StrictMode>,
  );
});
