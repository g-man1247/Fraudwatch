import React from 'react';
import { Link, Outlet } from 'react-router-dom';

const Layout = () => {
  return (
    <div style={{ fontFamily: 'system-ui, sans-serif', maxWidth: '1000px', margin: '0 auto', padding: '20px' }}>
      <header style={{ borderBottom: '1px solid #ccc', paddingBottom: '10px', marginBottom: '20px' }}>
        <h1 style={{ margin: '0 0 10px 0' }}>FraudWatch</h1>
        <nav style={{ display: 'flex', gap: '15px' }}>
          <Link to="/" style={{ textDecoration: 'none', color: '#0066cc' }}>Dashboard</Link>
          <Link to="/rules" style={{ textDecoration: 'none', color: '#0066cc' }}>Rules</Link>
        </nav>
      </header>
      
      <main>
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
