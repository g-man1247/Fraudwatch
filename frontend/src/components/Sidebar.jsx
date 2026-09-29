import React from 'react';
import { NavLink } from 'react-router-dom';

const Sidebar = ({ pendingCount }) => {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="sidebar-brand-icon">🛡️</div>
        <div className="sidebar-brand-text">
          <span className="sidebar-brand-name">FraudWatch</span>
          <span className="sidebar-brand-sub">Anomaly Detection</span>
        </div>
      </div>

      <div className="sidebar-section">
        <div className="sidebar-section-label">Main</div>
        <ul className="sidebar-nav">
          <li className="sidebar-nav-item">
            <NavLink to="/" end className={({ isActive }) => isActive ? 'active' : ''}>
              <span className="nav-icon">📊</span> Dashboard
            </NavLink>
          </li>
          <li className="sidebar-nav-item">
            <NavLink to="/transactions" className={({ isActive }) => isActive ? 'active' : ''}>
              <span className="nav-icon">💳</span> Transactions
            </NavLink>
          </li>
          <li className="sidebar-nav-item">
            <NavLink to="/alerts" className={({ isActive }) => isActive ? 'active' : ''}>
              <span className="nav-icon">🚨</span> Fraud Alerts
              {pendingCount > 0 && <span className="nav-badge">{pendingCount}</span>}
            </NavLink>
          </li>
        </ul>
      </div>

      <div className="sidebar-section">
        <div className="sidebar-section-label">Configuration</div>
        <ul className="sidebar-nav">
          <li className="sidebar-nav-item">
            <NavLink to="/rules" className={({ isActive }) => isActive ? 'active' : ''}>
              <span className="nav-icon">⚙️</span> Detection Rules
            </NavLink>
          </li>
        </ul>
      </div>

      <div className="sidebar-footer">
        <div className="sidebar-footer-info">
          <div className="status-dot" />
          <span className="status-text">Backend: http://localhost:8080</span>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
