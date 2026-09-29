import React, { useState, useEffect } from 'react';
import { getDashboardSummary } from '../api/dashboard';
import { getTransactions } from '../api/transactions';
import { getFlaggedTransactions } from '../api/flagged';
import ErrorBanner, { Loader, StatusBadge, formatCurrency, formatDate, timeAgo } from '../components/Shared';

/* ---- Donut SVG Chart ---- */
const DonutChart = ({ data }) => {
  const total = data.reduce((s, d) => s + d.value, 0);
  if (total === 0) {
    return (
      <div className="donut-wrap" style={{ justifyContent: 'center', flexDirection: 'column', gap: 8 }}>
        <svg viewBox="0 0 100 100" width={100} height={100}>
          <circle cx="50" cy="50" r="38" fill="none" stroke="#2e3350" strokeWidth="16" />
        </svg>
        <span style={{ fontSize: 12, color: 'var(--color-muted)' }}>No data yet</span>
      </div>
    );
  }

  const colors = ['#4f8cff', '#3fcf8e', '#ffb347', '#ff4f6d'];
  let offset = 0;
  const r = 38;
  const circum = 2 * Math.PI * r;

  return (
    <div className="donut-wrap">
      <svg viewBox="0 0 100 100" width={110} height={110} style={{ transform: 'rotate(-90deg)', flexShrink: 0 }}>
        <circle cx="50" cy="50" r={r} fill="none" stroke="#2e3350" strokeWidth="15" />
        {data.map((d, i) => {
          const pct = d.value / total;
          const dash = pct * circum;
          const el = (
            <circle
              key={i}
              cx="50" cy="50" r={r}
              fill="none"
              stroke={colors[i % colors.length]}
              strokeWidth="15"
              strokeDasharray={`${dash} ${circum - dash}`}
              strokeDashoffset={-offset}
            />
          );
          offset += dash;
          return el;
        })}
        <text x="50" y="54" textAnchor="middle"
          style={{ fill: 'var(--color-text)', fontSize: 18, fontWeight: 700, transform: 'rotate(90deg)', transformOrigin: '50% 50%' }}>
          {total}
        </text>
      </svg>
      <div className="donut-legend">
        {data.map((d, i) => (
          <div key={i} className="donut-legend-item">
            <div className="donut-legend-dot" style={{ background: colors[i % colors.length] }} />
            <span>{d.label}</span>
            <span className="donut-legend-pct">{total ? Math.round((d.value / total) * 100) : 0}%</span>
          </div>
        ))}
      </div>
    </div>
  );
};

/* ---- Mini sparkline ---- */
const SparkBar = ({ values, color }) => {
  const max = Math.max(...values, 1);
  return (
    <div style={{ display: 'flex', alignItems: 'flex-end', gap: 3, height: 36 }}>
      {values.map((v, i) => (
        <div key={i} style={{
          flex: 1,
          borderRadius: 2,
          background: color,
          height: `${(v / max) * 100}%`,
          minHeight: 3,
          opacity: i === values.length - 1 ? 1 : 0.4 + (i / values.length) * 0.5,
        }} />
      ))}
    </div>
  );
};

