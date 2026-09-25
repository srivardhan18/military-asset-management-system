import { useEffect, useMemo, useState } from 'react';
import { RefreshCw, X } from 'lucide-react';
import api from '../api';
import useFetch from '../hooks/useFetch';
import { resources } from '../config/resources';
import CreateForm from './CreateForm';
import EditForm from './EditForm';
import { EmptyState, ErrorState, LoadingState } from './FeedbackState';

function matchesFilters(item, filters) {
  if (filters.base && item.base?.id !== Number(filters.base) && item.fromBase?.id !== Number(filters.base) && item.toBase?.id !== Number(filters.base)) return false;
  if (filters.fromBase && item.fromBase?.id !== Number(filters.fromBase)) return false;
  if (filters.toBase && item.toBase?.id !== Number(filters.toBase)) return false;
  if (filters.equipmentType && item.equipmentType?.id !== Number(filters.equipmentType)) return false;
  if (filters.status && (item.status || (item.active ? 'ACTIVE' : 'INACTIVE')) !== filters.status) return false;
  if (filters.from && (item.purchaseDate || item.transferDate) < filters.from) return false;
  if (filters.to && (item.purchaseDate || item.transferDate) > filters.to) return false;
  return true;
}

export default function ResourcePage({ type }) {
  const config = resources[type];
  const { data, loading, error, reload } = useFetch(config.endpoint);
  const [query, setQuery] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [selected, setSelected] = useState(null);
  const [filters, setFilters] = useState({});
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);
  const [selectedSummary, setSelectedSummary] = useState(null);
  const [actionError, setActionError] = useState('');
  const [actionSuccess, setActionSuccess] = useState('');

  useEffect(() => {
    if (config.filters?.some((filter) => ['base', 'fromBase', 'toBase'].includes(filter))) api.get('/bases').then(({ data: values }) => setBases(values)).catch(() => setActionError('Reference bases could not be loaded.'));
    if (config.filters?.includes('equipmentType')) api.get('/equipment-types').then(({ data: values }) => setEquipmentTypes(values)).catch(() => setActionError('Equipment types could not be loaded.'));
  }, [type]);
  useEffect(() => {
    if (type === 'bases' && selected) api.get(`/bases/${selected.id}/summary`).then(({ data: values }) => setSelectedSummary(values)).catch(() => setSelectedSummary(null));
    else setSelectedSummary(null);
  }, [selected, type]);

  useEffect(() => {
    if (!selected) return undefined;
    const closeOnEscape = (event) => { if (event.key === 'Escape') closeDetails(); };
    document.addEventListener('keydown', closeOnEscape);
    return () => document.removeEventListener('keydown', closeOnEscape);
  }, [selected]);

  const filtered = useMemo(() => data.filter((item) => config.search(item).toLowerCase().includes(query.toLowerCase()) && matchesFilters(item, filters)), [data, query, filters, config]);
  const setFilter = (name, value) => setFilters((current) => ({ ...current, [name]: value }));
  const closeDetails = () => { setSelected(null); setSelectedSummary(null); setShowEdit(false); };
  const deactivate = async () => {
    try { await api.delete(`/assets/${selected.id}`); closeDetails(); setActionSuccess('Asset deactivated successfully.'); reload(); }
    catch (requestError) { setActionError(requestError.response?.status === 403 ? 'You do not have permission to perform this action.' : requestError.response?.data?.message || 'The asset could not be deactivated.'); }
  };
  const cancelTransfer = async () => {
    try { await api.post(`/transfers/${selected.id}/cancel`); closeDetails(); setActionSuccess('Transfer cancelled and inventory reversed.'); reload(); }
    catch (requestError) { setActionError(requestError.response?.status === 403 ? 'You do not have permission to perform this action.' : requestError.response?.data?.message || 'The transfer could not be cancelled.'); }
  };
  const Filter = ({ name, children }) => <label className="filter-control">{name.replace(/([A-Z])/g, ' $1')}<select value={filters[name] || ''} onChange={(event) => setFilter(name, event.target.value)}><option value="">All</option>{children}</select></label>;
  const editable = ['assets', 'purchases', 'users', 'equipmentTypes'].includes(type);

  return <section className="panel table-panel">
    <div className="section-heading"><div><div className="eyebrow">NETWORK RECORDS / {type.toUpperCase()}</div><h2>{config.title}</h2><p>{config.description}</p></div><div className="table-actions"><input className="search-input" placeholder="Search records..." value={query} onChange={(event) => setQuery(event.target.value)} /><button className="secondary-button" onClick={reload} aria-label="Refresh records"><RefreshCw size={15} /></button>{config.create && <button className="primary-small" onClick={() => setShowForm(true)}>+ Add</button>}</div></div>
    {config.filters && <div className="filter-bar">{config.filters.includes('base') && <Filter name="base">{bases.map((base) => <option key={base.id} value={base.id}>{base.code}</option>)}</Filter>}{config.filters.includes('fromBase') && <Filter name="fromBase">{bases.map((base) => <option key={base.id} value={base.id}>{base.code}</option>)}</Filter>}{config.filters.includes('toBase') && <Filter name="toBase">{bases.map((base) => <option key={base.id} value={base.id}>{base.code}</option>)}</Filter>}{config.filters.includes('equipmentType') && <Filter name="equipmentType">{equipmentTypes.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</Filter>}{config.filters.includes('status') && <Filter name="status"><option value="ACTIVE">Active</option><option value="COMPLETED">Completed</option><option value="CANCELLED">Cancelled</option><option value="DAMAGED">Damaged</option><option value="MISSING">Missing</option></Filter>}{config.filters.includes('from') && <label className="filter-control">From<input type="date" value={filters.from || ''} onChange={(event) => setFilter('from', event.target.value)} /></label>}{config.filters.includes('to') && <label className="filter-control">To<input type="date" value={filters.to || ''} onChange={(event) => setFilter('to', event.target.value)} /></label>}<button className="secondary-button" onClick={() => setFilters({})}>Reset filters</button></div>}
    {actionError && <div className="alert error" role="alert">{actionError}<button onClick={() => setActionError('')}>Dismiss</button></div>}{actionSuccess && <div className="alert success" role="status">{actionSuccess}<button onClick={() => setActionSuccess('')}>Dismiss</button></div>}
    {error ? <ErrorState error={error} onRetry={reload} fallback={`Unable to load ${config.title.toLowerCase()}.`} /> : loading ? <LoadingState label={`Loading ${config.title.toLowerCase()}...`} /> : filtered.length === 0 ? <EmptyState title={`No ${config.title.toLowerCase()} found.`} /> : <div className="table-wrap"><table><caption className="sr-only">{config.title}</caption><thead><tr>{config.columns.map(([label]) => <th scope="col" key={label}>{label}</th>)}</tr></thead><tbody>{filtered.map((item) => <tr key={item.id} onClick={() => setSelected(item)} className="data-row" tabIndex="0" onKeyDown={(event) => { if (event.key === 'Enter' || event.key === ' ') setSelected(item); }}>{config.columns.map(([label, getter]) => <td key={label}>{getter(item) || '-'}{label === 'Status' && getter(item) ? <span className="status-dot" /> : null}</td>)}</tr>)}</tbody></table></div>}
    {selected && <div className="modal-backdrop" onMouseDown={closeDetails}><div className="modal" onMouseDown={(event) => event.stopPropagation()}><div className="modal-header"><div><div className="eyebrow">RECORD DETAIL</div><h3>{config.title}</h3></div><button className="icon-button" onClick={closeDetails} aria-label="Close details"><X size={17} /></button></div><div className="detail-grid">{config.columns.map(([label, getter]) => <div key={label}><span>{label}</span><strong>{getter(selected) || '-'}</strong></div>)}</div>{type === 'bases' && selectedSummary && <div className="detail-grid base-summary-grid">{Object.entries(selectedSummary).map(([label, value]) => <div key={label}><span>{label.replace(/([A-Z])/g, ' $1')}</span><strong>{value}</strong></div>)}</div>}<div className="modal-actions">{editable && <button className="primary-small" onClick={() => setShowEdit(true)}>Edit</button>}{type === 'assets' && <button className="secondary-button" onClick={deactivate}>Deactivate asset</button>}{type === 'transfers' && selected.status === 'COMPLETED' && <button className="secondary-button" onClick={cancelTransfer}>Cancel and reverse transfer</button>}</div></div></div>}
    {showForm && <CreateForm type={config.create} onClose={() => setShowForm(false)} onCreated={() => { setShowForm(false); setActionSuccess('Record created successfully.'); reload(); }} />}{showEdit && selected && <EditForm type={type === 'equipmentTypes' ? 'equipmentType' : type === 'users' ? 'user' : type.slice(0, -1)} record={selected} onClose={() => setShowEdit(false)} onSaved={() => { setShowEdit(false); closeDetails(); setActionSuccess('Changes saved successfully.'); reload(); }} />}
  </section>;
}
