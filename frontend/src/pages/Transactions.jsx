import React, { useState, useEffect } from 'react';
import { getTransactions, createTransaction } from '../api/transactions';
import ErrorBanner, { Loader, StatusBadge, formatCurrency, formatDate } from '../components/Shared';

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ sender: '', receiver: '', amount: '', type: 'IN' });
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState(null);
  const [success, setSuccess] = useState('');

  useEffect(() => { fetchTx(); }, []);

  const fetchTx = async () => {
    try {
      setLoading(true);
      setError(null);
      setTransactions(await getTransactions());
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async e => {
    e.preventDefault();
    setFormError(null);
    setSuccess('');
    setSubmitting(true);
    try {
      await createTransaction({ ...form, amount: parseFloat(form.amount) });
      setSuccess('Transaction submitted successfully.');
      setForm({ sender: '', receiver: '', amount: '', type: 'IN' });
      setShowForm(false);
      await fetchTx();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      {success && <div className="feedback-success" style={{ marginBottom: 16 }}>{success}</div>}

      <div className="panel">
        <div className="panel-header">
          <span className="panel-title">
            <span className="panel-title-icon" style={{ background: 'var(--color-primary-dim)', color: 'var(--color-primary)' }}>💳</span>
            All Transactions
          </span>
          <button className="btn btn-primary" onClick={() => setShowForm(s => !s)}>
            {showForm ? '✕ Cancel' : '+ New Transaction'}
          </button>
        </div>

        {showForm && (
          <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--color-border)', background: 'var(--color-surface-2)' }}>
            {formError && <div className="feedback-error">{formError}</div>}
            <form onSubmit={handleSubmit} className="form-grid">
              <div className="form-group">
                <label className="form-label">Transaction Type</label>
                <select className="form-select" name="type" value={form.type} onChange={handleChange} required>
                  <option value="IN">IN (Money Entering)</option>
                  <option value="OUT">OUT (Money Leaving)</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Amount (USD)</label>
                <input className="form-input" name="amount" type="number" step="0.01" min="0.01" value={form.amount} onChange={handleChange} required placeholder="0.00" />
              </div>
              <div className="form-group">
                <label className="form-label">Sender Account</label>
                <input className="form-input" name="sender" value={form.sender} onChange={handleChange} required placeholder="e.g. ACC-001" />
              </div>
              <div className="form-group">
                <label className="form-label">Receiver Account</label>
                <input className="form-input" name="receiver" value={form.receiver} onChange={handleChange} required placeholder="e.g. ACC-002" />
              </div>
              <div className="form-group" style={{ alignSelf: 'flex-end', gridColumn: '1 / -1' }}>
                <button type="submit" className="btn btn-primary" disabled={submitting}>
                  {submitting ? 'Submitting…' : '↗ Submit Transaction'}
                </button>
              </div>
            </form>
          </div>
        )}

        {loading ? <Loader text="Loading transactions…" /> : error ? <ErrorBanner message={error} /> : (
          transactions.length === 0 ? (
            <div style={{ padding: 20 }}>
              <div className="empty-state">
                <div className="empty-state-icon">📭</div>
                <div className="empty-state-title">No transactions found</div>
                <div className="empty-state-sub">Use the button above to submit the first transaction</div>
              </div>
            </div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Type</th>
                  <th>Sender</th>
                  <th>Receiver</th>
                  <th>Amount</th>
                  <th>Timestamp</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {[...transactions].sort((a, b) => b.id - a.id).map(tx => (
                  <tr key={tx.id}>
                    <td style={{ color: 'var(--color-muted)', fontSize: 12 }}>#{tx.id}</td>
                    <td>
                      {tx.type === 'IN' ? (
                        <span style={{ color: 'var(--color-success)', fontWeight: 600 }}>↑ IN</span>
                      ) : (
                        <span style={{ color: 'var(--color-danger)', fontWeight: 600 }}>↓ OUT</span>
                      )}
                    </td>
                    <td style={{ fontWeight: 500 }}>{tx.sender}</td>
                    <td>{tx.receiver}</td>
                    <td style={{ fontWeight: 600 }}>{formatCurrency(tx.amount)}</td>
                    <td style={{ color: 'var(--color-muted)', fontSize: 12 }}>{formatDate(tx.timestamp)}</td>
                    <td><StatusBadge status={tx.status} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )
        )}
      </div>
    </>
  );
};

export default Transactions;