const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        setError(null);
        const [s, tx, fl] = await Promise.all([
          getDashboardSummary(),
          getTransactions(),
          getFlaggedTransactions(),
        ]);
        setStats(s);
        setTransactions(tx);
        setAlerts(fl);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) return <Loader text="Loading dashboard…" />;
  if (error) return <ErrorBanner message={error} />;

  // Build donut data
  const donutData = [
    { label: 'Completed', value: stats?.completedTransactions ?? 0 },
    { label: 'Pending Reviews', value: stats?.pendingReviews ?? 0 },
    { label: 'Flagged', value: stats?.flaggedTransactions ?? 0 },
    { label: 'Blocked', value: stats?.blockedTransactions ?? 0 },
  ].filter(d => d.value > 0);

  // Recent transactions (last 6)
  const recent = [...transactions]
    .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
    .slice(0, 6);

  // Recent alerts (last 5)
  const recentAlerts = [...alerts]
    .sort((a, b) => new Date(b.flaggedAt) - new Date(a.flaggedAt))
    .slice(0, 5);

  // Sparkline data – daily totals (mock distribution from total)
  const sparkTotal   = [2,3,1,4,2,5, stats?.totalTransactions ?? 0].slice(-7);
  const sparkFlagged = [0,1,0,2,1,2, stats?.flaggedTransactions ?? 0].slice(-7);

  return (
    <>
      {/* Stats Grid */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-label">Total Transactions</span>
            <div className="stat-card-icon blue">💳</div>
          </div>
          <div className="stat-card-value">{stats?.totalTransactions ?? 0}</div>
          <SparkBar values={sparkTotal} color="var(--color-primary)" />
          <div className="stat-card-sub" style={{ marginTop: 8 }}>All time</div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-label">Money In</span>
            <div className="stat-card-icon green">↑</div>
          </div>
          <div className="stat-card-value" style={{ color: 'var(--color-success)' }}>
            {formatCurrency(stats?.totalMoneyIn ?? 0)}
          </div>
          <div className="stat-card-sub" style={{ marginTop: 28 }}>Total received (IN)</div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-label">Money Out</span>
            <div className="stat-card-icon red">↓</div>
          </div>
          <div className="stat-card-value" style={{ color: 'var(--color-danger)' }}>
            {formatCurrency(stats?.totalMoneyOut ?? 0)}
          </div>
          <div className="stat-card-sub" style={{ marginTop: 28 }}>Total sent (OUT)</div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-label">Flagged</span>
            <div className="stat-card-icon orange">🚩</div>
          </div>
          <div className="stat-card-value" style={{ color: 'var(--color-warning)' }}>
            {stats?.flaggedTransactions ?? 0}
          </div>
          <SparkBar values={sparkFlagged} color="var(--color-warning)" />
          <div className="stat-card-sub" style={{ marginTop: 8 }}>Awaiting review</div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-label">Blocked</span>
            <div className="stat-card-icon red">🚫</div>
          </div>
          <div className="stat-card-value" style={{ color: 'var(--color-danger)' }}>
            {stats?.blockedTransactions ?? 0}
          </div>
          <div className="stat-card-sub" style={{ marginTop: 28 }}>Rejected after review</div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-label">Pending Reviews</span>
            <div className="stat-card-icon purple">⏳</div>
          </div>
          <div className="stat-card-value" style={{ color: '#7c6fff' }}>
            {stats?.pendingReviews ?? 0}
          </div>
          <div className="stat-card-sub" style={{ marginTop: 28 }}>Require analyst action</div>
        </div>
      </div>

      {/* Content Grid: Transactions + Donut + Alerts */}
      <div className="content-grid">
        {/* Recent Transactions */}
        <div className="panel">
          <div className="panel-header">
            <span className="panel-title">
              <span className="panel-title-icon" style={{ background: 'var(--color-primary-dim)', color: 'var(--color-primary)' }}>💳</span>
              Recent Transactions
            </span>
          </div>
          {recent.length === 0 ? (
            <div style={{ padding: '0 0 8px' }}>
              <div className="empty-state">
                <div className="empty-state-icon">📭</div>
                <div className="empty-state-title">No transactions yet</div>
                <div className="empty-state-sub">Transactions you create will appear here</div>
              </div>
            </div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Type</th>
                  <th>Sender → Receiver</th>
                  <th>Amount</th>
                  <th>Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {recent.map(tx => (
                  <tr key={tx.id}>
                    <td style={{ color: 'var(--color-muted)', fontSize: 12 }}>#{tx.id}</td>
                    <td>
                      {tx.type === 'IN' ? (
                        <span style={{ color: 'var(--color-success)', fontWeight: 600 }}>↑ IN</span>
                      ) : (
                        <span style={{ color: 'var(--color-danger)', fontWeight: 600 }}>↓ OUT</span>
                      )}
                    </td>
                    <td>
                      <span style={{ fontWeight: 500 }}>{tx.sender}</span>
                      <span style={{ color: 'var(--color-muted)', margin: '0 6px' }}>→</span>
                      <span>{tx.receiver}</span>
                    </td>
                    <td style={{ fontWeight: 600 }}>{formatCurrency(tx.amount)}</td>
                    <td style={{ color: 'var(--color-muted)', fontSize: 12 }}>{formatDate(tx.timestamp)}</td>
                    <td><StatusBadge status={tx.status} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* Right Column: Donut + Alerts */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          
          {/* Transaction Flow */}
          <div className="panel">
            <div className="panel-header">
              <span className="panel-title">
                <span className="panel-title-icon" style={{ background: 'rgba(63, 207, 142, 0.12)', color: 'var(--color-success)' }}>🌊</span>
                Transaction Flow
              </span>
            </div>
            <div className="panel-body">
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, marginBottom: 4 }}>
                    <span style={{ color: 'var(--color-text)' }}>IN</span>
                    <span style={{ color: 'var(--color-success)', fontWeight: 600 }}>{formatCurrency(stats?.totalMoneyIn ?? 0)}</span>
                  </div>
                  <div style={{ width: '100%', height: 8, background: 'var(--color-surface-2)', borderRadius: 4, overflow: 'hidden' }}>
                    <div style={{ width: `${(stats?.totalMoneyIn || 0) / (Math.max(stats?.totalMoneyIn + stats?.totalMoneyOut, 1)) * 100}%`, height: '100%', background: 'var(--color-success)' }} />
                  </div>
                </div>
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, marginBottom: 4 }}>
                    <span style={{ color: 'var(--color-text)' }}>OUT</span>
                    <span style={{ color: 'var(--color-danger)', fontWeight: 600 }}>{formatCurrency(stats?.totalMoneyOut ?? 0)}</span>
                  </div>
                  <div style={{ width: '100%', height: 8, background: 'var(--color-surface-2)', borderRadius: 4, overflow: 'hidden' }}>
                    <div style={{ width: `${(stats?.totalMoneyOut || 0) / (Math.max(stats?.totalMoneyIn + stats?.totalMoneyOut, 1)) * 100}%`, height: '100%', background: 'var(--color-danger)' }} />
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Status breakdown */}
          <div className="panel">
            <div className="panel-header">
              <span className="panel-title">
                <span className="panel-title-icon" style={{ background: 'rgba(124,111,255,0.12)', color: '#7c6fff' }}>🥧</span>
                Status Breakdown
              </span>
            </div>
            <div className="panel-body">
              {(stats?.totalTransactions ?? 0) === 0 ? (
                <div style={{ textAlign: 'center', color: 'var(--color-muted)', fontSize: 13, padding: '10px 0' }}>
                  No transactions to display
                </div>
              ) : (
                <DonutChart data={donutData} />
              )}
            </div>
          </div>

          {/* Fraud Alerts */}
          <div className="panel">
            <div className="panel-header">
              <span className="panel-title">
                <span className="panel-title-icon" style={{ background: 'var(--color-danger-dim)', color: 'var(--color-danger)' }}>🚨</span>
                Recent Alerts
              </span>
            </div>
            {recentAlerts.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon">🛡️</div>
                <div className="empty-state-title">No alerts</div>
                <div className="empty-state-sub">All clear</div>
              </div>
            ) : (
              recentAlerts.map(al => (
                <div key={al.id} className="alert-item">
                  <div className={`alert-icon ${al.reviewStatus === null ? '' : 'warning'}`}>
                    {al.reviewStatus === null ? '🚨' : '⚠️'}
                  </div>
                  <div className="alert-content">
                    <div className="alert-title">
                      ${al.transaction?.amount} — {al.transaction?.sender} → {al.transaction?.receiver}
                    </div>
                    <div className="alert-sub">
                      Rule: {al.rule?.name} · {al.reviewStatus ?? 'Pending review'}
                    </div>
                  </div>
                  <div className="alert-time">{timeAgo(al.flaggedAt)}</div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default Dashboard;
