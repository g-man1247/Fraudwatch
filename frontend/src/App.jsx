import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import Transactions from './pages/Transactions';
import Alerts from './pages/Alerts';
import Rules from './pages/Rules';
import Auth from './pages/Auth';
import { getFlaggedTransactions } from './api/flagged';
import './index.css';

const PAGE_TITLES = {
  '/': { title: 'Dashboard', sub: 'Transaction overview & fraud statistics' },
  '/transactions': { title: 'Transactions', sub: 'Submit and monitor wallet transactions' },
  '/alerts': { title: 'Fraud Alerts', sub: 'Flagged transactions requiring review' },
  '/rules': { title: 'Detection Rules', sub: 'Configure anomaly detection policies' },
};

const AppInner = ({ setAuthenticated }) => {
  const location = useLocation();
  const pageInfo = PAGE_TITLES[location.pathname] || { title: 'FraudWatch', sub: '' };
  const [pendingCount, setPendingCount] = useState(0);

  useEffect(() => {
    const fetchPending = async () => {
      try {
        const fl = await getFlaggedTransactions();
        setPendingCount(fl.filter(a => a.reviewStatus === null).length);
      } catch (_) {}
    };
    fetchPending();
    const id = setInterval(fetchPending, 30000); // refresh every 30s
    return () => clearInterval(id);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('auth_token');
    setAuthenticated(false);
  };

  return (
    <div className="app-shell">
      <Sidebar pendingCount={pendingCount} />
      <div className="main-content">
        <header className="topbar">
          <div className="topbar-title">
            {pageInfo.title}
            {pageInfo.sub && <span className="topbar-subtitle">— {pageInfo.sub}</span>}
          </div>
          <div className="topbar-actions">
            <span style={{ fontSize: 12, color: 'var(--color-muted)', display: 'flex', alignItems: 'center', gap: 6, marginRight: 16 }}>
              <span style={{ width: 8, height: 8, borderRadius: '50%', background: 'var(--color-success)', display: 'inline-block' }} />
              System Online
            </span>
            <button className="btn btn-secondary" onClick={handleLogout}>Log out</button>
          </div>
        </header>
        <div className="page-body">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/transactions" element={<Transactions />} />
            <Route path="/alerts" element={<Alerts />} />
            <Route path="/rules" element={<Rules />} />
          </Routes>
        </div>
      </div>
    </div>
  );
};

function App() {
  const [authenticated, setAuthenticated] = useState(!!localStorage.getItem('auth_token'));

  return (
    <BrowserRouter>
      {authenticated ? (
        <AppInner setAuthenticated={setAuthenticated} />
      ) : (
        <Auth setAuthenticated={setAuthenticated} />
      )}
    </BrowserRouter>
  );
}

export default App;
