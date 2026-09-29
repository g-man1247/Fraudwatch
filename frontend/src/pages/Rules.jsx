import React, { useState, useEffect } from 'react';
import { getRules, createRule } from '../api/rules';
import ErrorBanner, { Loader } from '../components/Shared';

const ruleTypeLabel = { LARGE_AMOUNT: '💰 Large Amount', HIGH_FREQUENCY: '⚡ High Frequency' };

const Rules = () => {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    name: '', type: 'LARGE_AMOUNT', enabled: true,
    thresholdAmount: '', transactionCount: '', timeWindowMinutes: '', description: ''
  });
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState(null);
  const [success, setSuccess] = useState('');

  useEffect(() => { fetchRules(); }, []);

  const fetchRules = async () => {
    try {
      setLoading(true); setError(null);
      setRules(await getRules());
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  const handleChange = e => {
    const { name, value, type, checked } = e.target;
    setForm(f => ({ ...f, [name]: type === 'checkbox' ? checked : value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setFormError(null); setSuccess('');
    setSubmitting(true);
    try {
      const payload = { ...form };
      if (payload.type === 'LARGE_AMOUNT') {
        payload.transactionCount = null; payload.timeWindowMinutes = null;
        payload.thresholdAmount = parseFloat(payload.thresholdAmount);
      } else {
        payload.thresholdAmount = null;
        payload.transactionCount = parseInt(payload.transactionCount, 10);
        payload.timeWindowMinutes = parseInt(payload.timeWindowMinutes, 10);
      }
      await createRule(payload);
      setSuccess('Rule created successfully!');
      setForm({ name: '', type: 'LARGE_AMOUNT', enabled: true, thresholdAmount: '', transactionCount: '', timeWindowMinutes: '', description: '' });
      setShowForm(false);
      await fetchRules();
    } catch (err) { setFormError(err.message); }
    finally { setSubmitting(false); }
  };

  return (
    <>
      {success && <div className="feedback-success" style={{ marginBottom: 16 }}>{success}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: 20, alignItems: 'flex-start' }}>

        {/* Rules List */}
        <div className="panel">
          <div className="panel-header">
            <span className="panel-title">
              <span className="panel-title-icon" style={{ background: 'var(--color-primary-dim)', color: 'var(--color-primary)' }}>⚙️</span>
              Detection Rules
            </span>
            <button className="btn btn-primary" onClick={() => setShowForm(s => !s)}>
              {showForm ? '✕ Cancel' : '+ New Rule'}
            </button>
          </div>

          {loading ? <Loader text="Loading rules…" /> : error ? <ErrorBanner message={error} /> : (
            rules.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon">⚙️</div>
                <div className="empty-state-title">No rules configured</div>
                <div className="empty-state-sub">Create a rule to start detecting suspicious transactions</div>
              </div>
            ) : (
              rules.map(rule => (
                <div key={rule.id} className="rule-card">
                  <div className={`rule-icon`} style={
                    rule.type === 'HIGH_FREQUENCY'
                      ? { background: 'var(--color-warning-dim)', color: 'var(--color-warning)' }
                      : {}
                  }>
                    {rule.type === 'LARGE_AMOUNT' ? '💰' : '⚡'}
                  </div>
                  <div className="rule-info">
                    <div className="rule-name">{rule.name}</div>
                    <div className="rule-desc">
                      {rule.type === 'LARGE_AMOUNT'
                        ? `Flags amounts above $${Number(rule.thresholdAmount).toLocaleString()}`
                        : `Flags ${rule.transactionCount} txns within ${rule.timeWindowMinutes} min`}
                      {rule.description ? ` — ${rule.description}` : ''}
                    </div>
                  </div>
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 6 }}>
                    <span className={`badge ${rule.enabled ? 'badge-active' : 'badge-disabled'}`}>
                      {rule.enabled ? 'Active' : 'Disabled'}
                    </span>
                    <span style={{ fontSize: 11, color: 'var(--color-muted)' }}>
                      {ruleTypeLabel[rule.type]}
                    </span>
                  </div>
                </div>
              ))
            )
          )}
        </div>

        {/* Create Rule Form */}
        {showForm && (
          <div className="panel">
            <div className="panel-header">
              <span className="panel-title">New Detection Rule</span>
            </div>
            <div className="panel-body">
              {formError && <div className="feedback-error">{formError}</div>}
              <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                <div className="form-group">
                  <label className="form-label">Rule Name</label>
                  <input className="form-input" name="name" value={form.name} onChange={handleChange} required placeholder="e.g. High Value Alert" />
                </div>
                <div className="form-group">
                  <label className="form-label">Rule Type</label>
                  <select className="form-select" name="type" value={form.type} onChange={handleChange}>
                    <option value="LARGE_AMOUNT">💰 Large Amount</option>
                    <option value="HIGH_FREQUENCY">⚡ High Frequency</option>
                  </select>
                </div>
                {form.type === 'LARGE_AMOUNT' && (
                  <div className="form-group">
                    <label className="form-label">Threshold Amount ($)</label>
                    <input className="form-input" type="number" step="0.01" name="thresholdAmount" value={form.thresholdAmount} onChange={handleChange} required placeholder="e.g. 10000" />
                  </div>
                )}
                {form.type === 'HIGH_FREQUENCY' && (
                  <>
                    <div className="form-group">
                      <label className="form-label">Transaction Count</label>
                      <input className="form-input" type="number" min="1" name="transactionCount" value={form.transactionCount} onChange={handleChange} required placeholder="e.g. 5" />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Time Window (minutes)</label>
                      <input className="form-input" type="number" min="1" name="timeWindowMinutes" value={form.timeWindowMinutes} onChange={handleChange} required placeholder="e.g. 10" />
                    </div>
                  </>
                )}
                <div className="form-group">
                  <label className="form-label">Description (optional)</label>
                  <textarea className="form-textarea" name="description" value={form.description} onChange={handleChange} rows={2} placeholder="What does this rule detect?" />
                </div>
                <label className="form-checkbox-row">
                  <input type="checkbox" name="enabled" checked={form.enabled} onChange={handleChange} />
                  Enable rule immediately
                </label>
                <button type="submit" className="btn btn-primary" disabled={submitting}>
                  {submitting ? 'Creating…' : '+ Create Rule'}
                </button>
              </form>
            </div>
          </div>
        )}

        {/* Info panel when form is hidden */}
        {!showForm && (
          <div className="panel">
            <div className="panel-header">
              <span className="panel-title">Rule Types</span>
            </div>
            <div className="panel-body" style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              <div style={{ padding: '14px', background: 'var(--color-surface-2)', borderRadius: 'var(--radius-sm)', borderLeft: '3px solid var(--color-primary)' }}>
                <div style={{ fontWeight: 600, marginBottom: 4, fontSize: 13 }}>💰 Large Amount</div>
                <div style={{ fontSize: 12, color: 'var(--color-muted)', lineHeight: 1.6 }}>
                  Flags any single transaction where the amount exceeds a configurable dollar threshold.
                </div>
              </div>
              <div style={{ padding: '14px', background: 'var(--color-surface-2)', borderRadius: 'var(--radius-sm)', borderLeft: '3px solid var(--color-warning)' }}>
                <div style={{ fontWeight: 600, marginBottom: 4, fontSize: 13 }}>⚡ High Frequency</div>
                <div style={{ fontSize: 12, color: 'var(--color-muted)', lineHeight: 1.6 }}>
                  Flags accounts that send more than N transactions within a specified time window — detecting rapid-fire fraud patterns.
                </div>
              </div>
              <button className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }} onClick={() => setShowForm(true)}>
                + Create New Rule
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Responsive fallback for form */}
      <style>{`
        @media (max-width: 768px) {
          .rules-layout { grid-template-columns: 1fr !important; }
        }
      `}</style>
    </>
  );
};

export default Rules;
