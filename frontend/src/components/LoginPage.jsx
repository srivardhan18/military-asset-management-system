import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import api from '../api';

export default function LoginPage({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  async function submit(event) {
    event.preventDefault();
    setBusy(true);
    setError('');
    try {
      const { data } = await api.post('/auth/login', { email: email.trim(), password });
      if (!data.token) throw new Error('Missing token');
      const user = { name: data.name, role: data.role, email: data.email };
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(user));
      onLogin(user);
    } catch (requestError) {
      if (requestError.response?.status === 401) setError('Invalid email or password.');
      else if (requestError.response?.status >= 500) setError('The command service is unavailable. Please try again shortly.');
      else setError('Unable to connect to the server. Please try again.');
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    } finally {
      setBusy(false);
    }
  }

  return <main className="login-page">
    <section className="login-visual"><div className="signal-line" /><div className="login-kicker">MAMS / SECURE OPERATIONS</div><h1>Know where every asset stands.</h1><p>One operational picture for inventory, readiness, movement, and accountability across every base.</p><div className="login-stats"><span><strong>24/7</strong> readiness view</span><span><strong>100%</strong> traceable movement</span></div></section>
    <section className="login-card"><div className="crest" aria-hidden="true">M</div><div className="login-kicker">AUTHORIZED PERSONNEL ONLY</div><h2>Welcome back</h2><p className="login-copy">Sign in to your operational workspace.</p><form onSubmit={submit} aria-busy={busy}><label htmlFor="email">Service email</label><input id="email" name="email" type="email" autoComplete="username" value={email} onChange={(event) => setEmail(event.target.value)} required /><label htmlFor="password">Passcode</label><div className="password-field"><input id="password" name="password" type={showPassword ? 'text' : 'password'} autoComplete="current-password" value={password} onChange={(event) => setPassword(event.target.value)} required /><button type="button" className="icon-button" onClick={() => setShowPassword((visible) => !visible)} aria-label={showPassword ? 'Hide password' : 'Show password'}>{showPassword ? <EyeOff size={16} /> : <Eye size={16} />}</button></div>{error && <div className="alert error" role="alert">{error}</div>}<button className="primary-button" disabled={busy}>{busy ? 'Authenticating...' : 'Enter command center'}<span aria-hidden="true">-&gt;</span></button></form></section>
  </main>;
}
