import { useEffect, useState } from 'react';
import { X } from 'lucide-react';
import api from '../api';

export default function CreateForm({ type, onClose, onCreated }) {
  const [form, setForm] = useState({});
  const [bases, setBases] = useState([]);
  const [types, setTypes] = useState([]);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  useEffect(() => { Promise.all([api.get('/bases'), api.get('/equipment-types')]).then(([baseResponse, typeResponse]) => { setBases(baseResponse.data); setTypes(typeResponse.data); }).catch(() => setError('Reference data could not be loaded.')); }, []);
  const change = (key, value) => setForm((current) => ({ ...current, [key]: value }));
  const submit = async (event) => {
    event.preventDefault(); setBusy(true); setError('');
    try {
      const base = form.baseId ? { id: Number(form.baseId) } : undefined;
      const equipmentType = form.equipmentTypeId ? { id: Number(form.equipmentTypeId) } : undefined;
      let endpoint; let payload;
      if (type === 'asset') { endpoint = '/assets'; payload = { assetCode: form.assetCode, quantity: Number(form.quantity), status: form.status || 'ACTIVE', base, equipmentType }; }
      else if (type === 'purchase') { endpoint = '/purchases'; payload = { quantity: Number(form.quantity), purchaseDate: form.date, referenceNumber: form.referenceNumber, unitPrice: Number(form.unitPrice || 0), notes: form.notes, base, equipmentType }; }
      else if (type === 'transfer') { endpoint = '/transfers'; payload = { quantity: Number(form.quantity), transferDate: form.date, referenceNumber: form.referenceNumber, notes: form.notes, status: 'COMPLETED', fromBase: { id: Number(form.fromBaseId) }, toBase: { id: Number(form.toBaseId) }, equipmentType }; }
      else if (type === 'base') { endpoint = '/bases'; payload = { name: form.name, code: form.code, location: form.location, active: form.active !== 'false' }; }
      else if (type === 'equipmentType') { endpoint = '/equipment-types'; payload = { name: form.name, category: form.category, description: form.description }; }
      else { endpoint = '/users'; payload = { name: form.name, email: form.email, password: form.password, role: form.role, base }; }
      await api.post(endpoint, payload); onCreated();
    } catch (requestError) { setError(requestError.response?.status === 403 ? 'You do not have permission for this action.' : requestError.response?.data?.message || 'The record could not be saved. Check required fields.'); } finally { setBusy(false); }
  };
  const title = type === 'asset' ? 'Add asset' : type === 'purchase' ? 'Record purchase' : type === 'transfer' ? 'Create transfer' : type === 'base' ? 'Add base' : type === 'equipmentType' ? 'Add equipment type' : 'Add personnel';
  const Field = ({ label, name, inputType = 'text', required = false }) => <label className="form-label">{label}{required && ' *'}<input type={inputType} value={form[name] || ''} onChange={(event) => change(name, event.target.value)} required={required} min={inputType === 'number' ? 1 : undefined} /></label>;
  const Select = ({ label, name, options, required = true }) => <label className="form-label">{label}<select value={form[name] || ''} onChange={(event) => change(name, event.target.value)} required={required}><option value="">Select...</option>{options.map((option) => <option key={option.id} value={option.id}>{option.name || option.code}</option>)}</select></label>;
  const referenceFields = <>{type === 'transfer' ? <><Select label="From base" name="fromBaseId" options={bases} /><Select label="To base" name="toBaseId" options={bases} /></> : <Select label="Base" name="baseId" options={bases} required={type !== 'equipmentType'} />}<Select label="Equipment type" name="equipmentTypeId" options={types} required={false} /></>;
  return <div className="modal-backdrop"><form className="modal form-modal" onSubmit={submit}><div className="modal-header"><div><div className="eyebrow">NEW RECORD</div><h3>{title}</h3></div><button type="button" className="icon-button" onClick={onClose} aria-label="Close form"><X size={17} /></button></div>{error && <div className="alert error">{error}</div>}{type === 'asset' && <Field label="Asset code" name="assetCode" required />}{['asset', 'purchase', 'transfer'].includes(type) && referenceFields}{type === 'purchase' && <><Field label="Reference number" name="referenceNumber" required /><Field label="Unit price" name="unitPrice" inputType="number" /><Field label="Notes" name="notes" /></>}{type === 'transfer' && <><Field label="Reference number" name="referenceNumber" required /><Field label="Notes" name="notes" /></>}{type === 'asset' && <><Field label="Quantity" name="quantity" inputType="number" required /><Select label="Status" name="status" options={[{ id: 'ACTIVE', name: 'Active' }, { id: 'DAMAGED', name: 'Damaged' }, { id: 'MISSING', name: 'Missing' }]} /></>}{['purchase', 'transfer'].includes(type) && <><Field label="Quantity" name="quantity" inputType="number" required /><Field label="Date" name="date" inputType="date" required /></>}{type === 'base' && <><Field label="Name" name="name" required /><Field label="Code" name="code" required /><Field label="Location" name="location" required /></>}{type === 'equipmentType' && <><Field label="Name" name="name" required /><Field label="Category" name="category" required /><Field label="Description" name="description" /></>}{type === 'user' && <><Field label="Name" name="name" required /><Field label="Email" name="email" inputType="email" required /><Field label="Password" name="password" inputType="password" required /><Select label="Role" name="role" options={[{ id: 'ADMIN', name: 'Admin' }, { id: 'BASE_COMMANDER', name: 'Base commander' }, { id: 'LOGISTICS_OFFICER', name: 'Logistics officer' }]} /><Select label="Base" name="baseId" options={bases} required={false} /></>}<div className="modal-actions"><button type="button" className="secondary-button" onClick={onClose}>Cancel</button><button type="submit" className="primary-small" disabled={busy}>{busy ? 'Saving...' : 'Save record'}</button></div></form></div>;
}
