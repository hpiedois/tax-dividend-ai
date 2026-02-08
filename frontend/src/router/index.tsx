import { createBrowserRouter, Navigate } from 'react-router-dom';
import { LoginScreen } from '../components/auth/LoginScreen';
import { DashboardView } from '../components/views/DashboardView';
import { HistoryView } from '../components/views/HistoryView';
import { SettingsView } from '../components/views/SettingsView';
import { ScanView } from '../components/views/ScanView';
import { FormGeneratorView } from '../components/forms/FormGeneratorView';
import { Layout } from '../components/Layout';
import { PrivateRoute } from './PrivateRoute';

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginScreen />,
  },
  {
    path: '/',
    element: <PrivateRoute />,
    children: [
      {
        element: <Layout />,
        children: [
          {
            index: true,
            element: <Navigate to="/dashboard" replace />,
          },
          {
            path: 'dashboard',
            element: <DashboardView />,
          },
          {
            path: 'history',
            element: <HistoryView />,
          },
          {
            path: 'settings',
            element: <SettingsView />,
          },
          {
            path: 'scan',
            element: <ScanView />,
          },
          {
            path: 'forms',
            element: <FormGeneratorView />,
          },
        ],
      },
    ],
  },
  {
    path: '*',
    element: <Navigate to="/dashboard" replace />,
  },
]);
