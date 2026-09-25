import { useCallback, useEffect, useState } from 'react';
import api from '../api';

export default function useFetch(endpoint) {
  const [state, setState] = useState({ data: [], loading: true, error: null });
  const reload = useCallback(async (signal) => {
    setState((previous) => ({ ...previous, loading: true, error: null }));
    try {
      const requestConfig = signal && typeof signal.aborted === 'boolean' ? { signal } : {};
      const { data } = await api.get(endpoint, requestConfig);
      setState({ data: Array.isArray(data) ? data : [], loading: false, error: null });
    } catch (requestError) {
      if (requestError.code === 'ERR_CANCELED') return;
      setState({ data: [], loading: false, error: requestError });
    }
  }, [endpoint]);

  useEffect(() => {
    const controller = new AbortController();
    reload(controller.signal);
    return () => controller.abort();
  }, [reload]);

  return { ...state, reload };
}
