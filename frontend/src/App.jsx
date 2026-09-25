import { useState } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import AppLayout from './layouts/AppLayout';
import LoginPage from './components/LoginPage';
import ProtectedRoute from './components/ProtectedRoute';

export default function App() {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('user')) || null; } catch { return null; }
  });
  return <Routes>
    <Route path="/login" element={user ? <Navigate to="/dashboard" replace /> : <LoginPage onLogin={setUser} />} />
    <Route path="/*" element={<ProtectedRoute user={user}><AppLayout user={user} onLogout={() => setUser(null)} /></ProtectedRoute>} />
  </Routes>;
}
