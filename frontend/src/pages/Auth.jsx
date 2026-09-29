import React, { useState } from 'react';
import { apiClient } from '../api/client';
import ErrorBanner from '../components/Shared';
import { useNavigate } from 'react-router-dom';

const Auth = ({ setAuthenticated }) => {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setMessage(null);
    setLoading(true);

    try {
      if (isLogin) {
        // HTTP Basic token
        const token = btoa(`${username}:${password}`);
        localStorage.setItem('auth_token', token);
        
        // Test it
        await apiClient('/api/auth/login', { method: 'POST' });
        
        setAuthenticated(true);
      } else {
        await apiClient('/api/auth/register', {
          method: 'POST',
          body: JSON.stringify({ username, password })
        });
        setMessage("Registration successful. Please log in.");
        setIsLogin(true);
      }
    } catch (err) {
      if (err.status === 401) {
        setError('Invalid username or password');
        localStorage.removeItem('auth_token');
      } else {
        setError(err.message || 'Authentication failed');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh',
      background: 'var(--color-bg)'
    }}>
      <div className="panel" style={{ width: '100%', maxWidth: 400, padding: 30 }}>
        <h2 style={{ textAlign: 'center', marginBottom: 20 }}>
          {isLogin ? 'Login to FraudWatch' : 'Register Account'}
        </h2>

        {error && <ErrorBanner message={error} />}
        {message && <div className="feedback-success" style={{ marginBottom: 16 }}>{message}</div>}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input 
              className="form-input" 
              value={username} 
              onChange={e => setUsername(e.target.value)} 
              required 
            />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input 
              className="form-input" 
              type="password" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              required 
            />
          </div>

          <button className="btn btn-primary" type="submit" disabled={loading}>
            {loading ? 'Please wait...' : (isLogin ? 'Log In' : 'Sign Up')}
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: 20, fontSize: 14 }}>
          {isLogin ? (
            <span>Don't have an account? <a href="#" onClick={(e) => { e.preventDefault(); setIsLogin(false); }} style={{ color: 'var(--color-primary)' }}>Register here</a></span>
          ) : (
            <span>Already have an account? <a href="#" onClick={(e) => { e.preventDefault(); setIsLogin(true); }} style={{ color: 'var(--color-primary)' }}>Login here</a></span>
          )}
        </div>
      </div>
    </div>
  );
};

export default Auth;
