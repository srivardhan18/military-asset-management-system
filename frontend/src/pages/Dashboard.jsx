import { useEffect, useState } from 'react';
import { Area, AreaChart, Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { FilePlus2, Package, RefreshCw, ShieldCheck, X } from 'lucide-react';
import { NavLink } from 'react-router-dom';
import api from '../api';
import { LoadingState } from '../components/FeedbackState';

const initialFilters = () => ({
  from: `${new Date().getFullYear()}-01-01`,
  to: new Date().toISOString().slice(0, 10),
  baseId: '',
  equipmentTypeId: ''
});

function requestParams(filters) {
  return Object.fromEntries(Object.entries(filters).filter(([, value]) => value !== ''));
}

function Metric({ label, value, caption, Icon, onClick }) {
  return <article className={`metric-card ${onClick ? 'clickable' : ''}`} onClick={onClick} role={onClick ? 'button' : undefined} tabIndex={onClick ? 0 : undefined} onKeyDown={(event) => { if (onClick && (event.key === 'Enter' || event.key === ' ')) onClick(); }}><span>{caption}</span><Icon size={17} /><strong>{value}</strong><small>{label}</small></article>;
}

function MovementModal({ movement, onClose }) {
  const sections = [['PURCHASES', movement.purchases || []], ['TRANSFER IN', movement.transferIn || []], ['TRANSFER OUT', movement.transferOut || []]];
  return <div className="modal-backdrop" onMouseDown={onClose}><div className="modal movement-modal" onMouseDown={(event) => event.stopPropagation()}><div className="modal-header"><div><div className="eyebrow">NET MOVEMENT LEDGER</div><h3>Movement details</h3></div><button className="icon-button" onClick={onClose} aria-label="Close movement details"><X size={17} /></button></div>{sections.map(([title, rows]) => <section className="movement-section" key={title}><div className="panel-heading"><h4>{title}</h4><span className="record-count">{rows.length} RECORDS</span></div>{rows.length === 0 ? <div className="table-state compact">No records in this section.</div> : <div className="table-wrap"><table><thead><tr><th>Date</th><th>Base</th><th>Equipment</th><th>Quantity</th><th>Reference</th></tr></thead><tbody>{rows.map((row, index) => <tr key={`${title}-${row.referenceNumber}-${index}`}><td>{row.date}</td><td>{row.base}</td><td>{row.equipmentType}</td><td>{row.quantity}</td><td>{row.referenceNumber}</td></tr>)}</tbody></table></div>}</section>)}</div></div>;
}

export default function Dashboard({ user }) {
  const [summary, setSummary] = useState({});
  const [trends, setTrends] = useState({});
  const [filters, setFilters] = useState(initialFilters);
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [showMovement, setShowMovement] = useState(false);
  const [movement, setMovement] = useState(null);
  const [movementLoading, setMovementLoading] = useState(false);

  const load = async (nextFilters = filters) => {
    setLoading(true);
    setError('');
    try {
      const params = requestParams(nextFilters);
      const [summaryResponse, trendsResponse] = await Promise.all([
        api.get('/dashboard', { params }),
        api.get('/dashboard/trends', { params: { from: nextFilters.from, to: nextFilters.to, baseId: nextFilters.baseId || undefined, equipmentTypeId: nextFilters.equipmentTypeId || undefined } })
      ]);
      setSummary(summaryResponse.data);
      setTrends(trendsResponse.data);
    } catch (requestError) {
      setError(requestError.response?.status === 403 ? 'You do not have permission to view this dashboard.' : requestError.response?.data?.message || 'Unable to load dashboard data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    Promise.all([api.get('/bases'), api.get('/equipment-types')]).then(([baseResponse, typeResponse]) => { setBases(baseResponse.data); setEquipmentTypes(typeResponse.data); }).catch(() => setError('Dashboard filters could not be loaded.'));
    load();
  }, []);

  const applyFilters = () => load(filters);
  const resetFilters = () => { const reset = initialFilters(); setFilters(reset); load(reset); };
  const openMovement = async () => {
    setMovementLoading(true);
    try { const { data } = await api.get('/dashboard/movement', { params: requestParams(filters) }); setMovement(data); setShowMovement(true); } catch { setError('Unable to load movement details.'); } finally { setMovementLoading(false); }
  };
  const chartData = [{ name: 'Purchases', value: trends.purchases || 0 }, { name: 'Transfer in', value: trends.transferIn || 0 }, { name: 'Transfer out', value: trends.transferOut || 0 }, { name: 'Assignments', value: trends.assignments || 0 }, { name: 'Expenditures', value: trends.expenditures || 0 }];
  const cards = [['Opening balance', summary.openingBalance ?? 0, 'LEDGER START', Package], ['Closing balance', summary.closingBalance ?? 0, 'CURRENT INVENTORY', Package], ['Net movement', summary.netMovement ?? 0, 'PURCHASES + IN - OUT', RefreshCw], ['Assigned assets', summary.assignedAssets ?? 0, 'ALLOCATED', ShieldCheck], ['Expended assets', summary.expendedAssets ?? 0, 'CONSUMED', FilePlus2], ['Purchases', summary.purchases ?? 0, 'UNITS RECEIVED', FilePlus2], ['Transfer in', summary.transferIn ?? 0, 'UNITS RECEIVED', RefreshCw], ['Transfer out', summary.transferOut ?? 0, 'UNITS MOVED', RefreshCw]];

  return <>
    <section className="welcome-row"><div><div className="eyebrow">GOOD DAY, {user.name?.toUpperCase()}</div><h2>Command overview</h2><p>Real-time asset visibility across bases and movement periods.</p></div><div className="dashboard-controls"><label>From<input type="date" value={filters.from} onChange={(event) => setFilters({ ...filters, from: event.target.value })} /></label><label>To<input type="date" value={filters.to} onChange={(event) => setFilters({ ...filters, to: event.target.value })} /></label><label>Base<select value={filters.baseId} onChange={(event) => setFilters({ ...filters, baseId: event.target.value })}><option value="">All bases</option>{bases.map((base) => <option key={base.id} value={base.id}>{base.code}</option>)}</select></label><label>Equipment<select value={filters.equipmentTypeId} onChange={(event) => setFilters({ ...filters, equipmentTypeId: event.target.value })}><option value="">All equipment</option>{equipmentTypes.map((type) => <option key={type.id} value={type.id}>{type.name}</option>)}</select></label><button className="primary-small" onClick={applyFilters}>Apply filters</button><button className="secondary-button" onClick={resetFilters}>Reset</button><button className="secondary-button" onClick={() => load()} aria-label="Refresh dashboard"><RefreshCw size={15} /></button></div></section>
    {error && <div className="alert error">{error} <button onClick={() => load()}>Retry</button></div>}
    {loading ? <LoadingState label="Loading dashboard data..." /> : <><div className="metric-grid">{cards.map(([label, value, caption, Icon]) => <Metric key={label} label={label} value={value} caption={caption} Icon={Icon} onClick={label === 'Net movement' ? openMovement : undefined} />)}</div><div className="dashboard-actions"><button className="secondary-button" onClick={openMovement} disabled={movementLoading}>{movementLoading ? 'Loading movement...' : 'Open net movement details'}</button></div><section className="chart-grid"><div className="panel chart-panel"><div className="panel-heading"><div><div className="eyebrow">MOVEMENT SIGNAL</div><h3>Activity in selected period</h3></div><span className="record-count">LIVE API DATA</span></div><ResponsiveContainer width="100%" height={240}><AreaChart data={chartData}><defs><linearGradient id="dashboardArea" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#9ed5ac" stopOpacity={.45} /><stop offset="100%" stopColor="#9ed5ac" stopOpacity={0} /></linearGradient></defs><CartesianGrid stroke="#29423e" strokeDasharray="3 3" vertical={false} /><XAxis dataKey="name" stroke="#78958e" tick={{ fontSize: 11 }} /><YAxis stroke="#78958e" tick={{ fontSize: 11 }} /><Tooltip contentStyle={{ background: '#10272b', border: '1px solid #315650' }} /><Area type="monotone" dataKey="value" stroke="#a9dfb2" fill="url(#dashboardArea)" strokeWidth={2} /></AreaChart></ResponsiveContainer></div><div className="panel chart-panel"><div className="eyebrow">RECORD MIX</div><h3>Operational events</h3><ResponsiveContainer width="100%" height={240}><BarChart data={chartData}><CartesianGrid stroke="#29423e" strokeDasharray="3 3" vertical={false} /><XAxis dataKey="name" stroke="#78958e" tick={{ fontSize: 10 }} /><YAxis stroke="#78958e" tick={{ fontSize: 11 }} /><Tooltip contentStyle={{ background: '#10272b', border: '1px solid #315650' }} /><Bar dataKey="value" fill="#75b28a" radius={[4, 4, 0, 0]} /></BarChart></ResponsiveContainer></div></section><section className="dashboard-grid"><div className="panel feature-panel"><div className="eyebrow">PURCHASE VALUE</div><h3>${summary.totalPurchaseValue ?? '0.00'}</h3><p>Purchase value recorded in the selected period and filters.</p></div><div className="panel quick-panel"><div className="eyebrow">QUICK ACCESS</div><h3>Move with intent</h3><div className="quick-list"><NavLink to="/assets">Review inventory <b>-&gt;</b></NavLink><NavLink to="/transfers">Monitor transfers <b>-&gt;</b></NavLink><button className="quick-link" onClick={openMovement}>Open movement ledger <b>-&gt;</b></button></div></div></section></>}
    {showMovement && movement && <MovementModal movement={movement} onClose={() => setShowMovement(false)} />}
  </>;
}
