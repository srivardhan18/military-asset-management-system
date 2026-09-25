export function LoadingState({ label = 'Loading records...' }) {
  return <div className="table-state" role="status"><span className="loading-spinner" aria-hidden="true" />{label}</div>;
}

export function EmptyState({ title = 'No records found.', detail = 'Try another filter or refresh the live collection.' }) {
  return <div className="table-state"><strong>{title}</strong><span>{detail}</span></div>;
}

export function ErrorState({ error, onRetry, fallback = 'Unable to load records.' }) {
  const status = error?.response?.status;
  const message = status === 403 ? 'You do not have permission to perform this action.' : error?.response?.data?.message || fallback;
  return <div className="alert error" role="alert">{message}{onRetry && <button onClick={onRetry}>Retry</button>}</div>;
}