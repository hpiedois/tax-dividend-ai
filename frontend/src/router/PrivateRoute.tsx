import { useAtom } from 'jotai';
import { Navigate, Outlet } from 'react-router-dom';
import { isAuthenticatedAtom } from '../store';

export function PrivateRoute() {
  const [isAuthenticated] = useAtom(isAuthenticatedAtom);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
