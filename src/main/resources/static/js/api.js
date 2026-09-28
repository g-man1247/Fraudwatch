/* ================================================================
   api.js — Shared API configuration and utility functions
   All pages include this file first.
   ================================================================ */

// Base URL — since Spring Boot serves the frontend on the same server,
// we use a relative path. Change only if running frontend separately.
const API_BASE = 'http://localhost:8080/api';

/* ── Generic fetch wrapper ──────────────────────────────────────── */

/**
 * GET request
 * @param {string} endpoint  e.g. '/industries'
 * @returns {Promise<any>}
 */
async function apiGet(endpoint) {
    const res = await fetch(API_BASE + endpoint);
    if (!res.ok) throw new Error(`GET ${endpoint} failed: ${res.status}`);
    return res.json();
}

/**
 * POST request
 * @param {string} endpoint
 * @param {object} body
 */
async function apiPost(endpoint, body) {
    const res = await fetch(API_BASE + endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!res.ok) {
        const text = await res.text();
        throw new Error(`POST ${endpoint} failed: ${res.status} — ${text}`);
    }
    return res.json();
}

/**
 * PUT request
 * @param {string} endpoint
 * @param {object|null} body  (null for endpoints like /pay that have no body)
 */
async function apiPut(endpoint, body = null) {
    const opts = { method: 'PUT', headers: {} };
    if (body !== null) {
        opts.headers['Content-Type'] = 'application/json';
        opts.body = JSON.stringify(body);
    }
    const res = await fetch(API_BASE + endpoint, opts);
    if (!res.ok) {
        const text = await res.text();
        throw new Error(`PUT ${endpoint} failed: ${res.status} — ${text}`);
    }
    return res.json();
}

/**
 * DELETE request
 * @param {string} endpoint
 */
async function apiDelete(endpoint) {
    const res = await fetch(API_BASE + endpoint, { method: 'DELETE' });
    if (!res.ok) throw new Error(`DELETE ${endpoint} failed: ${res.status}`);
    return res.text();
}

/* ── Utility helpers ────────────────────────────────────────────── */

/** Format a LocalDateTime string to a readable format */
function formatDate(dt) {
    if (!dt) return '—';
    return new Date(dt).toLocaleString('en-IN', {
        day: '2-digit', month: 'short', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

/** Return a badge HTML string for compliance status */
function complianceBadge(status) {
    const map = {
        'COMPLIANT':    'badge-success',
        'VIOLATION':    'badge-danger',
        'UNDER_REVIEW': 'badge-warning'
    };
    return `<span class="badge ${map[status] || 'badge-gray'}">${status || '—'}</span>`;
}

/** Return a badge HTML string for payment status */
function paymentBadge(status) {
    return status === 'PAID'
        ? `<span class="badge badge-success">PAID</span>`
        : `<span class="badge badge-warning">PENDING</span>`;
}

/** Return a badge HTML string for severity */
function severityBadge(sev) {
    const map = { 'HIGH': 'badge-danger', 'MEDIUM': 'badge-warning', 'LOW': 'badge-info' };
    return `<span class="badge ${map[sev] || 'badge-gray'}">${sev || '—'}</span>`;
}

/** Show an alert div */
function showAlert(id, message, type = 'success') {
    const el = document.getElementById(id);
    if (!el) return;
    el.className = `alert alert-${type} show`;
    el.textContent = message;
    setTimeout(() => { el.className = `alert alert-${type}`; }, 4000);
}

/** Populate a <select> with industry options */
async function populateIndustrySelect(selectId) {
    try {
        const industries = await apiGet('/industries');
        const sel = document.getElementById(selectId);
        if (!sel) return;
        sel.innerHTML = '<option value="">— Select Industry —</option>';
        industries.forEach(i => {
            sel.innerHTML += `<option value="${i.id}">${i.industryName}</option>`;
        });
    } catch (e) { console.error('Could not load industries for select:', e); }
}

/** Populate a <select> with station options */
async function populateStationSelect(selectId) {
    try {
        const stations = await apiGet('/stations');
        const sel = document.getElementById(selectId);
        if (!sel) return;
        sel.innerHTML = '<option value="">— Select Station —</option>';
        stations.forEach(s => {
            sel.innerHTML += `<option value="${s.id}">${s.stationName}</option>`;
        });
    } catch (e) { console.error('Could not load stations for select:', e); }
}
