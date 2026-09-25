import { useState } from 'react';
import { Bell, Menu, X } from 'lucide-react';
import { NavLink, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { navItems, roleOrder } from '../config/navigation';
import Dashboard from '../pages/Dashboard';
import Assets from '../pages/Assets';
import Purchases from '../pages/Purchases';
import Transfers from '../pages/Transfers';
import AssignmentsExpenditures from '../pages/AssignmentsExpenditures';
import Bases from '../pages/Bases';
import Personnel from '../pages/Personnel';
import EquipmentTypes from '../pages/EquipmentTypes';
import AuditTrail from '../pages/AuditTrail';

export default function AppLayout({ user, onLogout }) {
  const [open, setOpen] = useState(false); const navigate = useNavigate(); const location = useLocation();
  const items = navItems.filter((item) => !item.minRole || roleOrder[user.role] >= roleOrder[item.minRole]); const current = items.find((item) => location.pathname.startsWith(item.to)) || items[0];
  const logout = () => {
    const confirmed = window.confirm('Are you sure you want to logout?');
    if (!confirmed) return;
    localStorage.clear();
    onLogout();
    navigate('/login');
  };
  return <div className="app-shell"><button type="button" className="mobile-menu" onClick={() => setOpen(true)} aria-label="Open navigation"><Menu size={20} /></button><aside className={`sidebar ${open ? 'open' : ''}`} aria-label="Primary navigation"><div className="mobile-close"><button type="button" className="icon-button" onClick={() => setOpen(false)} aria-label="Close navigation"><X size={18} /></button></div><div className="brand-lockup"><div className="crest small" aria-hidden="true">M</div><div><strong>MAMS</strong><span>Asset command</span></div></div><div className="rail-label">WORKSPACE</div><nav className="nav">{items.map((item) => <NavLink onClick={() => setOpen(false)} key={item.to} to={item.to} className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}><span className="nav-short" aria-hidden="true">{item.short}</span><span>{item.label}</span></NavLink>)}</nav><div className="sidebar-bottom"><div className="online-dot" />Live system<span>MySQL / secure session</span></div></aside>{open && <button type="button" className="scrim" onClick={() => setOpen(false)} aria-label="Close navigation" />}<main className="main-content"><header className="topbar"><div><div className="eyebrow">MILITARY ASSET MANAGEMENT SYSTEM</div><h1>{current.label}</h1></div><div className="topbar-actions"><span className="status-chip"><i />System online</span><button type="button" className="icon-button" aria-label="Notifications"><Bell size={17} /></button><button type="button" className="avatar-button" onClick={logout} title="Sign out" aria-label="Sign out">{user.name?.slice(0, 1) || 'A'}</button></div></header><div className="content-wrap"><Routes><Route path="/dashboard" element={<Dashboard user={user} />} /><Route path="/assets" element={<Assets />} /><Route path="/purchases" element={<Purchases />} /><Route path="/transfers" element={<Transfers />} /><Route path="/assignments" element={<AssignmentsExpenditures />} /><Route path="/bases" element={<Bases />} /><Route path="/users" element={<Personnel />} /><Route path="/equipment-types" element={<EquipmentTypes />} /><Route path="/audit" element={<AuditTrail />} /><Route path="*" element={<Navigate to="/dashboard" replace />} /></Routes></div></main></div>;
}
