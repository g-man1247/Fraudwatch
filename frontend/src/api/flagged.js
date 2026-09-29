import { apiClient } from './client';

export const getFlaggedTransactions = () => {
  return apiClient('/api/flagged-transactions');
};

export const reviewFlaggedTransaction = (id, reviewData) => {
  return apiClient(`/api/flagged-transactions/${id}/review`, {
    method: 'PUT',
    body: JSON.stringify(reviewData),
  });
};
