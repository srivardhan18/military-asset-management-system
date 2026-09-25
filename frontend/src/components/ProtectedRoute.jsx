import { Navigate } from 'react-router-dom';

export default function ProtectedRoute({ user, children }) {
  return user && localStorage.getItem('token') ? children : <Navigate to="/login" replace />;
}
