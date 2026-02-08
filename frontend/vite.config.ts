/// <reference types="vitest" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './src/test/setup.ts',
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
  build: {
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor-react': ['react', 'react-dom', 'react-router-dom'],
          'vendor-ui': ['framer-motion', 'lucide-react', 'sonner', 'clsx', 'tailwind-merge'],
          'vendor-auth': ['react-oidc-context', 'oidc-client-ts'],
          'vendor-utils': ['@tanstack/react-query', 'zod', 'i18next', 'react-i18next', 'axios', 'jotai'],
          'vendor-pdf': ['pdf-lib', 'jszip'],
        },
      },
    },
  },
})
