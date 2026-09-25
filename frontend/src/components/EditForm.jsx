import { useEffect, useState } from 'react';
import { X } from 'lucide-react';
import api from '../api';

export default function EditForm({ type, record, onClose, onSaved }) {
  const [form, setForm] = useState({
    name: record.name || '', code: record.code || '', location: record.location || '',
    assetCode: record.assetCode || '', quantity: record.quantity ?? '', status: record.status || '',
    baseId: record.base?.id || '', equipmentTypeId: record.equipmentType?.id || '',
    purchaseDate: record.purchaseDate || '', referenceNumber: record.referenceNumber || '', unitPrice: record.unitPrice || '', notes: record.notes || '',
    email: record.email || '', role: record.role || '', active: record.active !== false,
    category: record.category || '', description: record.description || ''
  });
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  useEffect(() => { Promise.all([api.get('/bases'), api.get('/equipment-types')]).then(([baseResponse, typeResponse]) => { setBases(baseResponse.data); setEquipmentTypes(typeResponse.data); }).catch(() => setError('Reference data could not be loaded.')); }, []);
  const change = (key, value) => setForm((current) => ({ ...current, [key]: value }));
  const submit = async (event) => {
    event.preventDefault(); setBusy(true); setError('');
    try {
      let endpoint; let payload;
      if (type === 'asset') { endpoint = `/assets/${record.id}`; payload = { assetCode: form.assetCode, quantity: Number(form.quantity), status: form.status, base: { id: Number(form.baseId) }, equipmentType: { id: Number(form.equipmentTypeId) } }; }
      else if (type === 'purchase') { endpoint = `/purchases/${record.id}`; payload = { quantity: Number(form.quantity), purchaseDate: form.purchaseDate, referenceNumber: form.referenceNumber, unitPrice: Number(form.unitPrice), notes: form.notes, base: { id: Number(form.baseId) }, equipmentType: { id: Number(form.equipmentTypeId) } }; }
      else if (type === 'user') { endpoint = `/users/${record.id}`; payload = { name: form.name, role: form.role, active: form.active, base: form.baseId ? { id: Number(form.baseId) } : null }; }
      else { endpoint = `/equipment-types/${record.id}`; payload = { name: form.name, category: form.category, description: form.description }; }
      await api.put(endpoint, payload); onSaved();
    } catch (requestError) { setError(requestError.response?.status === 403 ? 'You do not have permission to perform this action.' : requestError.response?.data?.message || 'The record could not be updated.'); } finally { setBusy(false); }
  };
  const Field = ({ label, name, inputType = 'text', required = true }) => <label className="form-label">{label}<input type={inputType} value={form[name]} onChange={(event) => change(name, event.target.value)} required={required} /></label>;
  const Select = ({ label, name, options, required = true }) => <label className="form-label">{label}<select value={form[name]} onChange={(event) => change(name, event.target.value)} required={required}><option value="">Select...</option>{options.map((option) => <option key={option.id} value={option.id}>{option.name || option.code}</option>)}</select></label>;
  const title = type === 'asset' ? 'Edit asset' : type === 'purchase' ? 'Edit purchase' : type === 'user' ? 'Edit personnel' : 'Edit equipment type';
  return <div className="modal-backdrop"><form className="modal form-modal" onSubmit={submit}><div className="modal-header"><div><div className="eyebrow">EDIT RECORD</div><h3>{title}</h3></div><button type="button" className="icon-button" onClick={onClose} aria-label="Close edit form"><X size={17} /></button></div>{error && <div className="alert error">{error}</div>}{type === 'asset' && <><Field label="Asset code" name="assetCode" /><Select label="Base" name="baseId" options={bases} /><Select label="Equipment type" name="equipmentTypeId" options={equipmentTypes} /><Field label="Quantity" name="quantity" inputType="number" /><Select label="Status" name="status" options={[{ id: 'ACTIVE', name: 'Active' }, { id: 'DAMAGED', name: 'Damaged' }, { id: 'MISSING', name: 'Missing' }]} /></>}{type === 'purchase' && <><Select label="Base" name="baseId" options={bases} /><Select label="Equipment type" name="equipmentTypeId" options={equipmentTypes} /><Field label="Quantity" name="quantity" inputType="number" /><Field label="Purchase date" name="purchaseDate" inputType="date" /><Field label="Reference number" name="referenceNumber" /><Field label="Unit price" name="unitPrice" inputType="number" /><Field label="Notes" name="notes" required={false} /></>}{type === 'user' && <><Field label="Name" name="name" /><Select label="Role" name="role" options={[{ id: 'ADMIN', name: 'Admin' }, { id: 'BASE_COMMANDER', name: 'Base commander' }, { id: 'LOGISTICS_OFFICER', name: 'Logistics officer' }]} /><Select label="Base" name="baseId" options={bases} required={false} /><label className="form-label">Active<select value={String(form.active)} onChange={(event) => change('active', event.target.value === 'true')}><option value="true">Active</option><option value="false">Inactive</option></select></label></>}{type === 'equipmentType' && <><Field label="Name" name="name" /><Field label="Category" name="category" /><Field label="Description" name="description" required={false} /></>}<div className="modal-actions"><button type="button" className="secondary-button" onClick={onClose}>Cancel</button><button className="primary-small" disabled={busy}>{busy ? 'Saving...' : 'Save changes'}</button></div></form></div>;
}
