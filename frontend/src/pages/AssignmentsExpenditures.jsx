import { useEffect, useState } from 'react';
import { RefreshCw, X } from 'lucide-react';
import api from '../api';
import useFetch from '../hooks/useFetch';
import { EmptyState, ErrorState, LoadingState } from '../components/FeedbackState';

function EntryForm({ mode, references, onClose, onCreated }) {
	const [form, setForm] = useState({});
	const [busy, setBusy] = useState(false);
	const [error, setError] = useState('');
	const change = (key, value) => setForm({ ...form, [key]: value });
	const submit = async (event) => {
		event.preventDefault();
		setBusy(true);
		setError('');
		try {
			const payload = mode === 'assignment'
				? { asset: { id: Number(form.assetId) }, base: { id: Number(form.baseId) }, personnelName: form.personnelName, quantity: Number(form.quantity), assignmentDate: form.date, status: form.status || 'ACTIVE' }
				: { base: { id: Number(form.baseId) }, equipmentType: { id: Number(form.equipmentTypeId) }, quantity: Number(form.quantity), expenditureDate: form.date, reason: form.reason };
			await api.post(mode === 'assignment' ? '/assignments' : '/expenditures', payload);
			onCreated();
		} catch (requestError) {
			setError(requestError.response?.status === 403 ? 'You do not have permission to perform this action.' : requestError.response?.data?.message || 'The record could not be saved. Check all required fields.');
		} finally { setBusy(false); }
	};
	const Select = ({ label, name, options }) => <label className="form-label">{label}<select required value={form[name] || ''} onChange={(event) => change(name, event.target.value)}><option value="">Select...</option>{options.map((option) => <option key={option.id} value={option.id}>{option.name || option.code || option.assetCode}</option>)}</select></label>;
	return <div className="modal-backdrop"><form className="modal form-modal" onSubmit={submit}><div className="modal-header"><div><div className="eyebrow">NEW RECORD</div><h3>{mode === 'assignment' ? 'Create assignment' : 'Record expenditure'}</h3></div><button className="icon-button" type="button" onClick={onClose}><X size={17} /></button></div>{error && <div className="alert error">{error}</div>}{mode === 'assignment' ? <><Select label="Asset" name="assetId" options={references.assets} /><Select label="Base" name="baseId" options={references.bases} /><label className="form-label">Personnel<input required value={form.personnelName || ''} onChange={(event) => change('personnelName', event.target.value)} /></label></> : <><Select label="Base" name="baseId" options={references.bases} /><Select label="Equipment type" name="equipmentTypeId" options={references.types} /><label className="form-label">Reason<input required value={form.reason || ''} onChange={(event) => change('reason', event.target.value)} /></label></>}<label className="form-label">Quantity<input required min="1" type="number" value={form.quantity || ''} onChange={(event) => change('quantity', event.target.value)} /></label><label className="form-label">Date<input required type="date" value={form.date || ''} onChange={(event) => change('date', event.target.value)} /></label>{mode === 'assignment' && <Select label="Status" name="status" options={[{ id: 'ACTIVE', name: 'Active' }, { id: 'COMPLETED', name: 'Completed' }]} />}<div className="modal-actions"><button className="secondary-button" type="button" onClick={onClose}>Cancel</button><button className="primary-small" disabled={busy}>{busy ? 'Saving...' : 'Save record'}</button></div></form></div>;
}

function DataTable({ type, data, loading, error, reload, query }) {
	const records = data.map((item) => ({ item, values: type === 'assignment' ? [item.personnelName, item.asset?.assetCode, item.base?.code, item.quantity, item.assignmentDate, item.status] : [item.expenditureDate, item.base?.code, item.equipmentType?.name, item.quantity, item.reason] }));
	const visibleRecords = records.filter(({ values }) => values.join(' ').toLowerCase().includes(query.toLowerCase()));
	if (loading) return <LoadingState label="Loading secure records..." />;
	if (error) return <ErrorState error={error} onRetry={reload} fallback="Unable to load records." />;
	if (visibleRecords.length === 0) return <EmptyState title="No records found." />;
	return <div className="table-wrap"><table><caption className="sr-only">{type === 'assignment' ? 'Assignments' : 'Expenditures'}</caption><thead><tr>{(type === 'assignment' ? ['Personnel', 'Asset', 'Base', 'Quantity', 'Date', 'Status'] : ['Date', 'Base', 'Equipment', 'Quantity', 'Reason']).map((heading) => <th scope="col" key={heading}>{heading}</th>)}</tr></thead><tbody>{visibleRecords.map(({ item, values }) => <tr key={item.id}>{values.map((value, cellIndex) => <td key={cellIndex}>{value || '-'}</td>)}</tr>)}</tbody></table></div>;
}

export default function AssignmentsExpenditures() {
	const [tab, setTab] = useState('assignment');
	const [query, setQuery] = useState('');
	const [showForm, setShowForm] = useState(false);
	const assignments = useFetch('/assignments');
	const expenditures = useFetch('/expenditures');
	const [references, setReferences] = useState({ assets: [], bases: [], types: [] });
	const [referencesError, setReferencesError] = useState(false);
	useEffect(() => { Promise.all([api.get('/assets'), api.get('/bases'), api.get('/equipment-types')]).then(([assets, bases, types]) => setReferences({ assets: assets.data, bases: bases.data, types: types.data })).catch(() => setReferencesError(true)); }, []);
	const current = tab === 'assignment' ? assignments : expenditures;
	const filtered = current.data;
	return <section className="panel table-panel"><div className="section-heading"><div><div className="eyebrow">OPERATIONS / TRACKING</div><h2>Assignments & expenditures</h2><p>Control allocation and consumption against the live inventory.</p></div><div className="table-actions"><input className="search-input" placeholder="Search records..." value={query} onChange={(event) => setQuery(event.target.value)} /><button className="secondary-button" onClick={current.reload} aria-label="Refresh records"><RefreshCw size={15} /></button><button className="primary-small" onClick={() => setShowForm(true)} disabled={referencesError}>+ Add</button></div></div><div className="tabs"><button className={tab === 'assignment' ? 'tab active' : 'tab'} onClick={() => { setTab('assignment'); setQuery(''); }}>Assignments</button><button className={tab === 'expenditure' ? 'tab active' : 'tab'} onClick={() => { setTab('expenditure'); setQuery(''); }}>Expenditures</button></div>{referencesError && <div className="alert error">Reference data could not be loaded. Refresh the page before creating a record.</div>}<DataTable type={tab} data={filtered} loading={current.loading} error={current.error} reload={current.reload} query={query} />{showForm && <EntryForm mode={tab} references={references} onClose={() => setShowForm(false)} onCreated={() => { setShowForm(false); current.reload(); }} />}</section>;
}
