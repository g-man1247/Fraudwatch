import { apiClient } from './client';

export const getTransactions = () => {
  return apiClient('/api/transactions');
};

export const createTransaction = (txData) => {
  return apiClient('/api/transactions', {
    method: 'POST',
    body: JSON.stringify(txData),
  });
};
