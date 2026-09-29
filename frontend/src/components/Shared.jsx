import React from 'react';

const ErrorBanner = ({ message }) => {
  if (!message) return null;
  return (
    <div className="error-banner">
      <span className="error-banner-icon">⚠️</span>
      <span>{message}</span>
    </div>
  );
};

export const Loader = ({ text = 'Loading...' }) => (
  <div className="loader-wrap">
    <div className="spinner" />
    <span>{text}</span>
  </div>
);

export const EmptyState = ({ icon = '📭', title, sub }) => (
  <div className="empty-state">
    <div className="empty-state-icon">{icon}</div>
    <div className="empty-state-title">{title}</div>
    {sub && <div className="empty-state-sub">{sub}</div>}
  </div>
);

export const StatusBadge = ({ status }) => {
  if (!status) return null;
  const cls = `badge badge-${status.toLowerCase()}`;
  return <span className={cls}>{status}</span>;
};

export const formatCurrency = (val) =>
  val !== null && val !== undefined
    ? `$${Number(val).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
    : '—';

export const formatDate = (str) => {
  if (!str) return '—';
  try {
    return new Intl.DateTimeFormat('en-US', {
      month: 'short', day: 'numeric', year: 'numeric',
      hour: '2-digit', minute: '2-digit',
    }).format(new Date(str));
  } catch { return str; }
};

export const timeAgo = (str) => {
  if (!str) return '';
  const diff = Date.now() - new Date(str).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return 'Just now';
  if (mins < 60) return `${mins}m ago`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24) return `${hrs}h ago`;
  return `${Math.floor(hrs / 24)}d ago`;
};

export default ErrorBanner;
