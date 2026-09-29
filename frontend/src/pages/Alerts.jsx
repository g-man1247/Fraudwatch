import React, { useState, useEffect } from 'react';
import { getFlaggedTransactions, reviewFlaggedTransaction } from '../api/flagged';
import ErrorBanner, { Loader, formatCurrency, formatDate } from '../components/Shared';

const outcomeColor = {
  APPROVED: 'var(--color-success)',
  BLOCKED: 'var(--color-danger)',
};

const Alerts = () => {
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [reviewing, setReviewing] = useState(null); // id of alert being reviewed
  const [reviewForm, setReviewForm] = useState({ outcome: 'APPROVED', reviewer: '', comments: '' });
  const [submitting, setSubmitting] = useState(false);
  const [reviewError, setReviewError] = useState(null);

  useEffect(() => { fetchAlerts(); }, []);

  const fetchAlerts = async () => {
    try {
      setLoading(true);
      setError(null);
      setAlerts(await getFlaggedTransactions());
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const openReview = (id) => {
    setReviewing(id);
    setReviewForm({ outcome: 'APPROVED', reviewer: '', comments: '' });
    setReviewError(null);
  };

  const submitReview = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setReviewError(null);
    try {
      await reviewFlaggedTransaction(reviewing, reviewForm);
      setReviewing(null);
      await fetchAlerts();
    } catch (err) {
      setReviewError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const pending = alerts.filter(a => a.reviewStatus === null);
  const reviewed = alerts.filter(a => a.reviewStatus !== null);

  return (
    <>
      {/* Review modal */}
      {reviewing && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.6)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 999
        }}>
          <div style={{
            background: 'var(--color-surface)', border: '1px solid var(--color-border)',
            borderRadius: 'var(--radius)', padding: 28, width: 420, maxWidth: '90vw'
          }}>
            <h3 style={{ marginBottom: 20, fontSize: 16 }}>Review Flagged Transaction #{reviewing}</h3>
            {reviewError && <div className="feedback-error">{reviewError}</div>}
            <form onSubmit={submitReview} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
              <div className="form-group">
                <label className="form-label">Decision</label>
                <select className="form-select" value={reviewForm.outcome} onChange={e => setReviewForm(f => ({ ...f, outcome: e.target.value }))}>
                  <option value="APPROVED">✅ Approve — Allow transaction</option>
                  <option value="BLOCKED">🚫 Block — Reject transaction</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Reviewer Name</label>
                <input className="form-input" value={reviewForm.reviewer} required onChange={e => setReviewForm(f => ({ ...f, reviewer: e.target.value }))} placeholder="Your name" />
              </div>
              <div className="form-group">
                <label className="form-label">Comments</label>
                <textarea className="form-textarea" rows={3} value={reviewForm.comments} onChange={e => setReviewForm(f => ({ ...f, comments: e.target.value }))} placeholder="Optional notes…" />
              </div>
              <div style={{ display: 'flex', gap: 10 }}>
                <button type="submit" className="btn btn-primary" disabled={submitting}>
                  {submitting ? 'Submitting…' : 'Submit Review'}
                </button>
                <button type="button" className="btn btn-ghost" onClick={() => setReviewing(null)}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Pending Alerts */}
      <div className="panel" style={{ marginBottom: 20 }}>
        <div className="panel-header">
          <span className="panel-title">
            <span className="panel-title-icon" style={{ background: 'var(--color-danger-dim)', color: 'var(--color-danger)' }}>🚨</span>
            Pending Alerts
            {pending.length > 0 && (
              <span className="nav-badge" style={{ marginLeft: 8 }}>{pending.length}</span>
            )}
          </span>
        </div>
        {loading ? <Loader text="Loading alerts…" /> : error ? <ErrorBanner message={error} /> : (
          pending.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">🛡️</div>
              <div className="empty-state-title">No pending alerts</div>
              <div className="empty-state-sub">All flagged transactions have been reviewed</div>
            </div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Flag #</th>
                  <th>Transaction</th>
                  <th>Rule Triggered</th>
                  <th>Amount</th>
                  <th>Flagged At</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {pending.map(al => (
                  <tr key={al.id}>
                    <td style={{ color: 'var(--color-muted)', fontSize: 12 }}>#{al.id}</td>
                    <td>
                      <div style={{ fontWeight: 500 }}>{al.transaction?.sender} → {al.transaction?.receiver}</div>
                      <div style={{ fontSize: 11, color: 'var(--color-muted)' }}>TX #{al.transaction?.id}</div>
                    </td>
                    <td>
                      <span style={{
                        background: 'var(--color-warning-dim)', color: 'var(--color-warning)',
                        padding: '2px 8px', borderRadius: 20, fontSize: 11, fontWeight: 600
                      }}>
                        {al.rule?.name}
                      </span>
                    </td>
                    <td style={{ fontWeight: 600 }}>{formatCurrency(al.transaction?.amount)}</td>
                    <td style={{ color: 'var(--color-muted)', fontSize: 12 }}>{formatDate(al.flaggedAt)}</td>
                    <td>
                      <button className="btn btn-primary" style={{ fontSize: 12, padding: '5px 12px' }} onClick={() => openReview(al.id)}>
                        Review
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )
        )}
      </div>

      {/* Reviewed */}
      {reviewed.length > 0 && (
        <div className="panel">
          <div className="panel-header">
            <span className="panel-title">
              <span className="panel-title-icon" style={{ background: 'var(--color-success-dim)', color: 'var(--color-success)' }}>✅</span>
              Reviewed Alerts
            </span>
          </div>
          <table className="data-table">
            <thead>
              <tr>
                <th>Flag #</th>
                <th>Transaction</th>
                <th>Rule</th>
                <th>Amount</th>
                <th>Decision</th>
              </tr>
            </thead>
            <tbody>
              {reviewed.map(al => (
                <tr key={al.id}>
                  <td style={{ color: 'var(--color-muted)', fontSize: 12 }}>#{al.id}</td>
                  <td>
                    <div style={{ fontWeight: 500 }}>{al.transaction?.sender} → {al.transaction?.receiver}</div>
                    <div style={{ fontSize: 11, color: 'var(--color-muted)' }}>TX #{al.transaction?.id}</div>
                  </td>
                  <td style={{ fontSize: 12, color: 'var(--color-muted)' }}>{al.rule?.name}</td>
                  <td style={{ fontWeight: 600 }}>{formatCurrency(al.transaction?.amount)}</td>
                  <td>
                    <span style={{
                      color: outcomeColor[al.reviewStatus] || 'var(--color-muted)',
                      fontWeight: 600, fontSize: 13
                    }}>
                      {al.reviewStatus === 'APPROVED' ? '✅ Approved' : '🚫 Blocked'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
};

export default Alerts;
