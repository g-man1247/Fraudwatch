import { apiClient } from './client';

export const getDashboardSummary = () => {
  return apiClient('/api/dashboard/summary');
};
